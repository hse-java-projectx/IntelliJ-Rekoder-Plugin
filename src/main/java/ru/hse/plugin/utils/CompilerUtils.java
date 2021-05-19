package ru.hse.plugin.utils;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.fileTypes.FileTypes;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class CompilerUtils {
    private static final Map<String, String> filesExtentions = init();
    private static final String LANGUAGES_FILE = "/constants/languages.properties";
    private static final FileType DEFAULT_TYPE = FileTypes.PLAIN_TEXT;

    @NotNull
    public static String getFileExtension(String language) {
        return filesExtentions.getOrDefault(language, "txt");
    }

    public static Set<String> getAllLanguages() {
        return Collections.unmodifiableSet(filesExtentions.keySet());
    }

    @NotNull
    private static Map<String, String> init() {
        Map<String, String> types = new LinkedHashMap<>();
        Properties properties = new Properties();
        InputStream inputStream = CompilerUtils.class.getResourceAsStream(LANGUAGES_FILE);
        if (inputStream == null) {
            return types;
        }
        try {
            properties.load(inputStream);
            for (Object language : properties.keySet()) {
                if (!(language instanceof String)) {
                    System.out.println("Error");
                    return types;
                }
                String type = properties.getProperty((String) language);
                types.put((String) language, type);
            }
        } catch (IOException ignored) {
        }
        return types;
    }
}
