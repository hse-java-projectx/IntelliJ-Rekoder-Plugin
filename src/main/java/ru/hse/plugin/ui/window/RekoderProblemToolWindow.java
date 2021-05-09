package ru.hse.plugin.ui.window;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hse.plugin.data.Problem;
import ru.hse.plugin.utils.DataKeys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class RekoderProblemToolWindow extends SimpleToolWindowPanel implements DataProvider {

    private final SimpleToolWindowPanel mainPanel = new SimpleToolWindowPanel(true, true);

    private final SubmissionPanel submissionPanel;
    private final TestsPanel testsPanel;

    RekoderProblemToolWindow(Project project, ToolWindow toolWindow) {
        super(true, true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        testsPanel = setupTestsPart(project);
        submissionPanel = setupProblemInfoPart(project, toolWindow, testsPanel);

        JBSplitter s1 = new JBSplitter(true, 0.7f);
        s1.setFirstComponent(submissionPanel);
        s1.setSecondComponent(testsPanel);

        panel.add(s1);
        mainPanel.setContent(panel);

//        ActionManager actionManager = ActionManager.getInstance();
//        ActionToolbar actionToolbar = actionManager.createActionToolbar("rekoder Toolbar",
//                (DefaultActionGroup) actionManager.getAction("rekoder.MainWindowToolbar"), true);
//
//        mainPanel.setToolbar(actionToolbar.getComponent());
        setContent(mainPanel);
    }

    public void setProblem(Problem problem) {
        submissionPanel.setProblem(problem);
    }

    private SubmissionPanel setupProblemInfoPart(Project project, ToolWindow toolWindow, TestsPanel testsPanel) {
        return new SubmissionPanel(project, toolWindow, testsPanel);
    }

    private TestsPanel setupTestsPart(Project project) {
        return new TestsPanel(project);
    }

    @Override
    public @Nullable Object getData(@NotNull String dataId) {
        if (DataKeys.SUBMISSION_PANEL.is(dataId)) {
            return submissionPanel;
        } else if (DataKeys.TESTS_PANEL.is(dataId)) {
            return testsPanel;
        }
        return super.getData(dataId);
    }

}
