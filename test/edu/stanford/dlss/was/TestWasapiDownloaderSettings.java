package edu.stanford.dlss.was;

import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.*;

import edu.stanford.dlss.was.WasapiDownloader;
import edu.stanford.dlss.was.WasapiDownloaderSettings;

public class TestWasapiDownloaderSettings {

    @Before
    public void setUp() {
    }

    @Test
    public void constructorReadsPropertiesFile() throws IOException {
      WasapiDownloaderSettings settings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
      assertEquals("incorrect default settings.properties baseurl value", settings.baseUrlString(), "http://example.org");
      assertEquals("incorrect default settings.properties username value", settings.username(), "user");
      assertEquals("incorrect default settings.properties password value", settings.password(), "pass");
    }

    @Test
    public void constructorThrowsIOExceptionOnMissingSettingsFile() {
      boolean hasThrownException = false;
      try {
        new WasapiDownloaderSettings("does/not/exist", null);
      } catch (IOException e) {
        hasThrownException = true;
      }
      assertEquals("did not throw expected IOException when trying to load missing file", hasThrownException, true);
    }
}
