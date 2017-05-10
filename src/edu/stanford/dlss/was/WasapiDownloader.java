package edu.stanford.dlss.was;

import java.io.IOException;

public class WasapiDownloader {
  public static final String SETTINGS_FILE_LOCATION = "config/settings.properties";

  public WasapiDownloaderSettings settings = null;


  public WasapiDownloader(String settingsFileLocation, String[] args) throws IOException {
    settings = new WasapiDownloaderSettings(settingsFileLocation, args);
  }


  public static void main(String[] args) throws IOException {
    new WasapiDownloader(SETTINGS_FILE_LOCATION, args);
  }
}
