package edu.stanford.dlss.was;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.methods.HttpGet;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentMatchers;

public class TestWasapiConnection {
  private static final String ORIG_QUERY_URL = "https://example.org/query";
  private static final String NEXT_URL = "https://example.org/query?page=2";

  private static final String JSON_QUERY = "http://example.com/example.json";
  // private static final String DOWNLOAD_QUERY = "http://example.com/download?file=example.warc";
  private static final String OUTPUT_PATH = "/dev/null";

  @Test
  public void constructorCallsLogin() throws IOException {
    WasapiClient mockClient = mock(WasapiClient.class);
    new WasapiConnection(mockClient);

    verify(mockClient, times(1)).login();
  }

  @Test
  public void jsonQueryCallsExecute() throws IOException {
    WasapiClient mockClient = mock(WasapiClient.class);
    WasapiConnection testConnection = new WasapiConnection(mockClient);
    testConnection.jsonQuery(JSON_QUERY);

    verify(mockClient, times(1)).execute(ArgumentMatchers.<HttpGet>any(HttpGet.class),
                                         ArgumentMatchers.<JsonResponseHandler>any(JsonResponseHandler.class));
  }

  @Test
  public void jsonQuery_handlesNullRequestUrl() throws IOException {
    WasapiClient mockClient = mock(WasapiClient.class);
    WasapiConnection testConnection = new WasapiConnection(mockClient);
    assertNull("jsonQuery should just return a null response if requestURL is null", testConnection.jsonQuery(null));
  }

  @Test
  public void pagedJsonQuery_loopsThroughAndReturnsResponseList() throws IOException {
    WasapiClient mockClient = mock(WasapiClient.class);
    WasapiConnection spyConnection = spy(new WasapiConnection(mockClient));

    WasapiResponse[] responses = {
      mockResponseNotLast(),
      mockResponseLast()
    };
    doReturn(responses[0]).when(spyConnection).jsonQuery(ORIG_QUERY_URL);
    doReturn(responses[1]).when(spyConnection).jsonQuery(NEXT_URL);

    List<WasapiResponse> respList = spyConnection.pagedJsonQuery(ORIG_QUERY_URL);
    assertEquals("response list should have 2 entries", 2, respList.size());
    assertEquals("response list's first entry should be first response", responses[0], respList.get(0));
    assertEquals("response list's second entry should be second response", responses[1], respList.get(1));
  }

  @Test
  public void pagedJsonQuery_handlesSinglePageResponse() throws IOException {
    WasapiClient mockClient = mock(WasapiClient.class);
    WasapiConnection spyConnection = spy(new WasapiConnection(mockClient));
    WasapiResponse mockResponse = mockResponseLast();

    doReturn(mockResponse).when(spyConnection).jsonQuery(ORIG_QUERY_URL);

    List<WasapiResponse> respList = spyConnection.pagedJsonQuery(ORIG_QUERY_URL);
    assertEquals("response list should have 1 entry", 1, respList.size());
    assertEquals("response list's only entry should be mockResponse", mockResponse, respList.get(0));
  }

  @Test
  public void pagedJsonQuery_handlesNullResponse() throws IOException {
    WasapiClient mockClient = mock(WasapiClient.class);
    WasapiConnection spyConnection = spy(new WasapiConnection(mockClient));

    doReturn(null).when(spyConnection).jsonQuery(ORIG_QUERY_URL);
    List<WasapiResponse> respList = spyConnection.pagedJsonQuery(ORIG_QUERY_URL);
    assertEquals("response list should be empty", 0, respList.size());
  }

  @Test
  public void downloadQueryCallsExecute() throws IOException {
    WasapiClient mockClient = mock(WasapiClient.class);
    WasapiConnection testConnection = new WasapiConnection(mockClient);
    testConnection.downloadQuery(JSON_QUERY, OUTPUT_PATH);

    verify(mockClient, times(1)).execute(ArgumentMatchers.<HttpGet>any(HttpGet.class),
                                         ArgumentMatchers.<DownloadResponseHandler>any(DownloadResponseHandler.class));
  }


  private WasapiResponse mockResponseNotLast() {
    WasapiResponse mockResp = mock(WasapiResponse.class);
    doReturn(NEXT_URL).when(mockResp).getNext();
    return mockResp;
  }

  private WasapiResponse mockResponseLast() {
    WasapiResponse mockResp = mock(WasapiResponse.class);
    doReturn(null).when(mockResp).getNext();
    return mockResp;
  }
}
