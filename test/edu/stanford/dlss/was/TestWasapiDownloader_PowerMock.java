package edu.stanford.dlss.was;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WasapiDownloader.class, WasapiValidator.class})
@SuppressWarnings("TypeName")
/**
 * Tests for WasapiDownloader that require PowerMock
 *   split out because jacoco at this time (2017-05-31) is unable to record coverage for PowerMock tests ...
 *   thus, this splitting lets us get coverage stats for WasapiDownloader for those tests that don't need PowerMock
 */
public class TestWasapiDownloader_PowerMock {

  @Test
  public void main_callsExecutFromCmdLine() throws Exception {
    WasapiDownloader mockDownloader = PowerMockito.mock(WasapiDownloader.class);
    PowerMockito.whenNew(WasapiDownloader.class).withAnyArguments().thenReturn(mockDownloader);

    WasapiDownloader.main(null);
    verify(mockDownloader).executeFromCmdLine();
  }

  @Test
  public void main_executesFileSetRequest_usesAllAppropArgsSettings() throws Exception {
    String[] args = {"--collectionId", "123", "--crawlId=456",
        "--crawlStartAfter", "2014-03-14", "--crawlStartBefore=2017-03-14", "--username=Fred" };
    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.pagedJsonQuery(anyString())).thenReturn(null);
    WasapiDownloader downloaderSpy = PowerMockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, args));
    PowerMockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();
    PowerMockito.whenNew(WasapiDownloader.class).withAnyArguments().thenReturn(downloaderSpy);

    WasapiDownloader.main(args);
    verify(mockConn).pagedJsonQuery(ArgumentMatchers.contains("collection=123"));
    verify(mockConn).pagedJsonQuery(ArgumentMatchers.contains("crawl=456"));
    verify(mockConn).pagedJsonQuery(ArgumentMatchers.contains("crawl-start-after=2014-03-14"));
    verify(mockConn).pagedJsonQuery(ArgumentMatchers.contains("crawl-start-before=2017-03-14"));
    // username is used in login request
    verify(mockConn, Mockito.never()).pagedJsonQuery(ArgumentMatchers.contains("username=Fred"));
    // output directory is not part of wasapi request
    verify(mockConn, Mockito.never()).pagedJsonQuery(ArgumentMatchers.contains(WasapiDownloaderSettings.OUTPUT_BASE_DIR_PARAM_NAME));
  }

  @Test
  public void main_executesFileSetRequest_onlyUsesArgsSettings() throws Exception {
    String[] args = {"--collectionId", "123" };
    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.pagedJsonQuery(anyString())).thenReturn(null);
    WasapiDownloader downloaderSpy = PowerMockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, args));
    PowerMockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();
    PowerMockito.whenNew(WasapiDownloader.class).withAnyArguments().thenReturn(downloaderSpy);

    WasapiDownloader.main(args);
    verify(mockConn).pagedJsonQuery(ArgumentMatchers.contains("collection=123"));
    verify(mockConn, Mockito.never()).pagedJsonQuery(ArgumentMatchers.contains("crawl="));
    verify(mockConn, Mockito.never()).pagedJsonQuery(ArgumentMatchers.contains("crawl-start-after="));
    verify(mockConn, Mockito.never()).pagedJsonQuery(ArgumentMatchers.contains("crawl-start-before="));
  }

  @Test
  public void main_singleFileDownload_onlyUsesFilename() throws Exception {
    String[] args = {"--collectionId", "123", "--filename", "ARCHIVEIT-5425-MONTHLY-JOB302671-20170526114117181-00049.warc.gz" };
    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.pagedJsonQuery(anyString())).thenReturn(null);
    WasapiDownloader downloaderSpy = PowerMockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, args));
    PowerMockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();
    PowerMockito.whenNew(WasapiDownloader.class).withAnyArguments().thenReturn(downloaderSpy);

    WasapiDownloader.main(args);
    verify(mockConn).pagedJsonQuery(ArgumentMatchers.contains("filename=ARCHIVEIT-5425-MONTHLY-JOB302671-20170526114117181-00049.warc.gz"));
    verify(mockConn, Mockito.never()).pagedJsonQuery(ArgumentMatchers.contains("crawl="));
    verify(mockConn, Mockito.never()).pagedJsonQuery(ArgumentMatchers.contains("crawl-start-after="));
    verify(mockConn, Mockito.never()).pagedJsonQuery(ArgumentMatchers.contains("crawl-start-before="));
    verify(mockConn, Mockito.never()).pagedJsonQuery(ArgumentMatchers.contains("collection="));
  }

  private List<WasapiResponse> getWasapiRespList() {
    List<WasapiResponse> wasapiRespList = new ArrayList<WasapiResponse>();
    wasapiRespList.add(new WasapiResponse());
    return wasapiRespList;
  }

  @Test
  public void downloadSelectedWarcs_usesCrawlSelector() throws Exception {
    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    List<WasapiResponse> wasapiRespList = getWasapiRespList();
    Mockito.when(mockConn.pagedJsonQuery(anyString())).thenReturn(wasapiRespList);

    WasapiCrawlSelector mockCrawlSelector = PowerMockito.mock(WasapiCrawlSelector.class);
    List<Integer> desiredCrawlIds = new ArrayList<Integer>();
    desiredCrawlIds.add(Integer.valueOf("666"));
    PowerMockito.when(mockCrawlSelector.getSelectedCrawlIds(0)).thenReturn(desiredCrawlIds);
    PowerMockito.whenNew(WasapiCrawlSelector.class).withArguments(wasapiRespList).thenReturn(mockCrawlSelector);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();

    downloaderSpy.downloadSelectedWarcs();
    verify(mockCrawlSelector).getSelectedCrawlIds(0); // no command line args means it gets all crawl ids like this
    verify(mockCrawlSelector).getFilesForCrawl(anyInt());
  }

  @Test
  @SuppressWarnings("checkstyle:MethodLength")
  public void downloadSelectedWarcs_callsDownloadAndValidateFile() throws Exception {
    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    List<WasapiResponse> wasapiRespList = getWasapiRespList();
    Mockito.when(mockConn.pagedJsonQuery(anyString())).thenReturn(wasapiRespList);

    WasapiCrawlSelector mockCrawlSelector = PowerMockito.mock(WasapiCrawlSelector.class);
    List<Integer> desiredCrawlIds = new ArrayList<Integer>();
    desiredCrawlIds.add(Integer.valueOf("666"));
    PowerMockito.when(mockCrawlSelector.getSelectedCrawlIds(0)).thenReturn(desiredCrawlIds);
    List<WasapiFile> filesForCrawl = new ArrayList<WasapiFile>();
    WasapiFile wfile = new WasapiFile();
    String filename = "i_is_a_warc_file";
    wfile.setFilename(filename);
    filesForCrawl.add(wfile);
    PowerMockito.when(mockCrawlSelector.getFilesForCrawl(666)).thenReturn(filesForCrawl);
    PowerMockito.whenNew(WasapiCrawlSelector.class).withAnyArguments().thenReturn(mockCrawlSelector);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();
    Mockito.doNothing().when(downloaderSpy).downloadAndValidateFile(wfile);

    downloaderSpy.downloadSelectedWarcs();
    verify(downloaderSpy).downloadAndValidateFile(wfile);
  }

  @Test
  @SuppressWarnings("checkstyle:NoWhitespaceAfter")
  public void downloadSelectedWarcs_byCrawlIdLowerBound() throws Exception {
    String argValue = "666";
    String[] args = { "--crawlIdLowerBound=" + argValue };

    WasapiCrawlSelector mockCrawlSelector = PowerMockito.mock(WasapiCrawlSelector.class);
    List<WasapiResponse> wasapiRespList = getWasapiRespList();
    PowerMockito.whenNew(WasapiCrawlSelector.class).withArguments(wasapiRespList).thenReturn(mockCrawlSelector);

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.pagedJsonQuery(anyString())).thenReturn(wasapiRespList);
    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, args));
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();

    downloaderSpy.downloadSelectedWarcs();
    verify(mockCrawlSelector).getSelectedCrawlIds(Integer.valueOf(argValue));
  }

  @Test
  public void checksumValidate_md5_calls_wasapiValidator_validateMd5() throws SettingsLoadException, NoSuchAlgorithmException, IOException {
    WasapiFile wfile = new WasapiFile();
    String expectedChecksum = "666";
    HashMap<String, String> checksumsMap = new HashMap<String, String>();
    checksumsMap.put("md5", expectedChecksum);
    wfile.setChecksums(checksumsMap);

    PowerMockito.mockStatic(WasapiValidator.class);
    Mockito.when(WasapiValidator.validateMd5(expectedChecksum, anyString())).thenReturn(anyBoolean());

    WasapiDownloader wd = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    wd.checksumValidate("md5", wfile, anyString());

    PowerMockito.verifyStatic();
    WasapiValidator.validateMd5(expectedChecksum, "");
  }

  @Test
  public void checksumValidate_sha1_calls_wasapiValidator_validateSha1() throws SettingsLoadException, NoSuchAlgorithmException, IOException {
    WasapiFile wfile = new WasapiFile();
    String expectedChecksum = "666";
    HashMap<String, String> checksumsMap = new HashMap<String, String>();
    checksumsMap.put("sha1", expectedChecksum);
    wfile.setChecksums(checksumsMap);

    PowerMockito.mockStatic(WasapiValidator.class);
    Mockito.when(WasapiValidator.validateSha1(expectedChecksum, anyString())).thenReturn(anyBoolean());

    WasapiDownloader wd = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    wd.checksumValidate("sha1", wfile, anyString());

    PowerMockito.verifyStatic();
    WasapiValidator.validateSha1(expectedChecksum, "");
  }
}
