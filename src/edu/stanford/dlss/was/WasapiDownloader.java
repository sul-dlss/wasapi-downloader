package edu.stanford.dlss.was;

import java.io.*;
import java.util.Properties;

public class WasapiDownloader
{
  public static final String SETTINGS_FILE_LOCATION = "config/settings.properties";
  public Properties settings = null;

  public WasapiDownloader() {
    loadPropertiesFile();
  }

  private void loadPropertiesFile() {
    if (settings == null)
    {
      InputStream input = null;
      try {
        input = new FileInputStream(SETTINGS_FILE_LOCATION);
        settings = new Properties();
        settings.load(input);
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(99);
      }  finally {
        if (input != null) {
          try {
            input.close();
          } catch (IOException e) {
            e.printStackTrace();
            System.exit(99);
          }
        }
      }
    }
    else
    {
      System.err.println("Properties already loaded from " + SETTINGS_FILE_LOCATION);
    }
  }


  public static void main(String[] args) {
    System.out.println("I am WasapiDownloader. Hear me roar. Woo.");
    new WasapiDownloader();
  }

}
