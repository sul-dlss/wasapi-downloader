package edu.stanford.dlss.was;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.IntegerValidator;

public class WasapiDownloader {
  public static final String SETTINGS_FILE_LOCATION = "config/settings.properties";

  public WasapiDownloaderSettings settings;

  private WasapiConnection wasapiConn;


  public WasapiDownloader(String settingsFileLocation, String[] args) throws SettingsLoadException {
    settings = new WasapiDownloaderSettings(settingsFileLocation, args);
  }

  public void executeFromCmdLine() throws IOException {
    if (settings.shouldDisplayHelp()) {
      System.out.print(settings.getHelpAndSettingsMessage());
      return;
    }

    downloadSelectedWarcs();
  }

  // package level method for testing
  WasapiConnection getWasapiConn() throws IOException {
    if (wasapiConn == null)
      wasapiConn = new WasapiConnection(new WasapiClient(settings));
    return wasapiConn;
  }

  // package level method for testing
  void downloadSelectedWarcs() throws IOException {
    // System.out.println("DEBUG: about to request " + getFileListRequestUrl());
    WasapiResponse wasapiResp = getWasapiConn().jsonQuery(getFileListRequestUrl());
    // System.out.println(wasapiResp.toString());

    if (wasapiResp != null) {
      WasapiCrawlSelector crawlSelector = new WasapiCrawlSelector(wasapiResp.getFiles());
      for (Integer crawlId : desiredCrawlIds(crawlSelector)) {
        for (WasapiFile file : crawlSelector.getFilesForCrawl(crawlId)) {
          // TODO:  make a separate method for downloading individual file?
          System.out.println("We will eventually download " + file.toString());
        }
      }
    }
  }

  private List<Integer> desiredCrawlIds(WasapiCrawlSelector crawlSelector) {
    String crawlsAfter = settings.crawlStartAfter();
    if (!WasapiDownloaderSettings.isNullOrEmpty(crawlsAfter)) {
      // TODO:  be sure date format will work:  web-archiving#49
      return crawlSelector.getSelectedCrawlIds(crawlsAfter);
    }
    else {
      // TODO: want cleaner grab of int from settings: wasapi-downloader#83
      Integer myInteger = IntegerValidator.getInstance().validate(settings.jobIdLowerBound());
      if (myInteger != null) {
        int jobsAfter = myInteger.intValue();
        return crawlSelector.getSelectedCrawlIds(jobsAfter);
      }
    }
    return crawlSelector.getSelectedCrawlIds(0); // all crawl ids
  }

  private String getFileListRequestUrl() {
    StringBuilder sb = new StringBuilder(settings.baseUrlString() + "webdata?");
    List<String> params = requestParams();
    if (!params.isEmpty()) {
      for (String paramArg : params) {
        sb.append(paramArg + "&");
      }
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  private List<String> requestParams() {
    List<String> params = new ArrayList<String>();
    if (settings.collectionId() != null)
      params.add("collection=" + settings.collectionId());
    if (settings.crawlStartAfter() != null)
      params.add("crawl-start-after=" + settings.crawlStartAfter());
    if (settings.crawlStartBefore()!= null)
      params.add("crawl-start-before=" + settings.crawlStartBefore());
    if (settings.jobId() != null)
      params.add("crawl=" + settings.jobId());
    return params;
  }

  @SuppressWarnings("checkstyle:UncommentedMain")
  public static void main(String[] args) throws SettingsLoadException, IOException {
    WasapiDownloader downloader = new WasapiDownloader(SETTINGS_FILE_LOCATION, args);
    downloader.executeFromCmdLine();
  }
}
