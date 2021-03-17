package ru.hse.plugin.ui.window;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class RekoderProblemWindow extends SimpleToolWindowPanel implements DataProvider {

    private final SimpleToolWindowPanel mainPanel = new SimpleToolWindowPanel(true, true);

    RekoderProblemWindow() {
        super(true, true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JComponent problemInfo = setupProblemInfoPart();
        JComponent tests = setupTestsPart();
        JBSplitter s1 = new JBSplitter(true, 0.7f);
        s1.setFirstComponent(problemInfo);
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

    JComponent setupProblemInfoPart() {
        JBTextArea code = new JBTextArea(10, 20);
        code.setBackground(JBColor.GRAY);
        return new JBScrollPane(code, JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
}
