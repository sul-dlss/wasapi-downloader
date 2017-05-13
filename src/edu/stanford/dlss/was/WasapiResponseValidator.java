package edu.stanford.dlss.was;

import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;

public class WasapiResponseValidator {

  public static boolean validateResponse(StatusLine statusLine, boolean entityIsNull) throws ClientProtocolException, HttpResponseException {
    if(statusLine.getStatusCode() >= 300) {
      throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
    }
    if(entityIsNull) {
      throw new ClientProtocolException("Response contains no content");
    }
    return true;
  }
}
