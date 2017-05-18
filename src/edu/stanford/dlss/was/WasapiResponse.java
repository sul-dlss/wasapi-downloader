package edu.stanford.dlss.was;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class corresponding to JSON received after making WASAPI request ("FileSet")
 * @see https://github.com/WASAPI-Community/data-transfer-apis/blob/master/ait-implementation/wasapi/implemented-swagger.yaml#L184-L213
 */
public class WasapiResponse {

  private int count;

  @JsonProperty("includes-extra")
  private boolean includesExtra;

  private String next;
  private String previous;
  private WasapiFile[] files;

  public int getCount() {
    return count;
  }
  public void setCount(int count) {
    this.count = count;
  }

  public String getPrevious() {
    return previous;
  }
  public void setPrevious(String previous) {
    this.previous = previous;
  }

  public String getNext() {
    return next;
  }
  public void setNext(String next) {
    this.next = next;
  }

  public boolean isIncludesExtra() {
    return includesExtra;
  }
  public void setIncludesExtra(boolean includesExtra) {
    this.includesExtra = includesExtra;
  }

  public WasapiFile[] getFiles() {
    return files;
  }
  public void setFiles(WasapiFile[] files) {
    this.files = files;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("***** Wasapi Response Details *****\n");
    sb.append("count: " + Integer.toString(getCount()) + "\n");
    sb.append("previous: " + getPrevious() + "\n");
    sb.append("next: " + getNext() + "\n");
    sb.append("includes-extra: " + Boolean.toString(isIncludesExtra()) + "\n");
    sb.append("files:\n");
    for (int i = 0; i < files.length; i++) {
      sb.append("  file " + Integer.toString(i) + ":\n");
      sb.append("    " + files[i].toString());
    }
    return sb.toString();
  }
}
