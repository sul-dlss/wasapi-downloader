[![Build Status](https://travis-ci.org/sul-dlss/wasapi-downloader.svg?branch=master)](https://travis-ci.org/sul-dlss/wasapi-downloader)
[![Coverage Status](https://coveralls.io/repos/github/sul-dlss/wasapi-downloader/badge.svg?branch=master)](https://coveralls.io/github/sul-dlss/wasapi-downloader?branch=master)
[![GitHub version](https://badge.fury.io/gh/sul-dlss%2Fwasapi-downloader.svg)](https://badge.fury.io/gh/sul-dlss%2Fwasapi-downloader)

# wasapi-downloader
Java application to download crawls from WASAPI

## Usage

Gradle is the build tool.

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
