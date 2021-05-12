package ru.hse.plugin.utils;

public class StringUtils {
    public static String trimEndLines(String str) {

        int len = str.length();
        while (len > 0 && str.charAt(len - 1) == '\n') {
            len--;
        }
        return str.substring(0, len);
    }
}
