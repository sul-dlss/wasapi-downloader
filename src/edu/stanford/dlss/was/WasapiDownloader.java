package edu.stanford.dlss.was;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    // System.out.println("DEBUG: about to request " + getFileListRequestUrl());
    WasapiResponse wasapiResp = getWasapiConn().jsonQuery(getFileListRequestUrl());
    // System.out.println(wasapiResp.toString());

    // TODO:
    // use WasapiCrawlSelector to select crawls with files
    // download files for each selected crawl
  }

  WasapiConnection getWasapiConn() throws IOException {
    if (wasapiConn == null)
      wasapiConn = new WasapiConnection(new WasapiClient(settings));
    return wasapiConn;
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
