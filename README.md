[![Build Status](https://travis-ci.org/sul-dlss/wasapi-downloader.svg?branch=master)](https://travis-ci.org/sul-dlss/wasapi-downloader)
[![Coverage Status](https://coveralls.io/repos/github/sul-dlss/wasapi-downloader/badge.svg?branch=master)](https://coveralls.io/github/sul-dlss/wasapi-downloader?branch=master)
[![GitHub version](https://badge.fury.io/gh/sul-dlss%2Fwasapi-downloader.svg)](https://badge.fury.io/gh/sul-dlss%2Fwasapi-downloader)

# wasapi-downloader
Java application to download crawls from WASAPI

## Usage

Gradle is the build tool.

You can create a build from the current source code by running this command from the project root:
```
./gradlew build installDist
```

An example invocation of the downloader after building:
```
./build/install/wasapi-downloader/bin/wasapi-downloader --collectionId 123 --crawlStartAfter 2014-03-14
```

You can print usage info and current config state with the `-h` or `--help` argument:
```
./build/install/wasapi-downloader/bin/wasapi-downloader --help
```

Since `--help` prints the combined configuration state, including values of the command line args, it can be useful in conjunction with other args if you're unsure whether you're providing the correct settings/arguments.

### Getting started

1. `git clone https://github.com/sul-dlss/wasapi-downloader.git`
2. `cd wasapi-downloader`
3. `./gradlew jar`  (compile the code)

### One Time Setup

Dependencies:

- java (7)

## Development

Gradle, with wrapper, is the build tool.  To compile and run the tests:

    `./gradlew check`

## Deployment

Capistrano is used for deployment.

1. On your laptop, run

    `bundle`

  to install the Ruby capistrano gems and other dependencies for deployment.

2. Deploy code to remote VM:

    `cap dev deploy`

  This will also build and package the code on the remote VM with Gradle wrapper.
