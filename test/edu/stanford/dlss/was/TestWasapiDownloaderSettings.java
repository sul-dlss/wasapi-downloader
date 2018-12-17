package edu.stanford.dlss.was;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.cli.ParseException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.powermock.api.mockito.PowerMockito;

public class TestWasapiDownloaderSettings {
  private static final String EMPTY_SETTINGS_FILE_LOCATION = "test/fixtures/empty-settings.properties";

  @Test
  @SuppressWarnings({"checkstyle:NoWhitespaceAfter", "checkstyle:MethodLength"})
  public void constructor_readsPropertiesFileAndArgs() throws SettingsLoadException {
    // args is a String array, in the style of the `String[] args` param taken by the main method of a Java class.
    // JVM splits the whole command line argument string on whitespace, and passes the resultant String array into main, so
    // the below args array would come from something like:
    //   wasapi-downloader -h --collectionId 123 --crawlId=456 --crawlStartAfter 2014-03-14 --crawlStartBefore=2017-03-14
    // The Apache CLI parser should be able to handle all of those different styles of argument without any trouble.
    String[] args = { "-h", "--collectionId", "123", "--crawlId=456", "--crawlStartAfter", "2014-03-14", "--crawlStartBefore=2017-03-14", "--resume" };
    WasapiDownloaderSettings settings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, args);

    assertEquals("baseurl value should have come from settings file", "https://example.org/", settings.baseUrlString());
    assertEquals("authurl value should have come from settings file", "https://example.org/login", settings.authUrlString());
    assertEquals("username value should have come from settings file", "user", settings.username());
    assertEquals("password value should have come from settings file", "pass", settings.password());
    assertEquals("accountId value should have come from settings file", "1", settings.accountId());
    assertEquals("outputBaseDir value should have come from settings file", "test/outputBaseDir/", settings.outputBaseDir());
    assertEquals("checksumAlgorithm value should have come from settings file", "md5", settings.checksumAlgorithm());
    assertEquals("retries value should have come from settings file", "3", settings.retries());

