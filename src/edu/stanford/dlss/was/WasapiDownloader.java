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
    // System.out.println("DEBUG: about to request " + getFileSetRequestUrl());
    WasapiResponse wasapiResp = getWasapiConn().jsonQuery(getFileSetRequestUrl());
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
    // TODO: want cleaner grab of int from settings: wasapi-downloader#83
    Integer myInteger = IntegerValidator.getInstance().validate(settings.jobIdLowerBound());
    if (myInteger != null) {
      int jobsAfter = myInteger.intValue();
      return crawlSelector.getSelectedCrawlIds(jobsAfter);
    }
    else
      return crawlSelector.getSelectedCrawlIds(0); // all returns all crawl ids from FileSet
  }

  private String getFileSetRequestUrl() {
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

    // If a filename is provided, other arguments are ignored
    if (settings.filename() != null) {
      params.add("filename=" + settings.filename());
      return params;
    }

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
