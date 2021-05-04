package ru.hse.plugin.managers;

import com.intellij.openapi.project.Project;
import ru.hse.plugin.data.Test;
import ru.hse.plugin.ui.window.RekoderToolWindowFactory;
import ru.hse.plugin.ui.window.SubmissionPanel;
import ru.hse.plugin.ui.window.TestsPanel;
import ru.hse.plugin.utils.DataKeys;

import java.util.List;

public class ProblemWindowManager {
    public static List<Test> getTests(Project project) {
        return getTestsPanel(project).getTests();
    }

    public static void clearTests(Project project) {
        getTestsPanel(project).clearTests();
    }

    public static void setTests(Project project, List<? extends Test> tests) {
        TestsPanel testsPanel = getTestsPanel(project);
        testsPanel.setTests(tests);
    }

    public static void clearSubmission(Project project) {
        getSubmissionPanel(project).clearSubmission();
    }





    private static TestsPanel getTestsPanel(Project project) {
        return RekoderToolWindowFactory.getProblemDataContext(project).getData(DataKeys.TESTS_PANEL);
    }

    private static SubmissionPanel getSubmissionPanel(Project project) {
        return RekoderToolWindowFactory.getProblemDataContext(project).getData(DataKeys.SUBMISSION_PANEL);
    }
}
