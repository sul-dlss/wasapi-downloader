package edu.stanford.dlss.was;

import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;

public class WasapiConnection {
  private WasapiClient wasapiClient;

  public WasapiConnection(WasapiClient wasapiClient) throws IOException {
    this.wasapiClient = wasapiClient;
    this.wasapiClient.login();
  }


  /**
   * @return null when requestURL is null (for callers that just page through responses' "next" links)
   */
  public WasapiResponse jsonQuery(String requestURL) throws IOException {
    if (requestURL == null)
      return null;

    HttpGet jsonRequest = new HttpGet(requestURL);
    return wasapiClient.execute(jsonRequest, new JsonResponseHandler());
  }

  public List<WasapiResponse> pagedJsonQuery(String requestURL) throws IOException {
    List<WasapiResponse> wasapiRespList = new LinkedList<WasapiResponse>();

    WasapiResponse wasapiResp = jsonQuery(requestURL);
    while(wasapiResp != null) {
      wasapiRespList.add(wasapiResp);
      wasapiResp = jsonQuery(wasapiResp.getNext());
    }

    return wasapiRespList;
  }

  public Boolean downloadQuery(String downloadURL, final String outputPath) throws ClientProtocolException, HttpResponseException, IOException {
    HttpGet fileRequest = new HttpGet(downloadURL);
    return wasapiClient.execute(fileRequest, new DownloadResponseHandler(outputPath));
  }


  public void close() throws IOException {
    wasapiClient.close();
  }
}
