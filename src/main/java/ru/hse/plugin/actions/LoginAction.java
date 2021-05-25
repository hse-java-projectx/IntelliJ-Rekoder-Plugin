package ru.hse.plugin.actions;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.vcsUtil.AuthDialog;
import org.jetbrains.annotations.NotNull;

import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.managers.BackendManager;
import ru.hse.plugin.utils.NotificationUtils;


public class LoginAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        Credentials credentials = Credentials.getInstance();
        String login = credentials.getLogin();
        String password = null;
        boolean rememberByDefault = true;
        while (true) {
            AuthDialog dialog = new AuthDialog(project, "Login", "Login to Rekoder", login, password, rememberByDefault);
            if (!dialog.showAndGet()) {
                return;
            }
            login = dialog.getUsername();
            password = dialog.getPassword();
            rememberByDefault = dialog.isRememberPassword();
            ProgressManager progressManager = new ProgressManagerImpl();
            String token = null;
            try {
                String finalLogin = login;
                String finalPassword = password;
                token =  progressManager.run(new Task.WithResult<String, Exception>(project, "Login", true) {
                    @Override
                    protected String compute(@NotNull ProgressIndicator indicator) {
                        return new BackendManager(Credentials.getInstance()).login(finalLogin, finalPassword);
                    }
                });
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            if (token == null) {
                NotificationUtils.showToolWindowMessage("Login failed", NotificationType.ERROR, project);
                continue;
            }
            credentials.setLogin(dialog.getUsername());
            credentials.setToken(token);
            credentials.setRemember(rememberByDefault);

            RefreshAction refreshAction = new RefreshAction();
            refreshAction.actionPerformed(e);

            return;
        }
    }
}
