package edu.stanford.dlss.was;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WasapiResponseParser {

  public WasapiResponseParser() { }

  public WasapiResponse parse(InputStream jsonData) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    WasapiResponse responseObject = objectMapper.readValue(jsonData, WasapiResponse.class);
    return responseObject;
  }
}
