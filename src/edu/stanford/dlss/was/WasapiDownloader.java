package edu.stanford.dlss.was;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;

public class WasapiDownloader {
  public static final String SETTINGS_FILE_LOCATION = "config/settings.properties";

  public Properties settings = null;


  public WasapiDownloader() throws IOException {
    loadPropertiesFile();
  }


  private void loadPropertiesFile() throws IOException {
    if (settings == null) {
      InputStream input = null;
      try {
        input = new FileInputStream(SETTINGS_FILE_LOCATION);
        settings = new Properties();
        settings.load(input);
      } finally {
        if (input != null) {
          input.close();
        }
      }
    } else {
      System.err.println("Properties already loaded from " + SETTINGS_FILE_LOCATION);
    }
  }


  public static void main(String[] args) throws IOException {
    new WasapiDownloader();
  }
}
