package ru.hse.plugin.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.utils.PropertiesUtils;

public class WebSiteAction extends AnAction {
    private static final String URL = new PropertiesUtils("/constants/constants.propertgies").getKey("siteUrl", "");

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        BrowserUtil.browse(URL);
    }
}
