package ru.hse.plugin.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {
    Properties properties = new Properties();

    public PropertiesUtils(String fileName) {
        try {
            InputStream inputStream = PropertiesUtils.class.getResourceAsStream(fileName);
            if (inputStream == null) {
                return;
            }
            properties.load(inputStream);
        } catch (IOException ignored) {
            // TODO: log
        }
    }

    public String getKey(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
