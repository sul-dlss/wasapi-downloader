package edu.stanford.dlss.was;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.*;

import static org.hamcrest.CoreMatchers.*;

public class TestWasapiResponse {

  private static final char SEP = File.separatorChar;

  @Test
  public void toStringImplemented() throws IOException {
    FileInputStream fis = new FileInputStream("test" + SEP + "fixtures" + SEP + "webdata_filename_response.json");
    WasapiResponse myResponse =  new WasapiResponseParser().parse(fis);
    String responseAsString = myResponse.toString();
    assertThat(responseAsString, containsString("count: 1"));
    assertThat(responseAsString, containsString("previous: null"));
    assertThat(responseAsString, containsString("next: null"));
    assertThat(responseAsString, containsString("includes-extra: false"));
    assertThat(responseAsString, containsString("files:"));
    assertThat(responseAsString, containsString("file 0:"));
  }
}
