package edu.stanford.dlss.was;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(WasapiDownloader.class)
public class TestWasapiDownloader {

  @Test
  public void constructor_loadsSettings() throws SettingsLoadException {
    WasapiDownloader myInstance = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    assertNotNull(myInstance.settings);
  }

  @Test
  public void main_callsExecutFromCmdLine() throws Exception {
    WasapiDownloader mockDownloader = PowerMockito.mock(WasapiDownloader.class);
    PowerMockito.whenNew(WasapiDownloader.class).withAnyArguments().thenReturn(mockDownloader);

    WasapiDownloader.main(null);
    verify(mockDownloader).executeFromCmdLine();
  }

  @Test
  @SuppressWarnings("checkstyle:NoWhitespaceAfter")
  public void main_withHelp_canExecuteWithoutCrashing() throws SettingsLoadException, IOException {
    String[] args = { "-h" };
    WasapiDownloader.main(args);
  }

  @Test
  public void main_executesFileListRequest_usesAllAppropArgsSettings() throws Exception {
    String[] args = {"--collectionId", "123", "--jobId=456", "--crawlStartAfter", "2014-03-14", "--crawlStartBefore=2017-03-14", "--username=Fred" };
    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.jsonQuery(anyString())).thenReturn(null);
    WasapiDownloader downloaderSpy = PowerMockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, args));
    PowerMockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();
    PowerMockito.whenNew(WasapiDownloader.class).withAnyArguments().thenReturn(downloaderSpy);

    WasapiDownloader.main(args);
    verify(mockConn).jsonQuery(ArgumentMatchers.contains("collection=123"));
    verify(mockConn).jsonQuery(ArgumentMatchers.contains("crawl=456"));
    verify(mockConn).jsonQuery(ArgumentMatchers.contains("crawl-start-after=2014-03-14"));
    verify(mockConn).jsonQuery(ArgumentMatchers.contains("crawl-start-before=2017-03-14"));
    // username is used in login request
    verify(mockConn, Mockito.never()).jsonQuery(ArgumentMatchers.contains("username=Fred"));
    // output directory is not part of wasapi request
    verify(mockConn, Mockito.never()).jsonQuery(ArgumentMatchers.contains(WasapiDownloaderSettings.OUTPUT_BASE_DIR_PARAM_NAME));
  }

  @Test
  public void main_executesFileListRequest_onlyUsesArgsSettings() throws Exception {
    String[] args = {"--collectionId", "123" };
    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.jsonQuery(anyString())).thenReturn(null);
    WasapiDownloader downloaderSpy = PowerMockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, args));
    PowerMockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();
    PowerMockito.whenNew(WasapiDownloader.class).withAnyArguments().thenReturn(downloaderSpy);

    WasapiDownloader.main(args);
    verify(mockConn).jsonQuery(ArgumentMatchers.contains("collection=123"));
    verify(mockConn, Mockito.never()).jsonQuery(ArgumentMatchers.contains("crawl="));
    verify(mockConn, Mockito.never()).jsonQuery(ArgumentMatchers.contains("crawl-start-after="));
    verify(mockConn, Mockito.never()).jsonQuery(ArgumentMatchers.contains("crawl-start-before="));
  }

  @Test
  public void downloadSelectedWarcs_requestsFileListResponse() throws Exception {
    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.jsonQuery(anyString())).thenReturn(null);
    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();

    downloaderSpy.downloadSelectedWarcs();
    WasapiDownloaderSettings mySettings = new WasapiDownloaderSettings(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    verify(mockConn).jsonQuery(ArgumentMatchers.startsWith(mySettings.baseUrlString()));
  }

  @Test
  public void downloadSelectedWarcs_usesCrawlSelector() throws Exception {
    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.jsonQuery(anyString())).thenReturn(new WasapiResponse());

    WasapiCrawlSelector mockCrawlSelector = PowerMockito.mock(WasapiCrawlSelector.class);
    List<Integer> desiredCrawlIds = new ArrayList<Integer>();
    desiredCrawlIds.add(Integer.valueOf("666"));
    PowerMockito.when(mockCrawlSelector.getSelectedCrawlIds(0)).thenReturn(desiredCrawlIds);
    PowerMockito.whenNew(WasapiCrawlSelector.class).withAnyArguments().thenReturn(mockCrawlSelector);

    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null));
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();

    downloaderSpy.downloadSelectedWarcs();
    verify(mockCrawlSelector).getSelectedCrawlIds(0); // no command line args means it gets all crawl ids like this
    verify(mockCrawlSelector).getFilesForCrawl(anyInt());
  }

  @Test
  @SuppressWarnings("checkstyle:NoWhitespaceAfter")
  public void downloadSelectedWarcs_byJobIdLowerBound() throws Exception {
    String argValue = "666";
    String[] args = { "--jobIdLowerBound=" + argValue };

    WasapiCrawlSelector mockCrawlSelector = PowerMockito.mock(WasapiCrawlSelector.class);
    PowerMockito.whenNew(WasapiCrawlSelector.class).withAnyArguments().thenReturn(mockCrawlSelector);

    WasapiConnection mockConn = Mockito.mock(WasapiConnection.class);
    Mockito.when(mockConn.jsonQuery(anyString())).thenReturn(new WasapiResponse());
    WasapiDownloader downloaderSpy = Mockito.spy(new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, args));
    Mockito.doReturn(mockConn).when(downloaderSpy).getWasapiConn();

    downloaderSpy.downloadSelectedWarcs();
    verify(mockCrawlSelector).getSelectedCrawlIds(Integer.valueOf(argValue));
  }

}
