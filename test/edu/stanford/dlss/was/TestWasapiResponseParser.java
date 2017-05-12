package edu.stanford.dlss.was;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.*;

public class TestWasapiResponseParser {

  private static final char SEP = File.separatorChar;

  @Test
  public void parsesSingleFileFixture() throws IOException {
    FileInputStream fis = new FileInputStream("test" + SEP + "fixtures" + SEP + "webdata_filename_response.json");
    WasapiResponseParser responseParser = new WasapiResponseParser();
    WasapiResponse response = responseParser.parse(fis);
    assertNotNull(response.getCount());
    assertNotNull(response.getFiles());
    assertFalse(response.isIncludesExtra());
    WasapiFile file = response.getFiles()[0];
    assertNotNull(file.getAccountId());
    assertNotNull(file.getChecksumsStr());
    assertNotNull(file.getCollectionId());
    assertNotNull(file.getCrawlId());
    assertNotNull(file.getCrawlStartDateStr());
    assertNotNull(file.getFilename());
    assertNotNull(file.getFiletype());
    assertNotNull(file.getLocations());
    assertNotNull(file.getLocations()[0]);
    assertNotNull(file.getSize());
  }

  @Test
  public void parsesMultiFileFixture() throws IOException {
    FileInputStream fis = new FileInputStream("test" + SEP + "fixtures" + SEP + "webdata_crawl_mult_files_response.json");
    WasapiResponseParser responseParser = new WasapiResponseParser();
    WasapiResponse response = responseParser.parse(fis);
    assertNotNull(response.getCount());
    assertNotNull(response.getFiles());
    assertFalse(response.isIncludesExtra());
    WasapiFile file = response.getFiles()[0];
    assertNotNull(file.getAccountId());
    assertNotNull(file.getChecksumsStr());
    assertNotNull(file.getCollectionId());
    assertNotNull(file.getCrawlId());
    assertNotNull(file.getCrawlStartDateStr());
    assertNotNull(file.getFilename());
    assertNotNull(file.getFiletype());
    assertNotNull(file.getLocations());
    assertNotNull(file.getLocations()[0]);
    assertNotNull(file.getSize());
  }

  @Test
  public void parsesFirstPageOfMultPageFixture() throws IOException {
    FileInputStream fis = new FileInputStream("test" + SEP + "fixtures" + SEP + "webdata_response.json");
    WasapiResponseParser responseParser = new WasapiResponseParser();
    WasapiResponse response = responseParser.parse(fis);
    assertNotNull(response.getCount());
    assertNotNull(response.getFiles());
    assertFalse(response.isIncludesExtra());
    assertNotNull(response.getNext());
    WasapiFile file = response.getFiles()[0];
    assertNotNull(file.getAccountId());
    assertNotNull(file.getChecksumsStr());
    assertNotNull(file.getCollectionId());
    assertNotNull(file.getCrawlId());
    assertNotNull(file.getCrawlStartDateStr());
    assertNotNull(file.getFilename());
    assertNotNull(file.getFiletype());
    assertNotNull(file.getLocations());
    assertNotNull(file.getLocations()[0]);
    assertNotNull(file.getSize());
  }
}
