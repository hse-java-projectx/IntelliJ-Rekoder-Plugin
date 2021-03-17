package ru.hse.plugin.ui.window;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.treeStructure.Tree;
import icons.RekoderIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hse.plugin.data.Team;
import ru.hse.plugin.data.User;
import ru.hse.plugin.ui.renderers.MembersListRenderer;
import ru.hse.plugin.ui.renderers.TeamsListRenderer;
import ru.hse.plugin.utils.DataKeys;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class RekoderMainToolWindow extends SimpleToolWindowPanel implements DataProvider {
    private Tree problemsTree;
    private JBList<Object> teams;
    private JBList<Object> members;

    private final SimpleToolWindowPanel mainPanel = new SimpleToolWindowPanel(true, true);

    public RekoderMainToolWindow() {
        super(true, true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JComponent explorer = setupExplorerPart();
        JComponent problemInfo = setupProblemInfoPart();
        JComponent tests = setupTestsPart();

        JBSplitter s1 = new JBSplitter(true, 0.25f);
        s1.setFirstComponent(explorer);

        JBSplitter s2 = new JBSplitter(true, 0.65f);
        s2.setFirstComponent(problemInfo);
        s2.setSecondComponent(tests);
        s1.setSecondComponent(s2);

        panel.add(s1);
        mainPanel.setContent(panel);

        ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar("rekoder Toolbar",
                (DefaultActionGroup) actionManager.getAction("rekoder.MainWindowToolbar"), true);

        mainPanel.setToolbar(actionToolbar.getComponent());
        setContent(mainPanel);
    }

    public RekoderMainToolWindow(ToolWindow toolWindow) {
        super(true, true);
    }

    private JComponent setupExplorerPart() {
        DefaultListModel<Object> teamsModel = new DefaultListModel<>();
        DefaultListModel<Object> membersModel = new DefaultListModel<>();
        teams = new JBList<>(teamsModel);
        teams.setCellRenderer(new TeamsListRenderer());
        members = new JBList<>(membersModel);
        members.setCellRenderer(new MembersListRenderer());

        teamsModel.addElement(new User("Personal"));
        for (int k = 0; k < 100; k++) {
            teamsModel.addElement(new Team("Team " + k));
            membersModel.addElement(new User("Member " + k));
        }

        problemsTree = new Tree();
        problemsTree.setRootVisible(false);
        problemsTree.getEmptyText().clear().appendLine("Please Login to view problems");

        JBSplitter horizontalSplitter = new JBSplitter(false, 0.6f);
        horizontalSplitter.setFirstComponent(new JBScrollPane(problemsTree, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));


        JBSplitter verticalSplitter = new JBSplitter(true);
        verticalSplitter.setFirstComponent(new JBScrollPane(teams, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        verticalSplitter.setSecondComponent(new JBScrollPane(members, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        horizontalSplitter.setSecondComponent(verticalSplitter);

        return horizontalSplitter;
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

    @Override
    public @Nullable Object getData(@NotNull String dataId) {
        if (DataKeys.PROBLEMS_TREE.is(dataId)) {
            return problemsTree;
        } else if (DataKeys.TEAMS_LIST.is(dataId)) {
            return teams;
        } else if (DataKeys.MEMBERS_LIST.is(dataId)) {
            return members;
        }
        return super.getData(dataId);
    }
}
