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

public class WasapiDownloaderSettings {
  public static final String BASE_URL_PARAM_NAME = "baseurl";
  public static final String USERNAME_PARAM_NAME = "username";
  public static final String PASSWORD_PARAM_NAME = "password";
  public static final String HELP_PARAM_NAME = "help";
  public static final String COLLECTION_ID_PARAM_NAME = "collectionId";
  public static final String JOB_ID_PARAM_NAME = "jobId";
  public static final String CRAWL_START_AFTER_PARAM_NAME = "crawlStartAfter";
  public static final String CRAWL_START_BEFORE_PARAM_NAME = "crawlStartBefore";

  private HelpFormatter helpFormatter;
  private Options wdsOpts;
  private Properties settings;
  private String helpAndSettingsMessage;

  public WasapiDownloaderSettings(String settingsFileLocation, String[] args) throws SettingsLoadException {
    try {
      loadPropertiesFile(settingsFileLocation);
      setupArgOptions();
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
    helpFormatter.printHelp(pw, width, "bin/wasapi-downloader", "=====\npossible arguments\n---", wdsOpts, leftPad, descPad, "=====", true);
    pw.flush();
    sw.flush();
    return sw.getBuffer();
  }

  private CharSequence getSettingsSummaryCharSeq() {
    StringBuilder buf = new StringBuilder();
    buf.append("=====\n");
    buf.append("current settings\n");
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

  @SuppressWarnings("checkstyle:linelength")
  private void setupArgOptions() {
    Option helpOpt = Option.builder("h").longOpt(HELP_PARAM_NAME).desc("print this message (which describes expected arguments and dumps current config)").build();
    Option collectionIdOpt = buildArgOption(COLLECTION_ID_PARAM_NAME, "a collection from which to download crawl files");
    Option jobIdOpt = buildArgOption(JOB_ID_PARAM_NAME, "a job from which to download crawl files");
    Option crawlStartAfterOpt = buildArgOption(CRAWL_START_AFTER_PARAM_NAME, "only download crawl files created after this date");
    Option crawlStartBeforeOpt = buildArgOption(CRAWL_START_BEFORE_PARAM_NAME, "only download crawl files created before this date");

    wdsOpts = new Options();
    wdsOpts.addOption(helpOpt);
    wdsOpts.addOption(collectionIdOpt);
    wdsOpts.addOption(jobIdOpt);
    wdsOpts.addOption(crawlStartAfterOpt);
    wdsOpts.addOption(crawlStartBeforeOpt);
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
