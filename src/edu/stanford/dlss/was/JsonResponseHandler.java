package edu.stanford.dlss.was;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;

public class JsonResponseHandler implements ResponseHandler<WasapiResponse> {

  @Override
  public WasapiResponse handleResponse(final HttpResponse response)
      throws ClientProtocolException, HttpResponseException, IOException {
    HttpEntity entity = response.getEntity();
    if (WasapiValidator.validateResponse(response.getStatusLine(), entity == null))
      return new WasapiResponseParser().parse(entity.getContent());
    else return null;
  }
}
