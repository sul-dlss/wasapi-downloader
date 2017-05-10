package edu.stanford.dlss.was;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class WasapiDownloaderSettings {
  private Properties settings = null;


  public WasapiDownloaderSettings(String settingsFileLocation, String[] args) throws IOException {
    loadPropertiesFile(settingsFileLocation);
    //FIXME: parse args for collectionId, jobId, crawlStartAfter, crawlStartBefore.  store in settings var.
  }


  public String baseUrlString() {
    return settings.getProperty("baseurl");
  }

  public String username() {
    return settings.getProperty("username");
  }

  public String password() {
    return settings.getProperty("password");
  }

  public String collectionId() {
    return null; //FIXME: should come from command line args (stored in settings var)
  }

  public String jobId() {
    return null; //FIXME: should come from command line args (stored in settings var)
  }

  // e.g. 2014-01-01, see https://github.com/WASAPI-Community/data-transfer-apis/tree/master/ait-reference-specification#paths--examples
  public String crawlStartAfter() {
    return null; //FIXME: should come from command line args (stored in settings var)
  }

  // e.g. 2014-01-01, see https://github.com/WASAPI-Community/data-transfer-apis/tree/master/ait-reference-specification#paths--examples
  public String crawlStartBefore() {
    return null; //FIXME: should come from command line args (stored in settings var)
  }


  private void loadPropertiesFile(String settingsFileLocation) throws IOException {
    if (settings == null) {
      InputStream input = null;
      try {
        input = new FileInputStream(settingsFileLocation);
        settings = new Properties();
        settings.load(input);
      } finally {
        if (input != null) {
          input.close();
        }
      }
    } else {
      System.err.println("Properties already loaded from " + settingsFileLocation);
    }
  }
}
