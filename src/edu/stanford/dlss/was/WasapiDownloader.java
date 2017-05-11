package edu.stanford.dlss.was;

import java.io.IOException;

public class WasapiDownloader {
  public static final String SETTINGS_FILE_LOCATION = "config/settings.properties";

  public WasapiDownloaderSettings settings = null;


  public WasapiDownloader(String settingsFileLocation, String[] args) throws SettingsLoadException {
    settings = new WasapiDownloaderSettings(settingsFileLocation, args);
  }

  public void executeFromCmdLine() {
    if (settings.shouldDisplayHelp()) {
      System.out.print(settings.getHelpAndSettingsMessage());
      return;
    }

    //TODO: useful work
  }

  public static void main(String[] args) throws SettingsLoadException {
    WasapiDownloader downloader = new WasapiDownloader(SETTINGS_FILE_LOCATION, args);
    downloader.executeFromCmdLine();
  }
}
