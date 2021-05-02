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
import ru.hse.plugin.utils.DataKeys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class RekoderProblemWindow extends SimpleToolWindowPanel implements DataProvider {

    private final SimpleToolWindowPanel mainPanel = new SimpleToolWindowPanel(true, true);

    private final SubmissionPanel submissionPanel;

    RekoderProblemWindow(Project project, ToolWindow toolWindow) {
        super(true, true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        submissionPanel = setupProblemInfoPart(project, toolWindow);
        JComponent tests = setupTestsPart();
        JBSplitter s1 = new JBSplitter(true, 0.7f);
        s1.setFirstComponent(submissionPanel);
        s1.setSecondComponent(tests);

        panel.add(s1);
        mainPanel.setContent(panel);

//        ActionManager actionManager = ActionManager.getInstance();
//        ActionToolbar actionToolbar = actionManager.createActionToolbar("rekoder Toolbar",
//                (DefaultActionGroup) actionManager.getAction("rekoder.MainWindowToolbar"), true);
//
//        mainPanel.setToolbar(actionToolbar.getComponent());
        setContent(mainPanel);
    }

    SubmissionPanel setupProblemInfoPart(Project project, ToolWindow toolWindow) {
        return new SubmissionPanel(project, toolWindow);
    }

    JComponent setupTestsPart() {
        JPanel tests = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension dimension = super.getPreferredSize();
                dimension.height = 100;
                return dimension;
            }
        };
        tests.setLayout(new BoxLayout(tests, BoxLayout.X_AXIS));
        for (int k = 0; k < 100; k++) {
            tests.add(new TestPanel(tests).getComponent());
//            JButton button = new JButton("Test" + k);
//            button.addActionListener(a -> {
//                tests.remove(button);
//                tests.updateUI();
//            });
//            tests.add(button, 0);
        }

        JBScrollPane scrollPane = new JBScrollPane(tests, JBScrollPane.VERTICAL_SCROLLBAR_NEVER, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        MouseWheelListener listener = scrollPane.getMouseWheelListeners()[0];
        scrollPane.addMouseWheelListener(e -> {

            MouseWheelEvent event = new MouseWheelEvent(e.getComponent(), e.getID(), e.getWhen(),
                    e.getModifiersEx() | InputEvent.SHIFT_DOWN_MASK,
                    e.getX(), e.getY(),
                    e.getClickCount(), e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation());
            listener.mouseWheelMoved(event);
        });
        return scrollPane;
    }

    @Override
    public @Nullable Object getData(@NotNull String dataId) {
        if (DataKeys.SUBMISSION_PANEL.is(dataId)) {
            return submissionPanel;
        }
        return super.getData(dataId);
    }

}
