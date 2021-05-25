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
import ru.hse.plugin.exceptions.HttpException;
import ru.hse.plugin.exceptions.UnauthorizedException;
import ru.hse.plugin.managers.ExplorerManager;
import ru.hse.plugin.managers.ProblemManager;
import ru.hse.plugin.utils.NotificationUtils;

public class RefreshAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        String token = Credentials.getInstance().getToken();
        if (token == null || token.isEmpty()) {
            NotificationUtils.showAuthorisationFailedNotification(project);
            return;
        }

        ProgressManager progressManager = new ProgressManagerImpl();
        progressManager.run(new Task.Backgroundable(project, "Refreshing", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ExplorerManager explorerManager = new ExplorerManager(project);
                ProblemManager problemManager = new ProblemManager(project);
                try {
                    explorerManager.clearEverything();
                    problemManager.clearEverything();
                    explorerManager.updateTeamsList();
                } catch (UnauthorizedException ex) {
                    explorerManager.clearEverything();
                    problemManager.clearEverything();
                    NotificationUtils.showAuthorisationFailedNotification(project);
                } catch (HttpException ex) {
                    explorerManager.clearEverything();
                    problemManager.clearEverything();
                    NotificationUtils.showNetworkProblemNotification(project);
                    NotificationUtils.log(project, ex.getMessage(), NotificationType.ERROR);
                }
            }
        });
    }
}
