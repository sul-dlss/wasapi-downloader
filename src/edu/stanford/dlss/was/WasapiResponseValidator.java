package edu.stanford.dlss.was;

import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class WasapiResponseValidator {

  private static final int STATUS_CODE_THRESHOLD = 300;

  public static boolean validateResponse(StatusLine statusLine, boolean entityIsNull) throws ClientProtocolException, HttpResponseException {
    if(statusLine.getStatusCode() >= STATUS_CODE_THRESHOLD) {
      throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
    }
    if (entityIsNull) {
      throw new ClientProtocolException("Response contains no content");
    }
    return true;
  }
}
