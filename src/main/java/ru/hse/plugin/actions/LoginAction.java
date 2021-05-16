package ru.hse.plugin.actions;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.vcsUtil.AuthDialog;
import org.jetbrains.annotations.NotNull;

import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.managers.BackendManager;
import ru.hse.plugin.managers.ExplorerManager;
import ru.hse.plugin.managers.ProblemManager;
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
            String token = new BackendManager(Credentials.getInstance()).login(login, password);
            if (token == null) {
                NotificationUtils.showToolWindowMessage("Login failed", NotificationType.ERROR, e.getProject());
                continue;
            }
            credentials.setLogin(dialog.getUsername());
            credentials.setToken(dialog.getPassword());

            ExplorerManager.updateTeamsList(e.getProject());
            ExplorerManager.clearProblemPanel(e.getProject());
            ProblemManager.clearProblem(e.getProject());
            ProblemManager.clearTests(e.getProject());
            return;
        }
    }
}
