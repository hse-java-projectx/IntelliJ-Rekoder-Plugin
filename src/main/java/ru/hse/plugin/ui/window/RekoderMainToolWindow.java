package ru.hse.plugin.ui.window;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.ColoredSideBorder;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.HtmlPanel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hse.plugin.data.Folder;
import ru.hse.plugin.data.Team;
import ru.hse.plugin.data.User;
import ru.hse.plugin.managers.ProblemsTreeManager;
import ru.hse.plugin.ui.renderers.MembersListRenderer;
import ru.hse.plugin.ui.renderers.ProblemsTreeRenderer;
import ru.hse.plugin.ui.renderers.TeamsListRenderer;
import ru.hse.plugin.utils.DataKeys;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import java.awt.*;
import java.awt.event.*;

public class RekoderMainToolWindow extends SimpleToolWindowPanel implements DataProvider {
    private Tree problemsTree;
    private JBList<Object> teams;
    private JBList<Object> members;

    private ProblemPanel problemPanel;

    private final SimpleToolWindowPanel mainPanel = new SimpleToolWindowPanel(true, true);

    public RekoderMainToolWindow(Project project, ToolWindow toolWindow) {
        super(true, true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JComponent explorer = setupExplorerPart();
        problemPanel = new ProblemPanel(project, toolWindow);

        JBSplitter s1 = new JBSplitter(true, 0.3f);
        s1.setFirstComponent(explorer);
        s1.setSecondComponent(problemPanel);

        panel.add(s1);
        mainPanel.setContent(panel);

        ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar("rekoder Toolbar",
                (DefaultActionGroup) actionManager.getAction("rekoder.MainWindowToolbar"), true);

        mainPanel.setToolbar(actionToolbar.getComponent());
        setContent(mainPanel);
    }

//    public RekoderMainToolWindow(ToolWindow toolWindow) {
//        super(true, true);
//    }

    private JComponent setupExplorerPart() {
        DefaultListModel<Object> teamsModel = new DefaultListModel<>();
        DefaultListModel<Object> membersModel = new DefaultListModel<>();
        teams = new JBList<>(teamsModel);
        teams.setCellRenderer(new TeamsListRenderer());
        teams.setEmptyText("Teams");
        members = new JBList<>(membersModel);
        members.setCellRenderer(new MembersListRenderer());
        members.setEmptyText("Members");

        teams.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println(teams.getSelectedValue());
                }
            }
        }); // TODO: вынести в отдельный файл

        problemsTree = new Tree();
        problemsTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                Folder folder = (Folder) event.getPath().getLastPathComponent();
                if (!folder.isLoaded()) {
                    folder.setLoaded();
                    ProblemsTreeManager.loadFolder(folder);
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }
        }); // TODO: вынести в отдельный файл
        problemsTree.setRootVisible(false);
        problemsTree.getEmptyText().clear().appendLine("Please Login to view problems");
        problemsTree.setCellRenderer(new ProblemsTreeRenderer());

        JBSplitter horizontalSplitter = new JBSplitter(false, 0.6f);
        horizontalSplitter.setFirstComponent(new JBScrollPane(problemsTree, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));


        JBSplitter verticalSplitter = new JBSplitter(true);
        verticalSplitter.setFirstComponent(new JBScrollPane(teams, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        verticalSplitter.setSecondComponent(new JBScrollPane(members, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        horizontalSplitter.setSecondComponent(verticalSplitter);

        return horizontalSplitter;
    }

    @Override
    public @Nullable Object getData(@NotNull String dataId) {
        if (DataKeys.PROBLEMS_TREE.is(dataId)) {
            return problemsTree;
        } else if (DataKeys.TEAMS_LIST.is(dataId)) {
            return teams;
        } else if (DataKeys.MEMBERS_LIST.is(dataId)) {
            return members;
        } else if (DataKeys.PROBLEM_PANEL.is(dataId)) {
            return problemPanel;
        }
        return super.getData(dataId);
    }
}
