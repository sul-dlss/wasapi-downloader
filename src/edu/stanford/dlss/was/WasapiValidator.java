package edu.stanford.dlss.was;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class WasapiValidator {

  private static final int STATUS_CODE_THRESHOLD = 300;

  public static boolean validateResponse(StatusLine statusLine, boolean entityIsNull) throws ClientProtocolException, HttpResponseException {
    if(statusLine.getStatusCode() >= STATUS_CODE_THRESHOLD) {
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
    byte[] computedChecksumBytes = digest.digest(Files.readAllBytes(path));
    String computedChecksumString = bytesToHex(computedChecksumBytes);
    return expectedChecksum.toLowerCase().compareTo(computedChecksumString) == 0;
  }

}
