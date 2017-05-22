package edu.stanford.dlss.was;

/**
 * Wrapper exception used when loading app settings.
 *
 * Keeps callers from having to know all the underlying exceptions that might get thrown when reading properties and parsing CLI args.
 */
class SettingsLoadException extends Exception {
  SettingsLoadException(String message) {
    super(message);
  }
  SettingsLoadException(String message, Throwable cause) {
    super(message, cause);
  }
}
