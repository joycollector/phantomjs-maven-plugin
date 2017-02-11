package com.github.klieber.phantomjs.os;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class OperatingSystemFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(OperatingSystemFactory.class);

  private static final String OS_NAME = "os.name";
  private static final String OS_ARCH = "os.arch";
  private static final String OS_VERSION = "os.version";
  private static final String DISTRIBUTION_NAME = "ID";
  private static final String DISTRIBUTION_VERSION_ID = "VERSION_ID";
  private static final String OS_RELEASE_PROPERTIES_FILE = "/etc/os-release";

  public OperatingSystem create() {
    String name = getSystemProperty(OS_NAME);
    String architecture = getArchitecture();
    String version = getSystemProperty(OS_VERSION);

    return isLinux(name) ? createLinuxOS(name, architecture, version) : createOS(name, architecture, version);
  }

  private String getArchitecture() {
    return getSystemProperty(OS_ARCH).contains("64") ? "x86_64" : "i686";
  }

  private boolean isLinux(String name) {
    return name.contains("nux");
  }

  private OperatingSystem createOS(String name,
                                   String architecture,
                                   String version) {
    return new OperatingSystem(
      name,
      architecture,
      version
    );
  }

  private OperatingSystem createLinuxOS(String name,
                                        String architecture,
                                        String version) {
    Properties linuxProperties = getLinuxProperties();

    String distribution = getProperty(linuxProperties, DISTRIBUTION_NAME);
    String distributionVersion = getProperty(linuxProperties, DISTRIBUTION_VERSION_ID);

    return new OperatingSystem(
      name,
      architecture,
      version,
      distribution,
      distributionVersion
    );
  }

  private String getSystemProperty(String name) {
    return System.getProperty(name).toLowerCase();
  }

  private String getProperty(Properties properties, String name) {
    return properties.getProperty(name).replaceAll("^\"(.*)\"$", "$1");
  }

  private Properties getLinuxProperties() {
    Properties properties = new Properties();
    try {
      File propertiesFile = new File(OS_RELEASE_PROPERTIES_FILE);
      if (propertiesFile.exists() && propertiesFile.canRead()) {
        FileInputStream in = new FileInputStream(propertiesFile);
        properties.load(in);
        in.close();
      }
    } catch (IOException e) {
      LOGGER.trace("unable to read linux os properties", e);
    }
    return properties;
  }
}
