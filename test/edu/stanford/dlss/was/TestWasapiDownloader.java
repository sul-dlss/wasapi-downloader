package edu.stanford.dlss.was;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;
import org.junit.*;

public class TestWasapiDownloader {

  @Test
  public void constructor_loadsSettings() throws SettingsLoadException {
    WasapiDownloader myInstance = new WasapiDownloader(WasapiDownloader.SETTINGS_FILE_LOCATION, null);
    assertNotNull(myInstance.settings);
  }

  @Test
  public void main_noHelp_canExecuteWithoutCrashing() throws SettingsLoadException {
    WasapiDownloader.main(null);
  }

  @Test
  public void main_withHelp_canExecuteWithoutCrashing() throws SettingsLoadException {
    String[] args = { "-h" };
    WasapiDownloader.main(args);
  }

  private static final char SEP = File.separatorChar;
  private static final String FIXTURE_WARC_PATH = "test" + SEP + "fixtures" + SEP + "small-file.warc.gz";
  private static final String FIXTURE_MD5 = "f08b0bf60733b61216e288cb7620bd4a";
  private static final String FIXTURE_SHA1 = "c7dff430d5d725c3d2b786d5b247eb5a8d53b228";

  @Test
  public void validateMd5Checksum_withValidChecksum() throws NoSuchAlgorithmException, IOException {
    assertTrue("md5 checksum expected to validate for small-file.warc.gz", WasapiDownloader.validateMd5(FIXTURE_MD5, FIXTURE_WARC_PATH));
  }

  @Test
  public void validateMd5Checksum_withInvalidChecksum() throws NoSuchAlgorithmException, IOException {
    assertFalse("md5 checksum NOT expected to validate for small-file.warc.gz", WasapiDownloader.validateMd5(FIXTURE_MD5 + "9", FIXTURE_WARC_PATH));
    assertFalse("md5 checksum NOT expected to validate for small-file.warc.gz", WasapiDownloader.validateMd5(FIXTURE_SHA1, FIXTURE_WARC_PATH));
  }

  @Test
  public void validateSha1Checksum_withValidChecksum() throws NoSuchAlgorithmException, IOException {
    assertTrue("sha1 checksum expected to validate for small-file.warc.gz", WasapiDownloader.validateSha1(FIXTURE_SHA1, FIXTURE_WARC_PATH));
  }

  @Test
  public void validateSha1Checksum_withInvalidChecksum() throws NoSuchAlgorithmException, IOException {
    assertFalse("sha1 checksum NOT expected to validate for small-file.warc.gz", WasapiDownloader.validateSha1(FIXTURE_SHA1 + "9", FIXTURE_WARC_PATH));
    assertFalse("sha1 checksum NOT expected to validate for small-file.warc.gz", WasapiDownloader.validateSha1(FIXTURE_MD5, FIXTURE_WARC_PATH));
  }
}
