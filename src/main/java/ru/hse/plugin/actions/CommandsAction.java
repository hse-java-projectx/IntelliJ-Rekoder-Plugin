package ru.hse.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.ui.settings.SettingsConfigurable;

public class CommandsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), SettingsConfigurable.DISPLAY_NAME);
    }

}
