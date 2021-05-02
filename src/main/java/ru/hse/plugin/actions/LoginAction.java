package ru.hse.plugin.actions;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.vcsUtil.AuthDialog;
import org.jetbrains.annotations.NotNull;

import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.managers.BackendConnection;
import ru.hse.plugin.managers.MainWindowManager;


public class LoginAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Credentials credentials = Credentials.getInstance();
        AuthDialog dialog = new AuthDialog(e.getProject(), "Login", "Login to Rekoder", credentials.getLogin(), null, true);
        if (!dialog.showAndGet()) {
            return;
        }
        String token = BackendConnection.login(dialog.getUsername(), dialog.getPassword());
        if (token == null) { // TODO: заново выводить окно
//            Notification notification = new Notification(MyNotifier.NOTIFICATION_GROUP.getDisplayId(), "Login", "Login failed", NotificationType.ERROR);
            Notification notification = NotificationGroup.toolWindowGroup("rekoder", "rekoderWindow").createNotification("Login failed", NotificationType.ERROR);
            Notifications.Bus.notify(notification, e.getProject());
            return;
        }
        credentials.setLogin(dialog.getUsername());
        credentials.setToken(dialog.getPassword());

        MainWindowManager.updateProblemsTree(e.getProject());
        MainWindowManager.updateTeamsList(e.getProject());
        MainWindowManager.clearProblemPanel(e.getProject());
    }
}