    assertEquals("collectionId value should have come from args", "123", settings.collectionId());
    assertEquals("crawlId value should have come from args", "456", settings.crawlId());
    assertEquals("crawlStartAfter value should have come from args", "2014-03-14", settings.crawlStartAfter());
    assertEquals("crawlStartBefore value should have come from args", "2017-03-14", settings.crawlStartBefore());
    assertTrue("shouldDisplayHelp value should have come from args", settings.shouldDisplayHelp());
    assertTrue("shouldResume value should have come from args", settings.shouldResume());
  }

  @Test
  @SuppressWarnings({"checkstyle:NoWhitespaceAfter", "checkstyle:MethodLength", "checkstyle:ExecutableStatementCount"})
  public void getHelpAndSettingsMessage_containsUsageAndSettingsInfo() throws SettingsLoadException {
    //TODO: if settings validation flags possibly nonsensical/redundant combos like crawlId and crawlIdLowerBound,
    // then this test might have to be broken up a bit.
    String[] args = { "-h", "--collectionId", "123", "--crawlId=456", "--crawlIdLowerBound=400",
        "--crawlStartAfter", "2014-03-14", "--crawlStartBefore=2017-03-14", "--filename=filename.warc.gz", "--resume" };
    WasapiDownloaderSettings settings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, args);

    String helpAndSettingsMsg = settings.getHelpAndSettingsMessage();

    assertThat("helpAndSettingsMsg has a usage example", helpAndSettingsMsg, containsString("usage: bin/wasapi-downloader"));

    // allowable settings
    assertThat("helpAndSettingsMsg lists accountId arg", helpAndSettingsMsg, containsString("--accountId <arg>"));
    assertThat("helpAndSettingsMsg lists authurl arg", helpAndSettingsMsg, containsString("--authurl <arg>"));
    assertThat("helpAndSettingsMsg lists baseurl arg", helpAndSettingsMsg, containsString("--baseurl <arg>"));
    assertThat("helpAndSettingsMsg lists checksumAlgorithm arg", helpAndSettingsMsg, containsString("--checksumAlgorithm <arg>"));
    assertThat("helpAndSettingsMsg lists collectionId arg", helpAndSettingsMsg, containsString("--collectionId <arg>"));
    assertThat("helpAndSettingsMsg lists crawlId arg", helpAndSettingsMsg, containsString("--crawlId <arg>"));
    assertThat("helpAndSettingsMsg lists crawlIdLowerBound arg", helpAndSettingsMsg, containsString("--crawlIdLowerBound <arg>"));
    assertThat("helpAndSettingsMsg lists crawlStartAfter arg", helpAndSettingsMsg, containsString("--crawlStartAfter <arg>"));
    assertThat("helpAndSettingsMsg lists crawlStartBefore arg", helpAndSettingsMsg, containsString("--crawlStartBefore <arg>"));
    assertThat("helpAndSettingsMsg lists filename arg", helpAndSettingsMsg, containsString("--filename <arg>"));
    assertThat("helpAndSettingsMsg lists help flag", helpAndSettingsMsg, containsString("-h,--help"));
    assertThat("helpAndSettingsMsg lists password arg", helpAndSettingsMsg, containsString("--password <arg>"));
    assertThat("helpAndSettingsMsg lists retries arg", helpAndSettingsMsg, containsString("--retries <arg>"));
    assertThat("helpAndSettingsMsg lists username arg", helpAndSettingsMsg, containsString("--username <arg>"));
    assertThat("helpAndSettingsMsg lists resume flag", helpAndSettingsMsg, containsString("--resume"));

    // values
    assertThat("helpAndSettingsMsg lists accountId value", helpAndSettingsMsg, containsString("accountId : 1"));
    assertThat("helpAndSettingsMsg lists authurl value", helpAndSettingsMsg, containsString("authurl : https://example.org/login"));
    assertThat("helpAndSettingsMsg lists baseurl value", helpAndSettingsMsg, containsString("baseurl : https://example.org"));
    assertThat("helpAndSettingsMsg lists checksumAlgorithm value", helpAndSettingsMsg, containsString("checksumAlgorithm : md5"));
    assertThat("helpAndSettingsMsg lists collectionId value", helpAndSettingsMsg, containsString("collectionId : 123"));
    assertThat("helpAndSettingsMsg lists crawlId value", helpAndSettingsMsg, containsString("crawlId : 456"));
    assertThat("helpAndSettingsMsg lists crawlIdLowerBound value", helpAndSettingsMsg, containsString("crawlIdLowerBound : 400"));
    assertThat("helpAndSettingsMsg lists crawlStartAfter value", helpAndSettingsMsg, containsString("crawlStartAfter : 2014-03-14"));
    assertThat("helpAndSettingsMsg lists crawlStartBefore value", helpAndSettingsMsg, containsString("crawlStartBefore : 2017-03-14"));
    assertThat("helpAndSettingsMsg lists filename value", helpAndSettingsMsg, containsString("filename : filename.warc.gz"));
    assertThat("helpAndSettingsMsg lists help flag value", helpAndSettingsMsg, containsString("help : true"));
    assertThat("helpAndSettingsMsg hides password value", helpAndSettingsMsg, containsString("password : [password hidden]"));
    assertThat("helpAndSettingsMsg lists retries value", helpAndSettingsMsg, containsString("retries : 3"));
    assertThat("helpAndSettingsMsg lists username value", helpAndSettingsMsg, containsString("username : user"));
  }

  @Test
  @SuppressWarnings({"checkstyle:NoWhitespaceAfter", "checkstyle:LineLength"})
  public void argsOverrideSettings() throws SettingsLoadException {
    String[] args = { "--username=user2", "--outputBaseDir=test/outputBaseDir2" };
    WasapiDownloaderSettings settings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, args);
    assertEquals("the username from the .properties file should get overridden by the command-line arg", "user2", settings.username());
    assertEquals("the outputBaseDir from the .properties file should get overridden by the command-line arg", "test/outputBaseDir2", settings.outputBaseDir());
  }

  @Test
  @SuppressWarnings("checkstyle:NoWhitespaceAfter")
  public void toString_aliasesGetHelpAndSettingsMessage() throws SettingsLoadException {
    String[] args = { "-h", "--collectionId", "123", "--crawlId=456", "--crawlStartAfter", "2014-03-14", "--crawlStartBefore=2017-03-14" };
    WasapiDownloaderSettings settings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, args);

    assertSame("toString should return the same String obj as getHelpAndSettingsMessage", settings.getHelpAndSettingsMessage(), settings.toString());
  }

  @Test
  public void constructor_throwsSettingsLoadExceptionOnMissingSettingsFile() {
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
  @SuppressWarnings("checkstyle:NoWhitespaceAfter")
  public void constructor_throwsSettingsLoadExceptionOnBadArgs() {
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

  @Test(expected = SettingsLoadException.class)
  @SuppressWarnings("checkstyle:NoWhitespaceAfter")
  public void constructor_throwsSettingsLoadExceptionOnInvalidSettings() throws SettingsLoadException {
    String[] args = { "--baseurl=ftp://foo.org", "--authurl=http://foo.com/auth" }; // both of these URLs use unsupported protocols
    WasapiDownloaderSettings settings = new WasapiDownloaderSettings(EMPTY_SETTINGS_FILE_LOCATION, args);
  }

  @Test
  @SuppressWarnings({"checkstyle:NoWhitespaceAfter", "checkstyle:LineLength", "checkstyle:MethodLength"})
  public void getSettingsErrorMessages_listsAllErrors() {
    // use the no arg constructor, so that validateSettings() doesn't get called, so we can test the method it relies on
    WasapiDownloaderSettings wdSettings = new WasapiDownloaderSettings();

    Properties internalSettings = new Properties();
    wdSettings.settings = internalSettings;
    internalSettings.setProperty(WasapiDownloaderSettings.ACCCOUNT_ID_PARAM_NAME, "z26");
    internalSettings.setProperty(WasapiDownloaderSettings.AUTH_URL_PARAM_NAME, "http://foo.com/auth");
    internalSettings.setProperty(WasapiDownloaderSettings.BASE_URL_PARAM_NAME, "ftp://foo.org");
    internalSettings.setProperty(WasapiDownloaderSettings.CHECKSUM_ALGORITHM_PARAM_NAME, "foo");
    internalSettings.setProperty(WasapiDownloaderSettings.COLLECTION_ID_PARAM_NAME, "a1");
    internalSettings.setProperty(WasapiDownloaderSettings.CRAWL_ID_PARAM_NAME, "b2");
    internalSettings.setProperty(WasapiDownloaderSettings.CRAWL_ID_LOWER_BOUND_PARAM_NAME, "c3");
    internalSettings.setProperty(WasapiDownloaderSettings.CRAWL_START_AFTER_PARAM_NAME, "12/31/2010");
    internalSettings.setProperty(WasapiDownloaderSettings.CRAWL_START_BEFORE_PARAM_NAME, "01/01/2001");
    internalSettings.setProperty(WasapiDownloaderSettings.OUTPUT_BASE_DIR_PARAM_NAME, "does/not/exist");
    internalSettings.setProperty(WasapiDownloaderSettings.PASSWORD_PARAM_NAME, "");
    internalSettings.setProperty(WasapiDownloaderSettings.RETRIES_PARAM_NAME, "-1");
    internalSettings.setProperty(WasapiDownloaderSettings.USERNAME_PARAM_NAME, "");

    List<String> errMsgs = wdSettings.getSettingsErrorMessages();
    assertThat("error messages has entry for invalid account ID", errMsgs, hasItem("accountId must be an integer (if specified)"));
    assertThat("error messages has entry for invalid auth URL", errMsgs, hasItem("authurl is required, and must be a valid URL"));
    assertThat("error messages has entry for invalid base URL", errMsgs, hasItem("baseurl is required, and must be a valid URL"));
    assertThat("error messages has entry for invalid checksumAlgorithm", errMsgs, hasItem("checksumAlgorithm is required and must be md5 or sha1"));
    assertThat("error messages has entry for invalid collectionId", errMsgs, hasItem("collectionId must be an integer (if specified)"));
    assertThat("error messages has entry for invalid crawlId", errMsgs, hasItem("crawlId must be an integer (if specified)"));
    assertThat("error messages has entry for invalid crawlIdLowerBound", errMsgs, hasItem("crawlIdLowerBound must be an integer (if specified)"));
    assertThat("error messages has entry for invalid crawlStartAfter", errMsgs, hasItem("crawlStartAfter must be a valid ISO 8601 date string (if specified)"));
    assertThat("error messages has entry for invalid crawlStartBefore", errMsgs, hasItem("crawlStartBefore must be a valid ISO 8601 date string (if specified)"));
    assertThat("error messages has entry for invalid outputBaseDir", errMsgs, hasItem("outputBaseDir is required (and must be an extant, writable directory)"));
    assertThat("error messages has entry for invalid password", errMsgs, hasItem("password is required"));
    assertThat("error messages has entry for invalid retries", errMsgs, hasItem("retries is required and must be an integer >= 0"));
    assertThat("error messages has entry for invalid username", errMsgs, hasItem("username is required"));
  }

  @Test
  @SuppressWarnings("checkstyle:MethodLength")
  public void normalizeIso8601Setting_behavesCorrectly() {
    TimeZone.setDefault(TimeZone.getTimeZone("PDT")); // for test repeatability in different environments

    WasapiDownloaderSettings wdSettings = new WasapiDownloaderSettings();
    ByteArrayOutputStream errStream = new ByteArrayOutputStream();
    wdSettings.setErrStream(new PrintStream(errStream));
    Properties dateStrSettings = new Properties();
    wdSettings.settings = dateStrSettings;
    dateStrSettings.setProperty("yearOnly", "1999");
    dateStrSettings.setProperty("date", "2001-03-14");
    dateStrSettings.setProperty("dateWithTime", "2010-01-01T03:14:00");
    dateStrSettings.setProperty("dateWithTimeUtc", "2010-01-01T03:14:00Z");
    dateStrSettings.setProperty("dateWithTimePacific", "2010-01-01T03:14:00-07:00");
    dateStrSettings.setProperty("invalidIso8601Date", "01/01/2001");

    // this is sort of a bad example of normalizeIso8601Setting usage: we are failing slightly to
    // follow the method's javadoc in this somewhat contrived example, since we're filling a Properties
    // map with different style dates and not using the _PARAM_NAME constants to retrieve them.
    assertTrue("yearOnly can be parsed and normalized", wdSettings.normalizeIso8601Setting("yearOnly"));
    assertTrue("year-month-day can be parsed and normalized", wdSettings.normalizeIso8601Setting("date"));
    assertTrue("date with time can be parsed and normalized", wdSettings.normalizeIso8601Setting("dateWithTime"));
    assertTrue("date with UTC time can be parsed and normalized", wdSettings.normalizeIso8601Setting("dateWithTimeUtc"));
    assertTrue("date with pacific time can be parsed and normalized", wdSettings.normalizeIso8601Setting("dateWithTimePacific"));
    assertFalse("invalid iso8601 string can't be parsed and normalized", wdSettings.normalizeIso8601Setting("invalidIso8601Date"));

    assertEquals("yearOnly gets expanded to become first day of year", "1999-01-01", dateStrSettings.getProperty("yearOnly"));
    assertEquals("year-month-day gets used as-is", "2001-03-14", dateStrSettings.getProperty("date"));
    assertEquals("date with implied system time zone gets truncated to year-month-day", "2010-01-01", dateStrSettings.getProperty("dateWithTime"));
    assertEquals("date with UTC time gets truncated to year-month-day", "2010-01-01", dateStrSettings.getProperty("dateWithTimeUtc"));
    assertEquals("date with pacific time gets truncated to year-month-day", "2010-01-01", dateStrSettings.getProperty("dateWithTimePacific"));
    assertEquals("invalid iso8601 gets nulled out", null, dateStrSettings.getProperty("01/01/2001"));

    String errStreamContent = errStream.toString();
    assertThat("yearOnly has a warning message",
        errStreamContent, containsString("Normalized yearOnly to 1999-01-01 from 1999"));
    assertThat("dateWithTime has a warning message",
        errStreamContent, containsString("Normalized dateWithTime to 2010-01-01 from 2010-01-01T03:14:00"));
    assertThat("dateWithTimeUtc has a warning message",
        errStreamContent, containsString("Normalized dateWithTimeUtc to 2010-01-01 from 2010-01-01T03:14:00Z"));
    assertThat("dateWithTimePacific has a warning message",
        errStreamContent, containsString("Normalized dateWithTimePacific to 2010-01-01 from 2010-01-01T03:14:00-07:00"));
  }

  @Test
  public void isNullOrEmpty_behavesCorrectly() {
    assertTrue("isNullOrEmpty returns true on null", WasapiDownloaderSettings.isNullOrEmpty(null));
    assertTrue("isNullOrEmpty returns true on empty string", WasapiDownloaderSettings.isNullOrEmpty(""));
    assertFalse("isNullOrEmpty returns false on a non-empty string", WasapiDownloaderSettings.isNullOrEmpty("stuff"));
  }

  @Test
  public void isDirWritable_behavesCorrectly() {
    assertFalse("isDirWritable returns false if path doesn't exist", WasapiDownloaderSettings.isDirWritable("does/not/exist"));
    assertFalse("isDirWritable returns false if path isn't a directory", WasapiDownloaderSettings.isDirWritable(EMPTY_SETTINGS_FILE_LOCATION));
    assertFalse("isDirWritable returns false if path isn't writable", WasapiDownloaderSettings.isDirWritable("/dev"));
    assertTrue("isDirWritable returns true if path is an extant, writable directory", WasapiDownloaderSettings.isDirWritable("test/outputBaseDir"));
  }

  @Test
  public void validateSettings_throwsSettingsLoadExceptionWithErrorExplanation() {
    // use the no arg constructor, so that validateSettings() doesn't get called, so we can skip loading real settings and do some mocking
    WasapiDownloaderSettings settings = PowerMockito.spy(new WasapiDownloaderSettings());

    List<String> errMessages = new LinkedList<String>();
    errMessages.add("err msg 1");
    errMessages.add("err msg 2");
    PowerMockito.doReturn(errMessages).when(settings).getSettingsErrorMessages();

    boolean hasThrownException = false;
    try {
      settings.validateSettings();
    } catch(SettingsLoadException e) {
      hasThrownException = true;
      String expectedExMsg = "Invalid settings state:\n  err msg 1.\n  err msg 2.\n";
      assertThat("exception message should contain text about settings in error", e.getMessage(), containsString(expectedExMsg));
      assertNull("exception should not have another root cause", e.getCause());
    }
    assertTrue("validateSettings() should throw SettingsLoadException if there are settings errors", hasThrownException);
  }
}
