package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for loading configuration properties securely.
 * This class loads sensitive configuration data from a properties file
 * that should not be committed to version control.
 */
public class ConfigLoader {
    private static final Logger LOGGER = Logger.getLogger(ConfigLoader.class.getName());
    private static final Properties properties = new Properties();
    private static boolean isLoaded = false;

    /**
     * Load properties from the configuration file.
     */
    private static synchronized void loadProperties() {
        if (isLoaded) {
            return;
        }

        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                LOGGER.log(Level.SEVERE, "Unable to find config.properties file");
                // Attempt to load from sample file for development
                try (InputStream sampleInput = ConfigLoader.class.getClassLoader().getResourceAsStream("config.sample.properties")) {
                    if (sampleInput != null) {
                        properties.load(sampleInput);
                        LOGGER.log(Level.WARNING, "Loaded from sample configuration file. Please create a proper config.properties file.");
                    } else {
                        LOGGER.log(Level.SEVERE, "Unable to find config.sample.properties file");
                    }
                }
            } else {
                properties.load(input);
                LOGGER.log(Level.INFO, "Configuration loaded successfully");
            }
            isLoaded = true;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error loading configuration", ex);
        }
    }

    /**
     * Get a property value.
     * 
     * @param key The property key
     * @return The property value or null if not found
     */
    public static String getProperty(String key) {
        if (!isLoaded) {
            loadProperties();
        }
        return properties.getProperty(key);
    }

    /**
     * Get a property value with a default value if not found.
     * 
     * @param key The property key
     * @param defaultValue The default value to return if the key is not found
     * @return The property value or the default value if not found
     */
    public static String getProperty(String key, String defaultValue) {
        if (!isLoaded) {
            loadProperties();
        }
        return properties.getProperty(key, defaultValue);
    }
} 