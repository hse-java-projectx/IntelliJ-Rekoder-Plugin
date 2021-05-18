package ru.hse.plugin.actions;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.managers.ExplorerManager;
import ru.hse.plugin.managers.ProblemManager;
import ru.hse.plugin.utils.NotificationUtils;

public class LogoutAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ProgressManager progressManager = new ProgressManagerImpl();
        progressManager.run(new Task.Backgroundable(e.getProject(), "Logout", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Credentials credentials = Credentials.getInstance();
                if (isNotLoggedIn(credentials)) {
                    NotificationUtils.showToolWindowMessage("You are not logged in", NotificationType.WARNING, e.getProject());
                    return;
                }
                ExplorerManager explorerManager = new ExplorerManager(e.getProject());
                ProblemManager problemManager = new ProblemManager(e.getProject());
                explorerManager.clearEverything();
                problemManager.clearEverything();
                credentials.setToken(null);
                NotificationUtils.showToolWindowMessage("Logged out successfully", NotificationType.INFORMATION, e.getProject());
            }
        });
    }

    private boolean isNotLoggedIn(Credentials credentials) {
        return credentials.getToken() == null;
    }
}
