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
    private final Project project;

    public ProblemManager(Project project) {
        this.project = project;
    }

    public void clearEverything() {
        clearProblem();
        clearTests();
    }

    public List<Test> getTests() {
        TestsPanel testsPanel = getTestsPanel();
        AtomicReference<List<Test>> tests = new AtomicReference<>();
        ThreadUtils.runWriteAction(() -> tests.set(testsPanel.getTests()));
        return tests.get();
    }

    public void clearTests() {
        TestsPanel testsPanel = getTestsPanel();
        ThreadUtils.runWriteAction(testsPanel::clearTests);
    }

    public void setTests(List<? extends Test> tests) {
        TestsPanel testsPanel = getTestsPanel();
        ThreadUtils.runWriteAction(() -> testsPanel.setTests(tests));
    }

    public void clearProblem() {
        SubmissionPanel submissionPanel = getSubmissionPanel();
        ThreadUtils.runWriteAction(submissionPanel::clearProblem);
    }

    public void setProblem(Problem problem) {
        SubmissionPanel submissionPanel = getSubmissionPanel();
        ThreadUtils.runWriteAction(() -> submissionPanel.setProblem(problem));
    }



    private TestsPanel getTestsPanel() {
        AtomicReference<TestsPanel> testsPanel = new AtomicReference<>();
        ReadAction.run(() -> testsPanel.set(RekoderToolWindowFactory.getProblemDataContext(project).getData(DataKeys.TESTS_PANEL)));
        return testsPanel.get();
    }

    private SubmissionPanel getSubmissionPanel() {
        AtomicReference<SubmissionPanel> submissionPanel = new AtomicReference<>();
        ReadAction.run(() -> submissionPanel.set(RekoderToolWindowFactory.getProblemDataContext(project).getData(DataKeys.SUBMISSION_PANEL)));
        return submissionPanel.get();
    }
}
