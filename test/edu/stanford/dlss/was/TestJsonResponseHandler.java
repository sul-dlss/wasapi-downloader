package edu.stanford.dlss.was;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicStatusLine;

import org.junit.*;
import static org.junit.Assert.*;
import org.mockito.*;
import org.mockito.Mockito.*;
import org.mockito.Matchers.*;

public class TestJsonResponseHandler {

  private static final char SEP = File.separatorChar;
  private static final StatusLine validStatusLine = new BasicStatusLine(new ProtocolVersion("HTTP 1/1", 1, 1), 200, "OK");

  @Test(expected=ClientProtocolException.class)
  public void nullEntityThrowsException() throws ClientProtocolException, HttpResponseException, IOException {
    JsonResponseHandler handler = new JsonResponseHandler();
    HttpResponse mockResponse = Mockito.mock(HttpResponse.class);
    Mockito.when(mockResponse.getEntity()).thenReturn(null);
    Mockito.when(mockResponse.getStatusLine()).thenReturn(validStatusLine);

    handler.handleResponse(mockResponse);
  }


  @Test
  public void validResponseParsesCorrectly() throws ClientProtocolException, HttpResponseException, IOException {
    JsonResponseHandler handler = new JsonResponseHandler();
    HttpResponse mockResponse = Mockito.mock(HttpResponse.class);
    HttpEntity mockEntity = Mockito.mock(HttpEntity.class);
    Mockito.when(mockResponse.getEntity()).thenReturn(mockEntity);
    Mockito.when(mockResponse.getStatusLine()).thenReturn(validStatusLine);
    Mockito.when(mockEntity.getContent()).thenReturn(new FileInputStream(new File("test" + SEP + "fixtures" + SEP + "webdata_crawl_mult_files_response.json")));

    WasapiResponse parsedResponse = handler.handleResponse(mockResponse);
    assertEquals(parsedResponse.getCount(), 5);
  }
}
