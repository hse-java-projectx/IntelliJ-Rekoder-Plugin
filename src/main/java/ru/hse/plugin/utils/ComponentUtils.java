package ru.hse.plugin.utils;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import com.intellij.util.ui.HtmlPanel;

import javax.swing.*;

public class ComponentUtils {
    public static void clearComponent(JLabel label) {
        label.setText("");
    }

    public static void clearComponent(HtmlPanel panel) {
        panel.setBody("");
    }

    public static void clearComponent(JTextArea area) {
        area.setText("");
    }

    public static void clearComponent(ComboBox<?> comboBox) {
        comboBox.setModel(new DefaultComboBoxModel<>());
    }

    public static void clearComponent(JCEFHtmlPanel panel) {
        panel.setHtml("");
    }
}
