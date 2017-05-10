package edu.stanford.dlss.was;

import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.*;

import edu.stanford.dlss.was.WasapiDownloader;

public class TestWasapiDownloader {

    @Before
    public void setUp() {
    }

    @Test
    public void constructorLoadsSettings() throws IOException {
      WasapiDownloader myInstance = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
      assertNotNull(myInstance.settings);
    }

    @Test
    public void canExecuteMainWithoutCrashing() throws IOException {
      WasapiDownloader.main(null);
    }
}
