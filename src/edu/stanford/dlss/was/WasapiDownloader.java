package edu.stanford.dlss.was;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class WasapiDownloader {
  public static final String SETTINGS_FILE_LOCATION = "config/settings.properties";

  public WasapiDownloaderSettings settings = null;


  public WasapiDownloader(String settingsFileLocation, String[] args) throws SettingsLoadException {
    settings = new WasapiDownloaderSettings(settingsFileLocation, args);
  }

  public static boolean validateMd5(String expectedChecksum, String filePath)
      throws NoSuchAlgorithmException, IOException {
    return validateChecksum("MD5", expectedChecksum, filePath);
  }

  public static boolean validateSha1(String expectedChecksum, String filePath)
      throws NoSuchAlgorithmException, IOException {
    return validateChecksum("SHA-1", expectedChecksum, filePath);
  }

  public void executeFromCmdLine() {
    if (settings.shouldDisplayHelp()) {
      System.out.print(settings.getHelpAndSettingsMessage());
      return;
    }

    //TODO: useful work
  }

  public static void main(String[] args) throws SettingsLoadException {
    WasapiDownloader downloader = new WasapiDownloader(SETTINGS_FILE_LOCATION, args);
    downloader.executeFromCmdLine();
  }


  /**
   * convert byte array to a hexadecimal string. Note that this generates hexadecimal in lower case.
   */
   private static String bytesToHex(byte[] byteArray) {
     return DatatypeConverter.printHexBinary(byteArray).toLowerCase();
   }

   /**
    * @param algorithm - checksum algorithm to use, per https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest
    */
   public static boolean validateChecksum(String algorithm, String expectedChecksum, String filePath)
       throws NoSuchAlgorithmException, IOException {
     Path path = Paths.get(filePath);
     MessageDigest digest = MessageDigest.getInstance(algorithm);
     byte[] computedChecksumBytes = digest.digest(Files.readAllBytes(path));
     String computedChecksumString = bytesToHex(computedChecksumBytes);
     if (expectedChecksum.toLowerCase().compareTo(computedChecksumString) == 0)
       return true;
     else
       return false;
   }

}
