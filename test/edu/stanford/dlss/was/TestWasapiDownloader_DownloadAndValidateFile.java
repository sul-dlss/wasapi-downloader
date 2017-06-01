package edu.stanford.dlss.was;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.hamcrest.core.StringStartsWith;
import org.junit.*;
import org.mockito.Mockito;

/**
 * WasapiDownloader tests for downloadAndValidateFile() method
 */
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

  private WasapiDownloaderSettings defaultSettings() throws SettingsLoadException {
    return new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
  }

  private int defaultNumRetries() throws NumberFormatException, SettingsLoadException {
    return Integer.valueOf(defaultSettings().retries());
  }
}
