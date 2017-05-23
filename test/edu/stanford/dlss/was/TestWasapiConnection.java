package edu.stanford.dlss.was;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;

import org.junit.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentMatchers;

public class TestWasapiConnection {

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
  public void downloadQueryCallsExecute() throws IOException {
    WasapiClient mockClient = mock(WasapiClient.class);
    WasapiConnection testConnection = new WasapiConnection(mockClient);
    testConnection.downloadQuery(JSON_QUERY, OUTPUT_PATH);

    verify(mockClient, times(1)).execute(ArgumentMatchers.<HttpGet>any(HttpGet.class),
                                         ArgumentMatchers.<DownloadResponseHandler>any(DownloadResponseHandler.class));
  }
}
