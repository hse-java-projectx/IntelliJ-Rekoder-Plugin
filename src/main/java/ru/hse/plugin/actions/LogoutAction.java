package ru.hse.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.managers.BackendConnection;
import ru.hse.plugin.managers.MainWindowManager;

public class LogoutAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        MainWindowManager.clearProblemsTree(e.getProject());
        MainWindowManager.clearTeamsList(e.getProject());
        MainWindowManager.clearProblemPanel(e.getProject());
        // TODO: можно вывести сообщение об успешности или не успешности выхода
    }
}
