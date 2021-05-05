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
        String login = credentials.getLogin();
        String password = null;
        boolean rememberByDefault = true;
        while (true) {
            AuthDialog dialog = new AuthDialog(e.getProject(), "Login", "Login to Rekoder", login, password, rememberByDefault);
            if (!dialog.showAndGet()) {
                return;
            }
            login = dialog.getUsername();
            password = dialog.getPassword();
            rememberByDefault = dialog.isRememberPassword();
            String token = BackendManager.login(login, password);
            if (token == null) {
                NotificationUtils.showToolWindowMessage("Login failed", NotificationType.ERROR, e.getProject());
                continue;
            }
            credentials.setLogin(dialog.getUsername());
            credentials.setToken(dialog.getPassword());

            MainWindowManager.updateProblemsTree(e.getProject());
            MainWindowManager.updateTeamsList(e.getProject());
            MainWindowManager.clearProblemPanel(e.getProject());
            ProblemWindowManager.clearSubmission(e.getProject());
            ProblemWindowManager.clearTests(e.getProject());
            return;
        }
    }
}
