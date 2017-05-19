package edu.stanford.dlss.was;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.*;
import static org.mockito.Mockito.*;

public class TestWasapiClient {
  @Test
  public void constructorInitializesCorrectly() throws IOException, SettingsLoadException {
    WasapiDownloaderSettings settings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    WasapiClient testClient = new WasapiClient(settings);

    assertNotNull(testClient.wasapiClient);
    assertNotNull(testClient.wasapiContext);
    assertNotNull(testClient.cookieStore);
    assertEquals(testClient.settings, settings);
  }

  @Test
  public void closeCloses() throws IOException, SettingsLoadException {
    WasapiClient testClient = new WasapiClient(new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
    testClient.wasapiClient = mockHttpClient;

    testClient.close();

    verify(mockHttpClient, times(1)).close();
  }

  @Test
  public void executeUsesContext() throws IOException, SettingsLoadException {
    WasapiClient testClient = new WasapiClient(new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    CloseableHttpClient mockHttpClient = mock(CloseableHttpClient.class);
    testClient.wasapiClient = mockHttpClient;

    JsonResponseHandler mockHandler = mock(JsonResponseHandler.class);
    HttpGet mockRequest = mock(HttpGet.class);

    testClient.execute(mockRequest, mockHandler);

    verify(mockHttpClient, times(1)).execute(mockRequest, mockHandler, testClient.wasapiContext);
  }
}
