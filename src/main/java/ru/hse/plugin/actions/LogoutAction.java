package ru.hse.plugin.actions;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.managers.ExplorerManager;
import ru.hse.plugin.managers.ProblemManager;
import ru.hse.plugin.utils.NotificationUtils;

public class LogoutAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        Credentials credentials = Credentials.getInstance();
        if (isNotLoggedIn(credentials)) {
            NotificationUtils.showToolWindowMessage("You are not logged in", NotificationType.WARNING, project);
            return;
        }

        ProgressManager progressManager = new ProgressManagerImpl();
        progressManager.run(new Task.Backgroundable(project, "Logout", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ExplorerManager explorerManager = new ExplorerManager(project);
                ProblemManager problemManager = new ProblemManager(project);
                explorerManager.clearEverything();
                problemManager.clearEverything();
                credentials.setToken(null);
                NotificationUtils.showToolWindowMessage("Successfully logged out", NotificationType.INFORMATION, project);
            }
        });
    }

    private boolean isNotLoggedIn(Credentials credentials) {
        return credentials.getToken() == null;
    }
}
