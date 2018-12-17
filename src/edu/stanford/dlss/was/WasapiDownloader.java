package edu.stanford.dlss.was;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;

@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class WasapiDownloader {
  public static final String SETTINGS_FILE_LOCATION = "config/settings.properties";
  private static final char SEP = File.separatorChar;

  public WasapiDownloaderSettings settings;

  private WasapiConnection wasapiConn;


  public WasapiDownloader(String settingsFileLocation, String[] args) throws SettingsLoadException {
    settings = new WasapiDownloaderSettings(settingsFileLocation, args);
  }

  public void executeFromCmdLine() throws IOException, NoSuchAlgorithmException {
    if (settings.shouldDisplayHelp()) {
      System.out.print(settings.getHelpAndSettingsMessage());
      return;
    }

    downloadSelectedWarcs();
  }

  // package level method for testing
  void downloadSelectedWarcs() throws IOException, NoSuchAlgorithmException {
    // System.out.println("DEBUG: about to request " + getFileSetRequestUrl());
    List<WasapiResponse> wasapiRespList = getWasapiConn().pagedJsonQuery(getFileSetRequestUrl());
    // System.out.println(wasapiResp.toString());

    if (wasapiRespList != null && wasapiRespList.get(0) != null) {
      WasapiCrawlSelector crawlSelector = new WasapiCrawlSelector(wasapiRespList);
      for (Integer crawlId : desiredCrawlIds(crawlSelector)) {
        for (WasapiFile file : crawlSelector.getFilesForCrawl(crawlId)) {
          downloadAndValidateFile(file);
        }
      }
    }
  }

  // package level method for testing
  WasapiConnection getWasapiConn() throws IOException {
    if (wasapiConn == null)
      wasapiConn = new WasapiConnection(new WasapiClient(settings));
    return wasapiConn;
  }

  // package level method for testing
  @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:CyclomaticComplexity"})
  void downloadAndValidateFile(WasapiFile file) throws NoSuchAlgorithmException {
    String fullFilePath = prepareOutputLocation(file);
    if (fullFilePath == null) {
      // should never get here, except in testing
      System.err.println("fullFilePath is null - can't retrieve file");
      return;
    }
    int numRetries = Integer.parseInt(settings.retries());
    int attempts = 0;
    boolean checksumValidated = false;
    if (tryResume(fullFilePath, file)) {
      checksumValidated = true;
      System.out.println("file already retrieved: " + file.getLocations()[0]);
    }
    while (attempts <= numRetries && !checksumValidated) {
      attempts++;
      try {
        boolean downloadSuccess = getWasapiConn().downloadQuery(file.getLocations()[0], fullFilePath);
        if (downloadSuccess && checksumValidate(settings.checksumAlgorithm(), file, fullFilePath)) {
          System.out.println("file retrieved successfully: " + file.getLocations()[0]);
          checksumValidated = true; // break out of loop
        }
      } catch (HttpResponseException e) {
        String prefix = "ERROR: HttpResponseException (" + e.getMessage() + ") downloading file (will not retry): ";
        System.err.println(prefix + file.getLocations()[0]);
        System.err.println(" HTTP ResponseCode was " + e.getStatusCode());
        attempts = numRetries + 1;  // no more attempts
      } catch (ClientProtocolException e) {
        String prefix = "ERROR: ClientProtocolException (" + e.getMessage() + ") downloading file (will not retry): ";
        System.err.println(prefix + file.getLocations()[0]);
        attempts = numRetries + 1;  // no more attempts
      } catch (IOException e) {
        // swallow exception and try again - it may be a network issue
        System.err.println("WARNING: exception downloading file (will retry): " + file.getLocations()[0]);
        e.printStackTrace(System.err);
      }
    }

    if (attempts == numRetries + 1) // RE-tries, not number of attempts
      System.err.println("file not retrieved or unable to validate checksum: " + file.getLocations()[0]);
  }

  boolean tryResume(String fullFilePath, WasapiFile file) throws NoSuchAlgorithmException {
    if (!settings.shouldResume())
      return false;

    File fullFile = new File(fullFilePath);
    if (!fullFile.exists())
      return false;

    if (!checksumValidate(settings.checksumAlgorithm(), file, fullFilePath)) {
      fullFile.delete();
      return false;
    }
    return true;
  }

  // package level method for testing
  String prepareOutputLocation(WasapiFile file) {
    String outputPath = settings.outputBaseDir() + "AIT_" + file.getCollectionId() +
        SEP + file.getCrawlId() + SEP + file.getCrawlStartDateStr();
    new File(outputPath).mkdirs();
    return outputPath + SEP + file.getFilename();
  }

  // package level method for testing
  boolean checksumValidate(String algorithm, WasapiFile file, String fullFilePath) throws NoSuchAlgorithmException {
    String checksum = file.getChecksums().get(algorithm);
    if (checksum == null) {
      System.err.println("No checksum of type: " + algorithm + " available: " + file.getChecksums().toString());
      return false;
    }

    try {
      if ("md5".equals(algorithm))
        return WasapiValidator.validateMd5(checksum, fullFilePath);
      else if ("sha1".equals(algorithm))
        return WasapiValidator.validateSha1(checksum, fullFilePath);
      else {
        System.err.println("Unsupported checksum algorithm: " + algorithm + ".  Options are 'md5' or 'sha1'");
      }
    } catch (IOException e) {
      // Somethings wrong, so fail validate
    }
    return false;
  }

  private List<Integer> desiredCrawlIds(WasapiCrawlSelector crawlSelector) {
    // TODO: want cleaner grab of int from settings: wasapi-downloader#83
    Integer myInteger = IntegerValidator.getInstance().validate(settings.crawlIdLowerBound());
    if (myInteger != null) {
      int crawlsAfter = myInteger.intValue();
      return crawlSelector.getSelectedCrawlIds(crawlsAfter);
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
    if (settings.crawlId() != null)
      params.add("crawl=" + settings.crawlId());
    return params;
  }

  @SuppressWarnings("checkstyle:UncommentedMain")
  public static void main(String[] args) throws SettingsLoadException, IOException, NoSuchAlgorithmException {
    WasapiDownloader downloader = new WasapiDownloader(SETTINGS_FILE_LOCATION, args);
    downloader.executeFromCmdLine();
  }
}
