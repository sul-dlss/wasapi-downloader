package edu.stanford.dlss.was;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.junit.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

/**
 * Tests for WasapiDownloader that do NOT require PowerMock
 *   We split out the PowerMock tets because jacoco at this time (2017-05-31) is unable to record coverage for
 *   PowerMock tests.  Thus, this splitting lets us get coverage stats for WasapiDownloader for those tests that don't need PowerMock
 */
public class TestWasapiDownloader {

  @Test
  public void constructor_loadsSettings() throws SettingsLoadException {
    WasapiDownloader myInstance = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    assertNotNull(myInstance.settings);
  }

  @Test
  @SuppressWarnings("checkstyle:NoWhitespaceAfter")
  public void main_withHelp_canExecuteWithoutCrashing() throws SettingsLoadException, IOException, NoSuchAlgorithmException {
    String[] args = { "-h" };
    WasapiDownloader.main(args);
  }

  @Test
  public void downloadSelectedWarcs_requestsFileSetResponse() throws Exception {
    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.pagedJsonQuery(anyString())).thenReturn(null);
    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();

    downloaderSpy.downloadSelectedWarcs();
    WasapiDownloaderSettings mySettings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    verify(mockConn).pagedJsonQuery(ArgumentMatchers.startsWith(mySettings.baseUrlString()));
  }

  @Test
  public void prepareOutputLocation_correctLocation() throws SettingsLoadException {
    WasapiDownloader wd = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    WasapiFile wfile = new WasapiFile();
    String collId = "123";
    wfile.setCollectionId(Integer.parseInt(collId));
    String crawlStartTime = "2017-01-01T00:00:00Z";
    wfile.setCrawlStartDateStr(crawlStartTime);
    String filename = "i_is_a_warc_file";
    wfile.setFilename(filename);
    String crawlId = "666";
    wfile.setCrawlId(Integer.parseInt(crawlId));
    WasapiDownloaderSettings mySettings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, null);

    String result = wd.prepareOutputLocation(wfile);
    String expected = mySettings.outputBaseDir() + "AIT_" + collId + "/" + crawlId + "/" + crawlStartTime + "/" + filename;
    assertEquals("Incorrect output location", expected, result);
  }

  @Test
  public void prepareOutputLocation_whenMissingJsonValues() throws SettingsLoadException {
    WasapiDownloader wd = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    String result = wd.prepareOutputLocation(new WasapiFile());
    assertThat(result, org.hamcrest.CoreMatchers.containsString("/AIT_0/")); // when missing collectionId
    assertThat(result, org.hamcrest.CoreMatchers.containsString("/0/")); // when missing crawlId
    assertThat(result, org.hamcrest.CoreMatchers.containsString("/null/")); // when missing crawlStartTime
    assertThat(result, org.hamcrest.CoreMatchers.endsWith("/null")); // when missing filename - gah!
  }

  @Test
  public void prepareOutputLocation_createsDirsAsNecessary() throws SettingsLoadException {
    WasapiDownloader wd = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    WasapiFile wfile = new WasapiFile();
    String collId = "111";
    wfile.setCollectionId(Integer.parseInt(collId));
    String crawlStartTime = "2017-01-01T00:01:02Z";
    wfile.setCrawlStartDateStr(crawlStartTime);
    String crawlId = "888";
    wfile.setCrawlId(Integer.parseInt(crawlId));

    WasapiDownloaderSettings mySettings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    String expected = mySettings.outputBaseDir() + "AIT_" + collId + "/" + crawlId + "/" + crawlStartTime;
    File expectedDir = new File(expected);

    wd.prepareOutputLocation(wfile);
    assertTrue("Directory should exist: " + expected, expectedDir.exists());
  }

  @Test
  public void checksumValidate_missingChecksumPrintsErrorAndReturnsFalse() throws SettingsLoadException, NoSuchAlgorithmException, IOException {
    WasapiFile wfile = new WasapiFile();
    String expectedChecksum = "666";
    HashMap<String, String> checksumsMap = new HashMap<String, String>();
    checksumsMap.put("sha1", expectedChecksum);
    wfile.setChecksums(checksumsMap);

    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));

    WasapiDownloader wd = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    assertFalse("result of checksumValidate for missing checksum should be false", wd.checksumValidate("md5", wfile, "fullFilePath"));
    assertEquals("Wrong SYSERR output", "No checksum of type: md5 available: {sha1=666}\n", errContent.toString());
  }

  @Test
  public void checksumValidate_unsupportedAlgorithmReturnsFalse() throws SettingsLoadException, NoSuchAlgorithmException, IOException {
    WasapiFile wfile = new WasapiFile();
    String expectedChecksum = "666";
    HashMap<String, String> checksumsMap = new HashMap<String, String>();
    checksumsMap.put("foo", expectedChecksum);
    wfile.setChecksums(checksumsMap);

    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errContent));

    WasapiDownloader wd = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    assertFalse("result of checksumValidate for unsupported algorithm should be false", wd.checksumValidate("foo", wfile, "fullFilePath"));
    assertEquals("Wrong SYSERR output", "Unsupported checksum algorithm: foo.  Options are 'md5' or 'sha1'\n", errContent.toString());
  }
}
