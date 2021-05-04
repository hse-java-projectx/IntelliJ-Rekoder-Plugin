package ru.hse.plugin.actions;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.managers.MainWindowManager;
import ru.hse.plugin.managers.ProblemWindowManager;
import ru.hse.plugin.utils.NotificationUtils;

public class LogoutAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Credentials credentials = Credentials.getInstance();
        if (credentials.getToken() == null) {
            NotificationUtils.showToolWindowMessage("You are not logged in", NotificationType.WARNING, e.getProject());
            return;
        }
        MainWindowManager.clearProblemsTree(e.getProject());
        MainWindowManager.clearTeamsList(e.getProject());
        MainWindowManager.clearProblemPanel(e.getProject());
        ProblemWindowManager.clearSubmission(e.getProject());
        ProblemWindowManager.clearTests(e.getProject());
        credentials.setToken(null);
        NotificationUtils.showToolWindowMessage("Logged out successfully", NotificationType.INFORMATION, e.getProject());
    }
}
