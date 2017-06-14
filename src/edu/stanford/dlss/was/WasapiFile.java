package edu.stanford.dlss.was;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings({"checkstyle:LineLength", "checkstyle:MethodCount", "checkstyle:MethodLength"})
/**
 * Class corresponding to JSON returned by WASAPI that represents WebdataFile
 * @see https://github.com/WASAPI-Community/data-transfer-apis/blob/master/ait-implementation/wasapi/implemented-swagger.yaml#L132-L183
 */
public class WasapiFile {

  @JsonProperty("account")
  private int accountId;

  private Map<String, String> checksums;

  @JsonProperty("collection")
  private int collectionId;

  @JsonProperty("crawl")
  private int crawlId;

  @JsonProperty("crawl-start")
  private String crawlStartDateStr;

  @JsonProperty("crawl-time")
  private String crawlTimeDateStr;

  private String filename;
  private String filetype;
  private String[] locations;
  private long size;

  public int getAccountId() {
    return accountId;
  }
  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }

  public Map<String, String> getChecksums() {
    return checksums;
  }
  public void setChecksums(Map<String, String> checksums) {
    this.checksums = checksums;
  }

  public int getCollectionId() {
    return collectionId;
  }
  public void setCollectionId(int collectionId) {
    this.collectionId = collectionId;
  }

  public int getCrawlId() {
    return crawlId;
  }
  public void setCrawlId(int crawlId) {
    this.crawlId = crawlId;
  }

  public String getCrawlStartDateStr() {
    return crawlStartDateStr;
  }
  public void setCrawlStartDateStr(String crawlStartDateStr) {
    this.crawlStartDateStr = crawlStartDateStr;
  }

  public String getCrawlTimeDateStr() {
    return crawlTimeDateStr;
  }
  public void setCrawlTimeDateStr(String crawlTimeDateStr) {
    this.crawlTimeDateStr = crawlTimeDateStr;
  }

  public String getFilename() {
    return filename;
  }
  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getFiletype() {
    return filetype;
  }
  public void setFiletype(String filetype) {
    this.filetype = filetype;
  }

  public String[] getLocations() {
    return locations;
  }
  public void setLocations(String[] locations) {
    this.locations = locations;
  }

  public long getSize() {
    return size;
  }
  public void setSize(long size) {
    this.size = size;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("***** Wasapi File Details *****\n");
    sb.append("filename: " + getFilename() + "\n");
    sb.append("filetype: " + getFiletype() + "\n");
    sb.append("size: " + Long.toString(getSize()) + "\n");
    sb.append("locations:\n");
    for (int i = 0; i < getLocations().length; i++) {
      sb.append("  location " + Integer.toString(i) + ": " + locations[i].toString() + "\n");
    }
    sb.append("checksums:\n");
    for (String key : checksums.keySet()) {
      sb.append("  " + key + " checksum: " + checksums.get(key) + "\n");
    }
    sb.append("account: " + Integer.toString(getAccountId()) + "\n");
    sb.append("collection: " + Integer.toString(getCollectionId()) + "\n");
    sb.append("crawl: " + Integer.toString(getCrawlId()) + "\n");
    sb.append("crawl_start: " + getCrawlStartDateStr() + "\n");
    sb.append("crawl-time: " + getCrawlTimeDateStr() + "\n");
    return sb.toString();
  }
}
