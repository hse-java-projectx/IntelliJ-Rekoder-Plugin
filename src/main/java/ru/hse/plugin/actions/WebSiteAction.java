package ru.hse.plugin.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.utils.PropertiesUtils;

import java.util.Arrays;

public class WebSiteAction extends AnAction {
    private static final String URL = new PropertiesUtils("/constants/constants.properties").getKey("siteUrl", "");

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Arrays.stream(FileTypeRegistry.getInstance().getRegisteredFileTypes()).forEach(f -> System.out.println(f.getName()));
//        BrowserUtil.browse(URL);
    }
}
