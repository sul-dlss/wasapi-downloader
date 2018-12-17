package edu.stanford.dlss.was;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.io.File;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.hamcrest.core.StringStartsWith;
import org.junit.*;
import org.mockito.Mockito;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * WasapiDownloader tests for downloadAndValidateFile() method
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WasapiDownloader.class, WasapiConnection.class})
@SuppressWarnings({"TypeName", "MethodLength"})
public class TestWasapiDownloader_DownloadAndValidateFile {

  @Test
  public void downloadAndValidateFile_callsPrepareOutpuLocation() throws NoSuchAlgorithmException, SettingsLoadException {
    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    WasapiFile wfile = new WasapiFile();
    Mockito.doReturn(null).when(downloaderSpy).prepareOutputLocation(wfile);

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(downloaderSpy).prepareOutputLocation(wfile);
  }

  @Test
  public void downloadAndValidateFile_printsErrorForNullOutpuLocation() throws NoSuchAlgorithmException, SettingsLoadException {
    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    WasapiFile wfile = new WasapiFile();
    Mockito.doReturn(null).when(downloaderSpy).prepareOutputLocation(wfile);

    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));

    downloaderSpy.downloadAndValidateFile(wfile);
    assertEquals("Wrong SYSERR output", "fullFilePath is null - can't retrieve file\n", errContent.toString());
  }

  @Test
  public void downloadAndValidateFile_callsDownloadQuery() throws Exception {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation, "another location"};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.downloadQuery(firstLocation, fullFilePath)).thenReturn(false);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(fullFilePath).when(downloaderSpy).prepareOutputLocation(wfile);
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(mockConn, atLeastOnce()).downloadQuery(firstLocation, fullFilePath);
  }

  @Test
  public void downloadAndValidateFile_callsChecksumValidate() throws Exception {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.downloadQuery(firstLocation, fullFilePath)).thenReturn(true);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(fullFilePath).when(downloaderSpy).prepareOutputLocation(wfile);
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();
    Mockito.doReturn(false).when(downloaderSpy).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(downloaderSpy, atLeastOnce()).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
  }

  @Test
  public void downloadAndValidateFile_success() throws Exception {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.downloadQuery(firstLocation, fullFilePath)).thenReturn(true);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(fullFilePath).when(downloaderSpy).prepareOutputLocation(wfile);
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();
    Mockito.doReturn(true).when(downloaderSpy).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);

    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(mockConn, times(1)).downloadQuery(firstLocation, fullFilePath);
    verify(downloaderSpy, times(1)).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
    assertEquals("Wrong SYSOUT output", "file retrieved successfully: " + firstLocation + "\n", outContent.toString());
    assertEquals("No SYSERR output for success", "", errContent.toString());
  }

  @Test
  public void downloadAndValidateFile_downloadFailsThenSucceeds() throws Exception {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.downloadQuery(firstLocation, fullFilePath)).thenReturn(false, false, false, true);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(fullFilePath).when(downloaderSpy).prepareOutputLocation(wfile);
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();
    Mockito.doReturn(true).when(downloaderSpy).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(mockConn, times(defaultNumRetries() + 1)).downloadQuery(firstLocation, fullFilePath);
    verify(downloaderSpy, times(1)).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
  }

  @Test
  public void downloadAndValidateFile_validationFailsThenSucceeds() throws Exception {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.downloadQuery(firstLocation, fullFilePath)).thenReturn(true);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(fullFilePath).when(downloaderSpy).prepareOutputLocation(wfile);
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();
    Mockito.doReturn(false, false, false, true).when(downloaderSpy).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(mockConn, times(defaultNumRetries() + 1)).downloadQuery(firstLocation, fullFilePath);
    verify(downloaderSpy, times(defaultNumRetries() + 1)).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
  }

  @Test
  public void downloadAndValidateFile_downloadFailsThenValidationFails() throws Exception{
    // checksum won't validate, then download doesn't work, but 3rd time both work
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.downloadQuery(firstLocation, fullFilePath)).thenReturn(true, false, true);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(fullFilePath).when(downloaderSpy).prepareOutputLocation(wfile);
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();
    Mockito.doReturn(false, true).when(downloaderSpy).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(mockConn, times(3)).downloadQuery(firstLocation, fullFilePath);
    verify(downloaderSpy, times(2)).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
  }

  @Test
  public void downloadAndValidateFile_downloadNeverSucceeds() throws Exception {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.downloadQuery(firstLocation, fullFilePath)).thenReturn(false, false, false, false);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(fullFilePath).when(downloaderSpy).prepareOutputLocation(wfile);
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();

    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(mockConn, times(defaultNumRetries() + 1)).downloadQuery(firstLocation, fullFilePath);
    verify(downloaderSpy, never()).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
    assertEquals("Wrong SYSERR output", "file not retrieved or unable to validate checksum: " + firstLocation + "\n", errContent.toString());
  }

  @Test
  public void downloadAndValidateFile_checksumNeverValidates() throws Exception {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.downloadQuery(firstLocation, fullFilePath)).thenReturn(true, true, true, true);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(fullFilePath).when(downloaderSpy).prepareOutputLocation(wfile);
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();
    Mockito.doReturn(false).when(downloaderSpy).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);

    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(mockConn, times(defaultNumRetries() + 1)).downloadQuery(firstLocation, fullFilePath);
    verify(downloaderSpy, times(defaultNumRetries() + 1)).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
    assertEquals("Wrong SYSERR output", "file not retrieved or unable to validate checksum: " + firstLocation + "\n", errContent.toString());
  }

  @Test
  public void downloadAndValidateFile_HttpResponseException_handled() throws IOException, SettingsLoadException, NoSuchAlgorithmException {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    HttpResponseException hre = new HttpResponseException(666, "reason");
    Mockito.when(mockConn.downloadQuery(firstLocation, fullFilePath)).thenThrow(hre);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(fullFilePath).when(downloaderSpy).prepareOutputLocation(wfile);
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();

    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(mockConn, times(1)).downloadQuery(firstLocation, fullFilePath);
    verify(downloaderSpy, never()).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
    String expected = "ERROR: HttpResponseException (reason) downloading file (will not retry): " + firstLocation;
    assertThat("SYSERR should indicate HttpResponseException", errContent.toString(), StringStartsWith.startsWith(expected));
    assertThat("SYSERR should indicate Http ResponseCode", errContent.toString(), containsString("HTTP ResponseCode was 666"));
    assertThat("SYSERR should not have stacktrace", errContent.toString(), not(containsString(".HttpResponseException")));
  }

  @Test
  public void downloadAndValidateFile_ClientProtocolException_handled() throws IOException, SettingsLoadException, NoSuchAlgorithmException {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    ClientProtocolException cpe = new ClientProtocolException("reason");
    Mockito.when(mockConn.downloadQuery(firstLocation, fullFilePath)).thenThrow(cpe);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(fullFilePath).when(downloaderSpy).prepareOutputLocation(wfile);
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();

    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(mockConn, times(1)).downloadQuery(firstLocation, fullFilePath);
    verify(downloaderSpy, never()).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
    String expected = "ERROR: ClientProtocolException (reason) downloading file (will not retry): " + firstLocation;
    assertThat("SYSERR should indicate ClientProtocolException", errContent.toString(), StringStartsWith.startsWith(expected));
    assertThat("SYSERR should not have stacktrace", errContent.toString(), not(containsString(".ClientProtocolException")));
  }

  @Test
  public void downloadAndValidateFile_IOException_handled() throws Exception{
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    IOException ioe = new IOException("reason");
    Mockito.when(mockConn.downloadQuery(firstLocation, fullFilePath)).thenThrow(ioe).thenReturn(false, false, false);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(fullFilePath).when(downloaderSpy).prepareOutputLocation(wfile);
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();

    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(mockConn, times(defaultNumRetries() + 1)).downloadQuery(firstLocation, fullFilePath);
    verify(downloaderSpy, never()).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
    String expected = "WARNING: exception downloading file (will retry): " + firstLocation;
    assertThat("SYSERR should indicate IOException", errContent.toString(), StringStartsWith.startsWith(expected));
    assertThat("SYSERR should have stacktrace", errContent.toString(), containsString("java.io.IOException: reason"));
  }

  @Test
  public void downloadAndValidateFile_resume_skip() throws Exception {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
//    Mockito.when(mockConn.downloadQuery(firstLocation, fullFilePath)).thenReturn(true);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(fullFilePath).when(downloaderSpy).prepareOutputLocation(wfile);
    Mockito.doReturn(true).when(downloaderSpy).tryResume(fullFilePath, wfile);
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();

    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));

    downloaderSpy.downloadAndValidateFile(wfile);
    verify(mockConn, never()).downloadQuery(firstLocation, fullFilePath);
    verify(downloaderSpy, times(1)).tryResume(fullFilePath, wfile);
    assertEquals("Wrong SYSOUT output", "file already retrieved: " + firstLocation + "\n", outContent.toString());
    assertEquals("No SYSERR output for success", "", errContent.toString());
  }

  private static final char SEP = File.separatorChar;
  private static final String FIXTURE_WARC_PATH = "test" + SEP + "fixtures" + SEP + "small-file.warc.gz";

  @Test
  public void tryResume_no_file() throws Exception {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = "somewhere";

    WasapiDownloaderSettings settingsSpy = Mockito.spy(defaultSettings());

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    downloaderSpy.settings = settingsSpy;

    Mockito.doReturn(false).when(settingsSpy).shouldResume();

    assertFalse("Should not resume when file does not exist", downloaderSpy.tryResume(fullFilePath, wfile));
    verify(downloaderSpy, never()).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);

  }

  @Test
  public void tryResume_not_resume() throws Exception {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = FIXTURE_WARC_PATH;

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));

    assertFalse("Should not resume when not set to resume", downloaderSpy.tryResume(fullFilePath, wfile));
    verify(downloaderSpy, never()).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
  }

  @Test
  public void tryResume_validate_fails() throws Exception {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    // Create a tempfile
    File fullFile = File.createTempFile("test", ".warc.gz");
    String fullFilePath = fullFile.getAbsolutePath();
    assertTrue("Warc exists before resume", fullFile.exists());

    WasapiDownloaderSettings settingsSpy = Mockito.spy(defaultSettings());

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    downloaderSpy.settings = settingsSpy;

    Mockito.doReturn(true).when(settingsSpy).shouldResume();
    Mockito.doReturn(false).when(downloaderSpy).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);


    assertFalse("Should not resume when file exists and checksums do not match", downloaderSpy.tryResume(fullFilePath, wfile));
    verify(downloaderSpy, times(1)).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
    assertFalse("Warc deleted", fullFile.exists());
  }

  @Test
  public void tryResume_success() throws Exception {
    WasapiFile wfile = new WasapiFile();
    String firstLocation = "out there";
    String[] locations = new String[]{firstLocation};
    wfile.setLocations(locations);
    String fullFilePath = FIXTURE_WARC_PATH;

    WasapiDownloaderSettings settingsSpy = Mockito.spy(defaultSettings());

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    downloaderSpy.settings = settingsSpy;

    Mockito.doReturn(true).when(settingsSpy).shouldResume();
    Mockito.doReturn(true).when(downloaderSpy).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);


    assertTrue("Should resume when file exists and checksums match", downloaderSpy.tryResume(fullFilePath, wfile));
    verify(downloaderSpy, times(1)).checksumValidate(defaultSettings().checksumAlgorithm(), wfile, fullFilePath);
  }

  private WasapiDownloaderSettings defaultSettings() throws SettingsLoadException {
    return new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
  }

  private int defaultNumRetries() throws NumberFormatException, SettingsLoadException {
    return Integer.valueOf(defaultSettings().retries());
  }
}
