package edu.stanford.dlss.was;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class WasapiClient {
  protected CloseableHttpClient wasapiClient;     // Protected for easier testing
  protected HttpClientContext wasapiContext;
  protected BasicCookieStore cookieStore;
  protected WasapiDownloaderSettings settings;

  public WasapiClient(WasapiDownloaderSettings settings) throws IOException, UnsupportedEncodingException {
    this.settings = settings;
    initializeClient();
  }

  public void login() throws IOException {
    HttpPost loginRequest = createLoginRequest(settings);
    CloseableHttpResponse response = wasapiClient.execute(loginRequest);
    HttpEntity entity = response.getEntity();
    EntityUtils.consume(entity);
    response.close();
  }


  public void close() throws IOException {
    wasapiClient.close();
  }


  public <T> T execute(HttpGet jsonRequest, ResponseHandler<? extends T> rh) throws IOException {
    return wasapiClient.execute(jsonRequest, rh, wasapiContext);
  }


  protected CloseableHttpClient initializeClient() {
    cookieStore = new BasicCookieStore();
    HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(cookieStore);

    wasapiContext = HttpClientContext.create();
    wasapiContext.setCookieStore(cookieStore);

    return builder.build();
  }


  private HttpPost createLoginRequest(WasapiDownloaderSettings settings) throws UnsupportedEncodingException {
    HttpPost httpPost = new HttpPost(settings.authUrlString());
    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("username", settings.username()));
    nvps.add(new BasicNameValuePair("password", settings.password()));
    httpPost.setEntity(new UrlEncodedFormEntity(nvps));
    return httpPost;
  }
}
