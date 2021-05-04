package ru.hse.plugin.actions;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.vcsUtil.AuthDialog;
import org.jetbrains.annotations.NotNull;

import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.managers.BackendManager;
import ru.hse.plugin.managers.MainWindowManager;
import ru.hse.plugin.managers.ProblemWindowManager;
import ru.hse.plugin.utils.NotificationUtils;


public class LoginAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Credentials credentials = Credentials.getInstance();
        AuthDialog dialog = new AuthDialog(e.getProject(), "Login", "Login to Rekoder", credentials.getLogin(), null, true);
        if (!dialog.showAndGet()) {
            return;
        }
        String token = BackendManager.login(dialog.getUsername(), dialog.getPassword());
        if (token == null) { // TODO: заново выводить окно
            NotificationUtils.showToolWindowMessage("Login failed", NotificationType.ERROR, e.getProject());
            return;
        }
        credentials.setLogin(dialog.getUsername());
        credentials.setToken(dialog.getPassword());

        MainWindowManager.updateProblemsTree(e.getProject());
        MainWindowManager.updateTeamsList(e.getProject());
        MainWindowManager.clearProblemPanel(e.getProject());
        ProblemWindowManager.clearSubmission(e.getProject());
        ProblemWindowManager.clearTests(e.getProject());
    }
}
