package ru.hse.plugin.ui.window;

import com.intellij.ui.jcef.JBCefClient;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hse.plugin.utils.HtmlUtils;

public class RekoderHtmlPanel extends JCEFHtmlPanel {
    public RekoderHtmlPanel(@Nullable String url) {
        super(url);
    }

    public RekoderHtmlPanel(JBCefClient client, String url) {
        super(client, url);
    }

    @Override
    public void setHtml(@NotNull String text) {
        String html;
        if (!HtmlUtils.BASE_HTML.isEmpty()) {
            html = HtmlUtils.BASE_HTML.replace(HtmlUtils.STATEMENT_VAR, text);
        } else {
            html = text;
        }
        super.setHtml(html);
    }
}
