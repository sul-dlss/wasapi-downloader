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
  private static final String outputDirectory = new String("test" + SEP + "tmp");
  private static final String outputFilePath = new String(outputDirectory + SEP + "testDownloadResponseHandler.output");
  private static final StatusLine validStatusLine = new BasicStatusLine(new ProtocolVersion("HTTP 1/1", 1, 1), 200, "OK");

  private void writeToFile() throws IOException {
    new File(outputFilePath).createNewFile();
    return;
  }


  @Before
  public void setUp() {
    new File(outputDirectory).mkdir();
  }


  @After
  public void tearDown() {
    new File(outputFilePath).delete();
    new File(outputDirectory).delete();
  }


  @Test(expected=ClientProtocolException.class)
  public void nullEntityThrowsException() throws ClientProtocolException, HttpResponseException, IOException {
    DownloadResponseHandler handler = new DownloadResponseHandler("/dev/null");
    HttpResponse mockResponse = Mockito.mock(HttpResponse.class);
    Mockito.when(mockResponse.getEntity()).thenReturn(null);
    Mockito.when(mockResponse.getStatusLine()).thenReturn(validStatusLine);

    handler.handleResponse(mockResponse);
  }


  @Test
  public void validResponseDownloadsFile() throws ClientProtocolException, HttpResponseException, IOException {
    DownloadResponseHandler handler = new DownloadResponseHandler(outputFilePath);
    HttpResponse mockResponse = Mockito.mock(HttpResponse.class);
    HttpEntity mockEntity = Mockito.mock(HttpEntity.class);
    Mockito.when(mockResponse.getEntity()).thenReturn(mockEntity);
    Mockito.when(mockResponse.getStatusLine()).thenReturn(validStatusLine);

    Mockito.doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) throws IOException {
        new File(outputFilePath).createNewFile();
        return null;
        // Object[] args = invocation.getArguments();
        // System.out.println("called with arguments: " + Arrays.toString(args));
        // return null;
      }
    }).when(mockEntity).writeTo(Matchers.<FileOutputStream>any());

    boolean returnValue = handler.handleResponse(mockResponse);
    assertEquals(returnValue, true);
    assertEquals(new File(outputFilePath).exists(), true);
  }
}
