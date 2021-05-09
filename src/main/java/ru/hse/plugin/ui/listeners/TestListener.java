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
        List<Test> list = ProblemManager.getTests(project);
        if (list.isEmpty()) return;
        DefaultExecutor runExecutor = new DefaultExecutor();
        ProgressManager progressManager = new ProgressManagerImpl();
        progressManager.run(new Task.Backgroundable(project, "Testing", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                for (Test test : list) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        ApplicationManager.getApplication().runWriteAction(() -> test.setStatus(Test.Status.TESTING));
                    });
                    System.out.println(test.getInput());
                    System.out.println(test.getExpectedOutput());
                    Optional<String> result = runExecutor.execute(project, indicator, test.getInput());
                    Test.Status status;
                    if (result.isPresent()) {
                        System.out.println(result.get());
                        String expected = StringUtils.trimEndLines(test.getExpectedOutput());
                        String actual = StringUtils.trimEndLines(result.get());
                        if (expected.equals(actual)) {
                            status = Test.Status.PASSED;
                        } else {
                            status = Test.Status.FAILED;
                        }
                    } else {
                        status = Test.Status.ERROR;
                    }
                    System.out.println("Status: " + status);
                    ApplicationManager.getApplication().invokeLater(() -> {
                        ApplicationManager.getApplication().runWriteAction(() -> {
                            test.setStatus(status);
                            result.ifPresent(s -> test.setActualOutput(StringUtils.trimEndLines(s)));
                        });
                    });
                }
            }

            @Override
            public void onCancel() {
                for (Test test : list) {
                    if (test.getStatus().equals(Test.Status.TESTING)) {
                        ApplicationManager.getApplication().runWriteAction(() -> test.setStatus(Test.Status.NOT_TESTED));
                    }
                }
            }
        });
    }
}
