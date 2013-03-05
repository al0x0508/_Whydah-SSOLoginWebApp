package net.whydah.sso.service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Helper methods for reading configuration.
 */
public class AppConfig {
    public final static String IAM_CONFIG_KEY = "IAM_CONFIG";
    private final static Logger logger = LoggerFactory.getLogger(AppConfig.class);

    public static Properties readProperties() throws IOException {
        String appMode = ApplicationMode.getApplicationMode();
        Properties properties = loadFromClasspath(appMode);

        String configfilename = System.getProperty(IAM_CONFIG_KEY);
        if(configfilename != null) {
            loadFromFile(properties, configfilename);
        }
        return properties;
    }

    private static Properties loadFromClasspath(String appMode) throws IOException {
        Properties properties = new Properties();
        String propertyfile = String.format("ssologinservice.%s.properties", appMode);
        logger.info("Loading properties from classpath: {}", propertyfile);
        InputStream is = AppConfig.class.getClassLoader().getResourceAsStream(propertyfile);
        if(is == null) {
            logger.error("Error reading {} from classpath.", propertyfile);
            System.exit(3);
        }
        properties.load(is);
        return properties;
    }

    private static void loadFromFile(Properties properties, String configfilename) throws IOException {
        File file = new File(configfilename);
        logger.info("Overriding defaults from property file {}", file.getAbsolutePath());
        if(file.exists()) {
            properties.load(new FileInputStream(file));
        } else {
            logger.error("Config file {} specified by System property {} not found.", configfilename, IAM_CONFIG_KEY);
            System.exit(3);
        }
    }
}
