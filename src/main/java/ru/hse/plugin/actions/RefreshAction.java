package ru.hse.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.managers.MainWindowManager;
import ru.hse.plugin.managers.ProblemWindowManager;

public class RefreshAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        MainWindowManager.updateProblemsTree(e.getProject());
        MainWindowManager.updateTeamsList(e.getProject());
        MainWindowManager.clearProblemPanel(e.getProject());
        ProblemWindowManager.clearSubmission(e.getProject());
        ProblemWindowManager.clearTests(e.getProject());
        // TODO: можно вывести сообщение об успешности или не успешности обновления
    }
}
