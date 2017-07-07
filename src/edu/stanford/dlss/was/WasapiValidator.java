package edu.stanford.dlss.was;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class WasapiValidator {

  public static boolean validateResponse(StatusLine statusLine, boolean entityIsNull)
      throws ClientProtocolException, HttpResponseException {
    if(statusLine.getStatusCode() != HttpStatus.SC_OK) {
      throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
    }
    if (entityIsNull) {
      throw new ClientProtocolException("Response contains no content");
    }
    return true;
  }

  public static boolean validateMd5(String expectedChecksum, String filePath)
      throws NoSuchAlgorithmException, IOException {
    return validateChecksum("MD5", expectedChecksum, filePath);
  }

  public static boolean validateSha1(String expectedChecksum, String filePath)
      throws NoSuchAlgorithmException, IOException {
    return validateChecksum("SHA-1", expectedChecksum, filePath);
  }

  /**
   * convert byte array to a hexadecimal string. Note that this generates hexadecimal in lower case.
   */
  private static String bytesToHex(byte[] byteArray) {
    return DatatypeConverter.printHexBinary(byteArray).toLowerCase();
  }

  /**
   * @param algorithm - checksum algorithm to use, per
        https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest
   */
  private static boolean validateChecksum(String algorithm, String expectedChecksum, String filePath)
      throws NoSuchAlgorithmException, IOException {
    Path path = Paths.get(filePath);
    MessageDigest digest = MessageDigest.getInstance(algorithm);
    // The file can be several gigabytes, don't load the whole thing in memory
    byte[] buffer = new byte[8192];
    int n = 0;
    boolean success = false;
    InputStream inputStream = Files.newInputStream(path);
    try {
      while (n != -1) {
        n = inputStream.read(buffer);
        if (n > 0) {
          digest.update(buffer, 0, n);
        }
      }
      success = true;
    }
    finally {
      // Don't mask an exception with another caused by close()
      if (success) {
        inputStream.close();
      }
    }
    byte[] computedChecksumBytes = digest.digest();
    String computedChecksumString = bytesToHex(computedChecksumBytes);
    return expectedChecksum.toLowerCase().compareTo(computedChecksumString) == 0;
  }

}
