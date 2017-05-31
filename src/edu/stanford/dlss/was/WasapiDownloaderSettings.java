package edu.stanford.dlss.was;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.LinkedList;
import java.util.Properties;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.commons.validator.routines.UrlValidator;

@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class WasapiDownloaderSettings {
  // to add a new setting:
  // * add a String constant for the setting/arg name
  // * add a corresponding Option entry in optList
  // * add an accessor method (preferably with a name corresponding to the setting name)
  // * add the appropriate validation in getSettingsErrorMessages()
  // * add tests to make sure it: shows up in usage info and settings dump; is checked for validity; has a value in example settings, if applicable
  public static final String BASE_URL_PARAM_NAME = "baseurl";
  public static final String AUTH_URL_PARAM_NAME = "authurl";
  public static final String USERNAME_PARAM_NAME = "username";
  public static final String PASSWORD_PARAM_NAME = "password";
  public static final String ACCCOUNT_ID_PARAM_NAME = "accountId";
  public static final String HELP_PARAM_NAME = "help";
  public static final String COLLECTION_ID_PARAM_NAME = "collectionId";
  public static final String JOB_ID_PARAM_NAME = "jobId";
  public static final String CRAWL_START_AFTER_PARAM_NAME = "crawlStartAfter";
  public static final String CRAWL_START_BEFORE_PARAM_NAME = "crawlStartBefore";
  public static final String JOB_ID_LOWER_BOUND_PARAM_NAME = "jobIdLowerBound";
  public static final String OUTPUT_BASE_DIR_PARAM_NAME = "outputBaseDir";
  public static final String FILENAME_PARAM_NAME = "filename";

  protected Properties settings;

  private HelpFormatter helpFormatter;
  private static Options wdsOpts;
  private String helpAndSettingsMessage;

  private static Option[] optList = {
    Option.builder("h").longOpt(HELP_PARAM_NAME).desc("print this message (which describes expected arguments and dumps current config)").build(),
    buildArgOption(BASE_URL_PARAM_NAME, "the base URL of the WASAPI server from which to pull WARC files"),
    buildArgOption(AUTH_URL_PARAM_NAME, "the WASAPI server URL at which login credentials are passed"),
    buildArgOption(USERNAME_PARAM_NAME, "the username for WASAPI server login"),
    buildArgOption(PASSWORD_PARAM_NAME, "the password for WASAPI server login"),
    buildArgOption(ACCCOUNT_ID_PARAM_NAME, "the ID for the account from which WARC files are downloaded"),
    buildArgOption(COLLECTION_ID_PARAM_NAME, "a collection from which to download crawl files"),
    buildArgOption(JOB_ID_PARAM_NAME, "a job from which to download crawl files"),
    buildArgOption(CRAWL_START_AFTER_PARAM_NAME, "only download crawl files created after this date"),
    buildArgOption(CRAWL_START_BEFORE_PARAM_NAME, "only download crawl files created before this date"),
    buildArgOption(JOB_ID_LOWER_BOUND_PARAM_NAME, "\"last crawl downloaded\": only download crawl files with a higher job ID (not inclusive)"),
    buildArgOption(OUTPUT_BASE_DIR_PARAM_NAME, "destination directory for downloaded WARC files"),
    buildArgOption(FILENAME_PARAM_NAME, "single filename to download")
  };

  static {
    wdsOpts = new Options();
    for (Option opt : optList) {
      wdsOpts.addOption(opt);
    }
  }

  public WasapiDownloaderSettings(String settingsFileLocation, String[] args) throws SettingsLoadException {
    try {
      loadPropertiesFile(settingsFileLocation);
      parseArgsIntoSettings(args);
      validateSettings();
    } catch (IOException e) {
      throw new SettingsLoadException("Error reading properties file: " + e.getMessage(), e);
    } catch (ParseException e) {
      throw new SettingsLoadException("Error parsing command line arguments: " + e.getMessage(), e);
    }
  }

  protected WasapiDownloaderSettings() { } //for testing


  public boolean shouldDisplayHelp() {
    return settings.getProperty(HELP_PARAM_NAME) != null;
  }

  public String baseUrlString() {
    return settings.getProperty(BASE_URL_PARAM_NAME);
  }

  public String authUrlString() {
    return settings.getProperty(AUTH_URL_PARAM_NAME);
  }

  public String username() {
    return settings.getProperty(USERNAME_PARAM_NAME);
  }

  public String password() {
    return settings.getProperty(PASSWORD_PARAM_NAME);
  }

  public String accountId() {
    return settings.getProperty(ACCCOUNT_ID_PARAM_NAME);
  }

  public String collectionId() {
    return settings.getProperty(COLLECTION_ID_PARAM_NAME);
  }

  public String jobId() {
    return settings.getProperty(JOB_ID_PARAM_NAME);
  }

  // e.g. 2014-01-01, see https://github.com/WASAPI-Community/data-transfer-apis/tree/master/ait-reference-specification#paths--examples
  public String crawlStartAfter() {
    return settings.getProperty(CRAWL_START_AFTER_PARAM_NAME);
  }

  // e.g. 2014-01-01, see https://github.com/WASAPI-Community/data-transfer-apis/tree/master/ait-reference-specification#paths--examples
  public String crawlStartBefore() {
    return settings.getProperty(CRAWL_START_BEFORE_PARAM_NAME);
  }

  public String jobIdLowerBound() {
    return settings.getProperty(JOB_ID_LOWER_BOUND_PARAM_NAME);
  }

  public String outputBaseDir() {
    return settings.getProperty(OUTPUT_BASE_DIR_PARAM_NAME);
  }

  public String filename() {
    return settings.getProperty(FILENAME_PARAM_NAME);
  }

  public String getHelpAndSettingsMessage() {
    if (helpAndSettingsMessage == null)
      helpAndSettingsMessage = new StringBuilder(getCliHelpMessageCharSeq()).append(getSettingsSummaryCharSeq()).toString();

    return helpAndSettingsMessage;
  }

  public String toString() {
    return getHelpAndSettingsMessage();
  }


  protected void validateSettings() throws SettingsLoadException {
    List<String> errMessages = getSettingsErrorMessages();
    if (errMessages.size() > 0) {
      StringBuilder buf = new StringBuilder("Invalid settings state:\n");
      for (String errMsg : errMessages) {
        buf.append("  ");
        buf.append(errMsg);
        buf.append(".\n");
      }
      throw new SettingsLoadException(buf.toString());
    }
  }

  @SuppressWarnings({"checkstyle:CyclomaticComplexity", "checkstyle:NPathComplexity", "checkstyle:MethodLength", "checkstyle:MultipleStringLiterals"})
  protected List<String> getSettingsErrorMessages() {
    String[] schemes = {"https"};
    UrlValidator urlValidator = new UrlValidator(schemes);
    IntegerValidator intValidator = new IntegerValidator();
    List<String> errMessages = new LinkedList<String>();

    // required
    if (isNullOrEmpty(baseUrlString()) || !urlValidator.isValid(baseUrlString()))
      errMessages.add(BASE_URL_PARAM_NAME + " is required, and must be a valid URL");
    if (isNullOrEmpty(authUrlString()) || !urlValidator.isValid(authUrlString()))
      errMessages.add(AUTH_URL_PARAM_NAME + " is required, and must be a valid URL");
    if (isNullOrEmpty(username()))
      errMessages.add(USERNAME_PARAM_NAME + " is required");
    if (isNullOrEmpty(password()))
      errMessages.add(PASSWORD_PARAM_NAME + " is required");
    if (isNullOrEmpty(outputBaseDir()) || !isDirWritable(outputBaseDir()))
      errMessages.add(OUTPUT_BASE_DIR_PARAM_NAME + " is required (and must be an extant, writable directory)");

    // optional, validate if specified
    if (!isNullOrEmpty(accountId()) && !intValidator.isValid(accountId()))
      errMessages.add(ACCCOUNT_ID_PARAM_NAME + " must be an integer (if specified)");
    if (!isNullOrEmpty(collectionId()) && !intValidator.isValid(collectionId()))
      errMessages.add(COLLECTION_ID_PARAM_NAME + " must be an integer (if specified)");
    if (!isNullOrEmpty(jobId()) && !intValidator.isValid(jobId()))
      errMessages.add(JOB_ID_PARAM_NAME + " must be an integer (if specified)");
    if (!isNullOrEmpty(crawlStartBefore()) && !isValidIso8601String(crawlStartBefore()))
      errMessages.add(CRAWL_START_BEFORE_PARAM_NAME + " must be a valid ISO 8601 date string (if specified)");
    if (!isNullOrEmpty(crawlStartAfter()) && !isValidIso8601String(crawlStartAfter()))
      errMessages.add(CRAWL_START_AFTER_PARAM_NAME + " must be a valid ISO 8601 date string (if specified)");
    if (!isNullOrEmpty(jobIdLowerBound()) && !intValidator.isValid(jobIdLowerBound()))
      errMessages.add(JOB_ID_LOWER_BOUND_PARAM_NAME + " must be an integer (if specified)");

    return errMessages;
  }

  protected static boolean isNullOrEmpty(String str) {
    return str == null || str.isEmpty();
  }

  protected static boolean isDirWritable(String dirPath) {
    File outputBaseDirFile = new File(dirPath);
    return outputBaseDirFile.exists() && outputBaseDirFile.isDirectory() && outputBaseDirFile.canWrite();
  }

  // neither the java.util. Date and Calendar classes, nor the apache commons
  // validator classes provide an easy way to parse or validate ISO 8601 date strings.
  // but javax.xml.bind.DatatypeConverter does it, since xsd:dateTime is ISO 8601.
  // https://www.w3.org/TR/xmlschema11-2/#dateTime
  // https://docs.oracle.com/javase/7/docs/api/javax/xml/bind/DatatypeConverter.html
  private static boolean isValidIso8601String(String dateStr) {
    try {
      DatatypeConverter.parseDateTime(dateStr);
    } catch(IllegalArgumentException e) {
      return false;
    }
    return true;
  }

  private CharSequence getCliHelpMessageCharSeq() {
    helpFormatter = new HelpFormatter();
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    int width = HelpFormatter.DEFAULT_WIDTH;
    int leftPad = HelpFormatter.DEFAULT_LEFT_PAD;
    int descPad = HelpFormatter.DEFAULT_DESC_PAD;
    String helpMsgHeader = "=====\nallowed arguments\n(may also also be specified in config/settings.properties, sans leading hyphens)\n---";
    helpFormatter.printHelp(pw, width, "bin/wasapi-downloader", helpMsgHeader, wdsOpts, leftPad, descPad, "=====", true);
    pw.flush();
    sw.flush();
    return sw.getBuffer();
  }

  private CharSequence getSettingsSummaryCharSeq() {
    StringBuilder buf = new StringBuilder();
    buf.append("=====\n");
    buf.append("current settings (arg values override settings.properties values)\n");
    buf.append("---\n");
    for (String settingName : settings.stringPropertyNames()) {
      if (!settingName.equals(PASSWORD_PARAM_NAME)) {
        buf.append(settingName);
        buf.append(" : ");
        buf.append(settings.getProperty(settingName));
      } else {
        buf.append(settingName);
        buf.append(" : [password hidden]");
      }
      buf.append("\n");
    }
    buf.append("\n=====\n");

    return buf;
  }

  private static Option buildArgOption(String optionName, String description) {
    return Option.builder().hasArg().longOpt(optionName).desc(description).build();
  }

  private void addParsedArgsToSettings(CommandLine parsedArgs) {
    for (Option opt : parsedArgs.getOptions()) {
      String optName = opt.getLongOpt();
      String optValue = parsedArgs.getOptionValue(optName);
      if (optValue != null)
        settings.setProperty(optName, optValue);
    }

    if (parsedArgs.hasOption(HELP_PARAM_NAME))
      settings.setProperty(HELP_PARAM_NAME, "true");
  }

  private void parseArgsIntoSettings(String[] args) throws ParseException {
    CommandLineParser cliParser = new DefaultParser();
    CommandLine parsedArgs = cliParser.parse(wdsOpts, args);
    addParsedArgsToSettings(parsedArgs);
  }

  private void loadPropertiesFile(String settingsFileLocation) throws IOException {
    if (settings == null) {
      InputStream input = null;
      try {
        input = new FileInputStream(settingsFileLocation);
        settings = new Properties();
        settings.load(input);
      } finally {
        if (input != null)
          input.close();
      }
    } else {
      // should never get here, since this method is private, and only gets called once from the constructor
      System.err.println("Properties already loaded from " + settingsFileLocation);
    }
  }
}
