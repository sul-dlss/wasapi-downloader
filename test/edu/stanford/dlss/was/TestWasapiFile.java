package edu.stanford.dlss.was;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.*;

import static org.hamcrest.CoreMatchers.*;

public class TestWasapiFile {

  private static final char SEP = File.separatorChar;

  @Test
  public void toStringImplemented() throws IOException {
    FileInputStream fis = new FileInputStream("test" + SEP + "fixtures" + SEP + "webdata_filename_response.json");
    WasapiFile myFile = new WasapiResponseParser().parse(fis).getFiles()[0];
    String fileAsString = myFile.toString();
    assertThat(fileAsString, containsString("filename: ARCHIVEIT-5425-CRAWL_SELECTED_SEEDS-JOB299019-20170504225811451-00000.warc.gz"));
    assertThat(fileAsString, containsString("filetype: warc"));
    assertThat(fileAsString, containsString("size: 231145356"));
    assertThat(fileAsString, containsString("locations:"));
    String url = "https://partner.archive-it.org/webdatafile/ARCHIVEIT-5425-CRAWL_SELECTED_SEEDS-JOB299019-20170504225811451-00000.warc.gz";
    assertThat(fileAsString, containsString("location 0: " + url));
    assertThat(fileAsString, containsString("checksums:"));
    assertThat(fileAsString, containsString("sha1 checksum: 24cf2dd655cda1000349282dd0193a32642cb751"));
    assertThat(fileAsString, containsString("md5 checksum: 70c9a0ae2d7b90a64bd7fc000fae5277"));
    assertThat(fileAsString, containsString("account: 925"));
    assertThat(fileAsString, containsString("collection: 5425"));
    assertThat(fileAsString, containsString("crawl: 299019"));
    assertThat(fileAsString, containsString("crawl_start: 2017-05-04T22:58:04Z"));
    assertThat(fileAsString, containsString("crawl_time: 2014-07-31T04:18:21Z"));
  }
}
