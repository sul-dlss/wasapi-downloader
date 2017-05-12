package edu.stanford.dlss.was;

import java.io.IOException;

import org.apache.commons.cli.ParseException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.*;

public class TestWasapiDownloaderSettings {

    @Before
    public void setUp() {
    }

    @Test
    public void constructorReadsPropertiesFileAndArgs() throws SettingsLoadException {
      // args is a String array, in the style of the `String[] args` param taken by the main method of a Java class.
      // JVM splits the whole command line argument string on whitespace, and passes the resultant String array into main, so
      // the below args array would come from something like:
      //   wasapi-downloader -h --collectionId 123 --jobId=456 --crawlStartAfter 2014-03-14 --crawlStartBefore=2017-03-14
      // The Apache CLI parser should be able to handle all of those different styles of argument without any trouble.
      String[] args = { "-h", "--collectionId", "123", "--jobId=456", "--crawlStartAfter", "2014-03-14", "--crawlStartBefore=2017-03-14" };
      WasapiDownloaderSettings settings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, args);

      assertEquals("incorrect default settings.properties baseurl value", settings.baseUrlString(), "http://example.org");
      assertEquals("incorrect default settings.properties username value", settings.username(), "user");
      assertEquals("incorrect default settings.properties password value", settings.password(), "pass");
      assertEquals("incorrect value for collectionId", settings.collectionId(), "123");
      assertEquals("incorrect value for jobId", settings.jobId(), "456");
      assertEquals("incorrect value for crawlStartAfter", settings.crawlStartAfter(), "2014-03-14");
      assertEquals("incorrect value for crawlStartBefore", settings.crawlStartBefore(), "2017-03-14");
      assertTrue("incorrect value for shouldDisplayHelp", settings.shouldDisplayHelp());
    }

    @Test
    public void getHelpAndSettingsMessageContainsUsageAndSettingsInfo() throws SettingsLoadException {
      String[] args = { "-h", "--collectionId", "123", "--jobId=456", "--crawlStartAfter", "2014-03-14", "--crawlStartBefore=2017-03-14" };
      WasapiDownloaderSettings settings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, args);

      String helpAndSettingsMsg = settings.getHelpAndSettingsMessage();
      assertThat("helpAndSettingsMsg has a usage example", helpAndSettingsMsg, containsString("usage: bin/wasapi-downloader"));
      assertThat("helpAndSettingsMsg lists collectionId arg", helpAndSettingsMsg, containsString("--collectionId <arg>"));
      assertThat("helpAndSettingsMsg lists crawlStartAfter arg", helpAndSettingsMsg, containsString("--crawlStartAfter <arg>"));
      assertThat("helpAndSettingsMsg lists crawlStartBefore arg", helpAndSettingsMsg, containsString("--crawlStartBefore <arg>"));
      assertThat("helpAndSettingsMsg lists help flag", helpAndSettingsMsg, containsString("-h,--help"));
      assertThat("helpAndSettingsMsg lists jobId arg", helpAndSettingsMsg, containsString("--jobId <arg>"));
      assertThat("helpAndSettingsMsg hides password value", helpAndSettingsMsg, containsString("password : [password hidden]"));
      assertThat("helpAndSettingsMsg lists crawlStartAfter value", helpAndSettingsMsg, containsString("crawlStartAfter : 2014-03-14"));
      assertThat("helpAndSettingsMsg lists crawlStartBefore value", helpAndSettingsMsg, containsString("crawlStartBefore : 2017-03-14"));
      assertThat("helpAndSettingsMsg lists help flag value", helpAndSettingsMsg, containsString("help : true"));
      assertThat("helpAndSettingsMsg lists collectionId value", helpAndSettingsMsg, containsString("collectionId : 123"));
      assertThat("helpAndSettingsMsg lists baseurl value", helpAndSettingsMsg, containsString("baseurl : http://example.org"));
      assertThat("helpAndSettingsMsg lists username value", helpAndSettingsMsg, containsString("username : user"));
    }

    @Test
    public void toStringAliasesGetHelpAndSettingsMessage() throws SettingsLoadException {
      String[] args = { "-h", "--collectionId", "123", "--jobId=456", "--crawlStartAfter", "2014-03-14", "--crawlStartBefore=2017-03-14" };
      WasapiDownloaderSettings settings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, args);

      assertSame("toString should return the same String obj as getHelpAndSettingsMessage", settings.getHelpAndSettingsMessage(), settings.toString());
    }

    @Test
    public void constructorThrowsSettingsLoadExceptionOnMissingSettingsFile() {
      boolean hasThrownException = false;
      try {
        new WasapiDownloaderSettings("does/not/exist", null);
      } catch (SettingsLoadException e) {
        hasThrownException = true;
        assertThat("cause should be IOException", e.getCause(), instanceOf(IOException.class));
      }
      assertTrue("constructor should throw SettingsLoadException when trying to load missing file", hasThrownException);
    }

    @Test
    public void constructorThrowsSettingsLoadExceptionOnBadArgs() {
      boolean hasThrownException = false;
      try {
        String[] args = { "--unrecognizedOpt", "123" };
        WasapiDownloaderSettings settings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, args);
      } catch (SettingsLoadException e) {
        hasThrownException = true;
        assertThat("cause should be ParseException", e.getCause(), instanceOf(ParseException.class));
      }
      assertTrue("constructor should throw SettingsLoadException on unrecognized arg", hasThrownException);
    }
}
