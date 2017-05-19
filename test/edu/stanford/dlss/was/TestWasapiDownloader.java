package edu.stanford.dlss.was;

import static org.junit.Assert.*;
import org.junit.*;

public class TestWasapiDownloader {

  @Test
  public void constructor_loadsSettings() throws SettingsLoadException {
    WasapiDownloader myInstance = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    assertNotNull(myInstance.settings);
  }

  @Test
  public void main_noHelp_canExecuteWithoutCrashing() throws SettingsLoadException {
    WasapiDownloader.main(null);
  }

  @Test
  @SuppressWarnings("checkstyle:NoWhitespaceAfter")
  public void main_withHelp_canExecuteWithoutCrashing() throws SettingsLoadException {
    String[] args = { "-h" };
    WasapiDownloader.main(args);
  }
}
