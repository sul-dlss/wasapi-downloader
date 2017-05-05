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
    public void constructorReadsPropertiesFile() throws IOException {
      WasapiDownloader myInstance = new WasapiDownloader();
      assertNotNull(myInstance.settings);
      assertEquals("incorrect default settings.properties baseurl value", myInstance.settings.getProperty("baseurl"), "http://example.org");
      assertEquals("incorrect default settings.properties username value", myInstance.settings.getProperty("username"), "user");
      assertEquals("incorrect default settings.properties password value", myInstance.settings.getProperty("password"), "pass");
    }

    @Test
    public void canExecuteMainWithoutCrashing() throws IOException {
      WasapiDownloader.main(null);
    }
}
