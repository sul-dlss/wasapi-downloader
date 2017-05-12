package edu.stanford.dlss.was;

import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.*;

public class TestWasapiDownloader {

    @Before
    public void setUp() {
    }

    @Test
    public void constructorLoadsSettings() throws SettingsLoadException {
      WasapiDownloader myInstance = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
      assertNotNull(myInstance.settings);
    }

    @Test
    public void canExecuteMainNoHelpWithoutCrashing() throws SettingsLoadException {
      WasapiDownloader.main(null);
    }

    @Test
    public void canExecuteMainWithHelpWithoutCrashing() throws SettingsLoadException {
      String[] args = { "-h" };
      WasapiDownloader.main(args);
    }
}
