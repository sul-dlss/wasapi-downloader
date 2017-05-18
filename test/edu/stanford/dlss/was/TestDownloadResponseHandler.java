package edu.stanford.dlss.was;

import java.io.File;
import java.io.FileOutputStream;
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
import org.mockito.invocation.*;
import org.mockito.stubbing.*;



public class TestDownloadResponseHandler {
  private static final char SEP = File.separatorChar;
  private static final String OUTPUT_DIRECTORY = new String("test" + SEP + "tmp");
  private static final String OUTPUT_FILE_PATH = new String(OUTPUT_DIRECTORY + SEP + "testDownloadResponseHandler.output");
  private static final StatusLine VALID_STATUS_LINE = new BasicStatusLine(new ProtocolVersion("HTTP 1/1", 1, 1), 200, "OK");

  private void writeToFile() throws IOException {
    new File(OUTPUT_FILE_PATH).createNewFile();
    return;
  }


  @Before
  public void setUp() {
    new File(OUTPUT_DIRECTORY).mkdir();
  }


  @After
  public void tearDown() {
    new File(OUTPUT_FILE_PATH).delete();
    new File(OUTPUT_DIRECTORY).delete();
  }


  @Test(expected = ClientProtocolException.class)
  public void nullEntityThrowsException() throws ClientProtocolException, HttpResponseException, IOException {
    DownloadResponseHandler handler = new DownloadResponseHandler("/dev/null");
    HttpResponse mockResponse = Mockito.mock(HttpResponse.class);
    Mockito.when(mockResponse.getEntity()).thenReturn(null);
    Mockito.when(mockResponse.getStatusLine()).thenReturn(VALID_STATUS_LINE);

    handler.handleResponse(mockResponse);
  }


  @Test
  public void validResponseDownloadsFile() throws ClientProtocolException, HttpResponseException, IOException {
    DownloadResponseHandler handler = new DownloadResponseHandler(OUTPUT_FILE_PATH);
    HttpResponse mockResponse = Mockito.mock(HttpResponse.class);
    HttpEntity mockEntity = Mockito.mock(HttpEntity.class);
    Mockito.when(mockResponse.getEntity()).thenReturn(mockEntity);
    Mockito.when(mockResponse.getStatusLine()).thenReturn(VALID_STATUS_LINE);

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) throws IOException {
        new File(OUTPUT_FILE_PATH).createNewFile();
        return null;
      }
    }).when(mockEntity).writeTo(Matchers.<FileOutputStream>any());

    boolean returnValue = handler.handleResponse(mockResponse);
    assertEquals(returnValue, true);
    assertEquals(new File(OUTPUT_FILE_PATH).exists(), true);
  }
}
