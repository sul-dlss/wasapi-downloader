package edu.stanford.dlss.was;

import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.message.BasicStatusLine;

import org.junit.*;
import static org.junit.Assert.*;

public class TestWasapiResponseValidator {

  private StatusLine validStatusLine = new BasicStatusLine(new ProtocolVersion("HTTP 1/1", 1, 1), 200, "OK");
  private StatusLine invalidStatusLine = new BasicStatusLine(new ProtocolVersion("HTTP 1/1", 1, 1), 300, "Not Defined");

  @Test(expected = ClientProtocolException.class)
  public void testNullEntity() throws ClientProtocolException, HttpResponseException {
    WasapiResponseValidator.validateResponse(validStatusLine, true);
  }

  @Test(expected = HttpResponseException.class)
  public void testWrongResponseCode() throws ClientProtocolException, HttpResponseException {
    WasapiResponseValidator.validateResponse(invalidStatusLine, false);
  }

  @Test
  public void testValidResponse() throws ClientProtocolException, HttpResponseException {
    assertTrue(WasapiResponseValidator.validateResponse(validStatusLine, false));
  }
}
