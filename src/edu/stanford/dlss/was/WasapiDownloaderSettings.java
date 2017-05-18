package edu.stanford.dlss.was;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class WasapiDownloaderSettings {
  // to add a new setting:
  // * add a String constant for the setting/arg name
  // * add a corresponding Option entry in optList
  // * add an accessor method (preferably with a name corresponding to the setting name)
  // * add to the tests to make sure it shows up in the usage info and settings dump
  public static final String BASE_URL_PARAM_NAME = "baseurl";
  public static final String USERNAME_PARAM_NAME = "username";
  public static final String PASSWORD_PARAM_NAME = "password";
  public static final String HELP_PARAM_NAME = "help";
  public static final String COLLECTION_ID_PARAM_NAME = "collectionId";
  public static final String JOB_ID_PARAM_NAME = "jobId";
  public static final String CRAWL_START_AFTER_PARAM_NAME = "crawlStartAfter";
  public static final String CRAWL_START_BEFORE_PARAM_NAME = "crawlStartBefore";
  public static final String OUTPUT_BASE_DIR_PARAM_NAME = "outputBaseDir";

  private HelpFormatter helpFormatter;
  private static Options wdsOpts;
  private Properties settings;
  private String helpAndSettingsMessage;

  private static Option[] optList = {
    Option.builder("h").longOpt(HELP_PARAM_NAME).desc("print this message (which describes expected arguments and dumps current config)").build(),
    buildArgOption(BASE_URL_PARAM_NAME, "the base URL of the WASAPI server from which to pull WARC files"),
    buildArgOption(USERNAME_PARAM_NAME, "the username for WASAPI server login"),
    buildArgOption(PASSWORD_PARAM_NAME, "the password for WASAPI server login"),
    buildArgOption(COLLECTION_ID_PARAM_NAME, "a collection from which to download crawl files"),
    buildArgOption(JOB_ID_PARAM_NAME, "a job from which to download crawl files"),
    buildArgOption(CRAWL_START_AFTER_PARAM_NAME, "only download crawl files created after this date"),
    buildArgOption(CRAWL_START_BEFORE_PARAM_NAME, "only download crawl files created before this date"),
    buildArgOption(OUTPUT_BASE_DIR_PARAM_NAME, "destination directory for downloaded WARC files")
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

      //TODO: validate settings state.  see https://github.com/sul-dlss/wasapi-downloader/issues/42
    } catch (IOException e) {
      throw new SettingsLoadException("Error reading properties file: " + e.getMessage(), e);
    } catch (ParseException e) {
      throw new SettingsLoadException("Error parsing command line arguments: " + e.getMessage(), e);
    }
  }


  public boolean shouldDisplayHelp() {
    return settings.getProperty(HELP_PARAM_NAME) != null;
  }

  public String baseUrlString() {
    return settings.getProperty(BASE_URL_PARAM_NAME);
  }

  public String authUrlString() {
    return settings.getProperty("authurl");
  }

  public String username() {
    return settings.getProperty(USERNAME_PARAM_NAME);
  }

  public String password() {
    return settings.getProperty(PASSWORD_PARAM_NAME);
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

  public String outputBaseDir() {
    return settings.getProperty(OUTPUT_BASE_DIR_PARAM_NAME);
  }

  public String getHelpAndSettingsMessage() {
    if (helpAndSettingsMessage == null)
      helpAndSettingsMessage = new StringBuilder(getCliHelpMessageCharSeq()).append(getSettingsSummaryCharSeq()).toString();

    return helpAndSettingsMessage;
  }

  public String toString() {
    return getHelpAndSettingsMessage();
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
