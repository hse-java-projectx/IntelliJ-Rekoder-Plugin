package ru.hse.plugin.managers;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import ru.hse.plugin.data.Problem;
import ru.hse.plugin.data.Test;
import ru.hse.plugin.ui.window.RekoderToolWindowFactory;
import ru.hse.plugin.ui.window.SubmissionPanel;
import ru.hse.plugin.ui.window.TestsPanel;
import ru.hse.plugin.utils.DataKeys;
import ru.hse.plugin.utils.ThreadUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ProblemManager {
    public static List<Test> getTests(Project project) {
        TestsPanel testsPanel = getTestsPanel(project);
        AtomicReference<List<Test>> tests = new AtomicReference<>();
        ThreadUtils.runWriteAction(() -> tests.set(testsPanel.getTests()));
        return tests.get();
    }

    public static void clearTests(Project project) {
        TestsPanel testsPanel = getTestsPanel(project);
        ThreadUtils.runWriteAction(testsPanel::clearTests);
    }

    public static void setTests(Project project, List<? extends Test> tests) {
        TestsPanel testsPanel = getTestsPanel(project);
        ThreadUtils.runWriteAction(() -> testsPanel.setTests(tests));
    }

    public static void clearProblem(Project project) {
        SubmissionPanel submissionPanel = getSubmissionPanel(project);
        ThreadUtils.runWriteAction(submissionPanel::clearProblem);
    }

    public static void setProblem(Project project, Problem problem) {
        SubmissionPanel submissionPanel = getSubmissionPanel(project);
        ThreadUtils.runWriteAction(() -> submissionPanel.setProblem(problem));
    }



    private static TestsPanel getTestsPanel(Project project) {
        AtomicReference<TestsPanel> testsPanel = new AtomicReference<>();
        ReadAction.run(() -> testsPanel.set(RekoderToolWindowFactory.getProblemDataContext(project).getData(DataKeys.TESTS_PANEL)));
        return testsPanel.get();
    }

    private static SubmissionPanel getSubmissionPanel(Project project) {
        AtomicReference<SubmissionPanel> submissionPanel = new AtomicReference<>();
        ReadAction.run(() -> submissionPanel.set(RekoderToolWindowFactory.getProblemDataContext(project).getData(DataKeys.SUBMISSION_PANEL)));
        return submissionPanel.get();
    }
}
