package ru.hse.plugin.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import ru.hse.plugin.ui.window.RekoderToolWindowFactory;

public class NotificationUtils {
    public static void showToolWindowMessage(String message, NotificationType type, Project project) {
        Notification notification = NotificationGroup.
                toolWindowGroup("rekoder", RekoderToolWindowFactory.MAIN_WINDOW_ID).
                createNotification(message, type);
        Notifications.Bus.notify(notification, project);
    }
}
