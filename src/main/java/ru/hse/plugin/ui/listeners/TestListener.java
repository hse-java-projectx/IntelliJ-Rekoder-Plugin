package ru.hse.plugin.ui.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Test;
import ru.hse.plugin.executors.DefaultExecutor;
import ru.hse.plugin.managers.ProblemManager;
import ru.hse.plugin.utils.StringUtils;
import ru.hse.plugin.utils.ThreadUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class TestListener implements ActionListener {
    private final Project project;

    public TestListener(Project project) {
        this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ThreadUtils.runInBackground(() -> {
            ProblemManager problemManager = new ProblemManager(project);
            problemManager.runTests();
        });
    }
}
