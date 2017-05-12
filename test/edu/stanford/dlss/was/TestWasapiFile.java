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
    assertThat(fileAsString, containsString("filename: ARCHIVEIT-"));
    assertThat(fileAsString, containsString("checksum: sha1:"));
    assertThat(fileAsString, containsString("filetype: warc"));
    assertThat(fileAsString, containsString("locations:"));
    assertThat(fileAsString, containsString("location 0: https://partner.archive-it.org/"));
    assertThat(fileAsString, containsString("size: 231145356"));
    assertThat(fileAsString, containsString("account: 925"));
    assertThat(fileAsString, containsString("collection: 5425"));
    assertThat(fileAsString, containsString("crawl: 299019"));
    assertThat(fileAsString, containsString("crawl_start: 2017-05-04T22"));
  }
}
