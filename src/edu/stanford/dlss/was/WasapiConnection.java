package edu.stanford.dlss.was;

import java.io.IOException;

import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;

public class WasapiConnection {

  private WasapiClient wasapiClient;
  private JsonResponseHandler jsonResponseHandler;
  private DownloadResponseHandler downloadResponseHandler;


  public WasapiConnection(WasapiClient wasapiClient,
                          JsonResponseHandler jsonHandler,
                          DownloadResponseHandler downloadHandler) throws IOException {
    this.wasapiClient = wasapiClient;
    this.jsonResponseHandler = jsonHandler;
    this.downloadResponseHandler = downloadHandler;
    this.wasapiClient.login();
  }


  public WasapiResponse jsonQuery(String requestURL) throws IOException {
    HttpGet jsonRequest = new HttpGet(requestURL);
    return wasapiClient.execute(jsonRequest, jsonResponseHandler);
  }


  public Boolean downloadQuery(String downloadURL, final String outputPath) throws ClientProtocolException, HttpResponseException, IOException {
    HttpGet fileRequest = new HttpGet(downloadURL);
    return wasapiClient.execute(fileRequest, downloadResponseHandler);
  }


  public void close() throws IOException {
    wasapiClient.close();
  }
}
