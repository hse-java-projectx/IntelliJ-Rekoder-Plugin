package ru.hse.plugin.utils;


import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ThreadUtils {
    public static void runWriteAction(Runnable runnable) {
        ThreadUtils.runInEdt(() -> WriteAction.run(runnable::run));
    }

    public static void runInEdt(Runnable runnable) {
        Application app = ApplicationManager.getApplication();
        if (app.isDispatchThread()) {
            runnable.run();
        }
        else {
            app.invokeLater(runnable);
        }
    }
}
