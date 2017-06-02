package edu.stanford.dlss.was;

/**
 * Wrapper exception used when loading app settings from properties file and command line arguments.
 *
 * Shields callers knowing which underlying exceptions could be thrown.
 */
class SettingsLoadException extends Exception {
  SettingsLoadException(String message) {
    super(message);
  }
  SettingsLoadException(String message, Throwable cause) {
    super(message, cause);
  }
}
