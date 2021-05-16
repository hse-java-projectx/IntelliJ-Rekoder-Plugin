package ru.hse.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.managers.ExplorerManager;
import ru.hse.plugin.managers.ProblemManager;
import ru.hse.plugin.utils.ThreadUtils;

public class RefreshAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ProgressManager progressManager = new ProgressManagerImpl();
        progressManager.run(new Task.Backgroundable(e.getProject(), "Refresh", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ExplorerManager.clearProblemsTree(e.getProject());
                ExplorerManager.clearTeamsList(e.getProject());
                ExplorerManager.updateTeamsList(e.getProject());
                ExplorerManager.clearProblemPanel(e.getProject());
                ProblemManager.clearProblem(e.getProject());
                ProblemManager.clearTests(e.getProject());
            }
        });
    }
}
