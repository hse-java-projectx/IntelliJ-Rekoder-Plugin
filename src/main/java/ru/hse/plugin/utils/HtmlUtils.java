package ru.hse.plugin.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HtmlUtils {
    public static final String BASE_HTML;
    public static final String NAME_OF_BASE_HTML = "/baseHtml.html";
    public static final String STATEMENT_VAR = "${Statement}";

    static {
        String html;
        try {
            InputStream is = HtmlUtils.class.getResourceAsStream(NAME_OF_BASE_HTML);
            if (is != null) {
                html = IOUtils.toString(is, StandardCharsets.UTF_8);
            } else {
                html = "";
            }
        } catch (IOException e) {
            html = "";
        }
        BASE_HTML = html;
    }
}