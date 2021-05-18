package ru.hse.plugin.managers;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Problem;
import ru.hse.plugin.data.ProblemPool;
import ru.hse.plugin.data.Test;
import ru.hse.plugin.executors.DefaultExecutor;
import ru.hse.plugin.ui.window.RekoderToolWindowFactory;
import ru.hse.plugin.ui.window.SubmissionPanel;
import ru.hse.plugin.ui.window.TestsPanel;
import ru.hse.plugin.utils.DataKeys;
import ru.hse.plugin.utils.StringUtils;
import ru.hse.plugin.utils.ThreadUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class ProblemManager {
    private final Project project;

    public ProblemManager(Project project) {
        this.project = project;
    }

    public void clearEverything() {
        clearProblem();
        clearTests();
        ProblemPool.getInstance().clear();
    }

    public List<Test> getTests() {
        TestsPanel testsPanel = getTestsPanel();
        AtomicReference<List<Test>> tests = new AtomicReference<>();
        ReadAction.run(() -> tests.set(testsPanel.getTests()));
        return tests.get();
    }

    // return true is all tests have passed
    public boolean runTests() {
        List<Test> list = getTests();
        if (list.isEmpty()) {
            return true;
        }
        final boolean[] allPassed = {true};
        CountDownLatch latch = new CountDownLatch(1);
        DefaultExecutor runExecutor = new DefaultExecutor();
        ProgressManager progressManager = new ProgressManagerImpl();
        progressManager.run(new Task.Backgroundable(project, "Testing", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {

                for (Test test : list) {
                    ThreadUtils.runWriteAction(() -> test.setStatus(Test.Status.TESTING));
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
                            allPassed[0] = false;
                        }
                    } else {
                        status = Test.Status.ERROR;
                        allPassed[0] = false;
                    }
                    System.out.println("Status: " + status);
                    ThreadUtils.runWriteAction(() -> {
                        test.setStatus(status);
                        result.ifPresent(s -> test.setActualOutput(StringUtils.trimEndLines(s)));
                    });

                }
                latch.countDown();
            }

            @Override
            public void onCancel() {
                latch.countDown();
                for (Test test : list) {
                    if (test.getStatus().equals(Test.Status.TESTING)) {
                        ThreadUtils.runWriteAction(() -> test.setStatus(Test.Status.NOT_TESTED));
                    }
                }
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            return false;
        }
        return allPassed[0];
    }

    private void clearTests() {
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
        ThreadUtils.runWriteAction(() -> submissionPanel.setCurrentProblem(problem));
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
