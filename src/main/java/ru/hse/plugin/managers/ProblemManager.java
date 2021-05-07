package ru.hse.plugin.managers;

import com.intellij.openapi.project.Project;
import ru.hse.plugin.data.Problem;
import ru.hse.plugin.data.Test;
import ru.hse.plugin.ui.window.RekoderToolWindowFactory;
import ru.hse.plugin.ui.window.SubmissionPanel;
import ru.hse.plugin.ui.window.TestsPanel;
import ru.hse.plugin.utils.DataKeys;

import java.util.List;

public class ProblemManager {
    public static List<Test> getTests(Project project) {
        return getTestsPanel(project).getTests();
    }

    public static void clearTests(Project project) {
        getTestsPanel(project).clearTests();
    }

    public static void setTests(Project project, List<? extends Test> tests, boolean canChangeTests) {
        TestsPanel testsPanel = getTestsPanel(project);
        testsPanel.setTests(tests, canChangeTests);
    }

    public static void clearProblem(Project project) {
        getSubmissionPanel(project).clearProblem();
    }

    public static void setProblem(Project project, Problem problem) {
        getSubmissionPanel(project).setProblem(problem);
    }



    private static TestsPanel getTestsPanel(Project project) {
        return RekoderToolWindowFactory.getProblemDataContext(project).getData(DataKeys.TESTS_PANEL);
    }

    private static SubmissionPanel getSubmissionPanel(Project project) {
        return RekoderToolWindowFactory.getProblemDataContext(project).getData(DataKeys.SUBMISSION_PANEL);
    }
}
