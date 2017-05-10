package edu.stanford.dlss.was;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class corresponding to JSON returned by WASAPI that represents WebdataFile
 * @see https://github.com/WASAPI-Community/data-transfer-apis/blob/master/ait-implementation/wasapi/implemented-swagger.yaml#L132-L183
 */
public class WasapiFile {

  @JsonProperty("account")
  private int accountId;

  @JsonProperty("checksum")
  private String checksumsStr;

  @JsonProperty("collection")
  private int collectionId;

  @JsonProperty("crawl")
  private int crawlId;

  @JsonProperty("crawl_start")
  private String crawlStartDateStr;

  private String filename;
  private String filetype;
  private String[] locations;
  private int size;


  public WasapiFile() {
  }

  public int getAccountId()
  {
    return accountId;
  }
  public void setAccountId(int accountId)
  {
    this.accountId = accountId;
  }

  public String getChecksumsStr()
  {
    return checksumsStr;
  }
  public void setChecksumsStr(String checksumsStr)
  {
    this.checksumsStr = checksumsStr;
  }

  public int getCollectionId()
  {
    return collectionId;
  }
  public void setCollectionId(int collectionId)
  {
    this.collectionId = collectionId;
  }

  public int getCrawlId()
  {
    return crawlId;
  }
  public void setCrawlId(int crawlId)
  {
    this.crawlId = crawlId;
  }

  public String getCrawlStartDateStr()
  {
    return crawlStartDateStr;
  }
  public void setCrawlStartDateStr(String crawlStartDateStr)
  {
    this.crawlStartDateStr = crawlStartDateStr;
  }

  public String getFilename()
  {
    return filename;
  }
  public void setFilename(String filename)
  {
    this.filename = filename;
  }

  public String getFiletype()
  {
    return filetype;
  }
  public void setFiletype(String filetype)
  {
    this.filetype = filetype;
  }

  public String[] getLocations()
  {
    return locations;
  }
  public void setLocations(String[] locations)
  {
    this.locations = locations;
  }

  public int getSize()
  {
    return size;
  }
  public void setSize(int size)
  {
    this.size = size;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("***** Wasapi File Details *****\n");
    sb.append("filename: " + getFilename() + "\n");
    sb.append("checksum: " + getChecksumsStr() + "\n");
    sb.append("filetype: " + getFiletype() + "\n");
    sb.append("locations:\n");
    for (int i = 0; i < getLocations().length; i++) {
      sb.append("  location " + Integer.toString(i) + ": " + locations[i].toString() + "\n");
    }
    sb.append("size: " + Integer.toString(getSize()) + "\n");
    sb.append("account: " + Integer.toString(getAccountId()) + "\n");
    sb.append("collection: " + Integer.toString(getCollectionId()) + "\n");
    sb.append("crawl: " + Integer.toString(getCrawlId()) + "\n");
    sb.append("crawl_start: " + getCrawlStartDateStr() + "\n");
    return sb.toString();
  }
}
