package ru.hse.plugin.utils;

import com.intellij.notification.*;
import com.intellij.openapi.project.Project;

public class NotificationUtils {
    private static final NotificationGroup LOG_NOTIFICATION_GROUP =
            NotificationGroupManager.getInstance().getNotificationGroup("Rekoder log");
    public static NotificationGroup TOOL_WINDOW_NOTIFICATION_GROUP =
            NotificationGroupManager.getInstance().getNotificationGroup("Rekoder tool window");

    public static void showToolWindowMessage(String message, NotificationType type, Project project) {
        Notification notification = TOOL_WINDOW_NOTIFICATION_GROUP.createNotification(message, type);
        Notifications.Bus.notify(notification, project);
    }

    public static void showAuthorisationFailedNotification(Project project) {
        showToolWindowMessage("Authentication failed", NotificationType.ERROR, project);
    }

    public static void showNetworkProblemNotification(Project project) {
        showToolWindowMessage("Network problem", NotificationType.ERROR, project);
    }

    public static void log(Project project, String message, NotificationType notificationType) {
        LOG_NOTIFICATION_GROUP.createNotification(message, notificationType).notify(project);
    }
}
