package ru.hse.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.managers.ExplorerManager;
import ru.hse.plugin.managers.ProblemManager;

public class RefreshAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ExplorerManager.clearProblemsTree(e.getProject());
        ExplorerManager.clearTeamsList(e.getProject());
        ExplorerManager.updateTeamsList(e.getProject());
        ExplorerManager.clearProblemPanel(e.getProject());
        ProblemManager.clearProblem(e.getProject());
        ProblemManager.clearTests(e.getProject());
        // TODO: можно вывести сообщение об успешности или не успешности обновления
    }
}
