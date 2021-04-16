package ru.hse.plugin.ui.window;

import com.intellij.util.ui.HtmlPanel;
import org.jetbrains.annotations.NotNull;

public class SimpleHtmlPanel extends HtmlPanel {
    @Override
    protected @NotNull String getBody() {
        return getText();
    }
}
