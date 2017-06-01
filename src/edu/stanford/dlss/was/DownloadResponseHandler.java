package edu.stanford.dlss.was;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;

public class DownloadResponseHandler implements ResponseHandler<Boolean> {
  private String outputPath;

  public DownloadResponseHandler(String outPath) {
    this.outputPath = outPath;
  }

  @Override
  public Boolean handleResponse(final HttpResponse response)
      throws ClientProtocolException, HttpResponseException, IOException {
    HttpEntity entity = response.getEntity();

    if (WasapiValidator.validateResponse(response.getStatusLine(), entity == null)) {
      FileOutputStream fouts = new FileOutputStream(outputPath, false);
      entity.writeTo(fouts);
      fouts.close();
      return true;
    }
    else return false;
  }
}
