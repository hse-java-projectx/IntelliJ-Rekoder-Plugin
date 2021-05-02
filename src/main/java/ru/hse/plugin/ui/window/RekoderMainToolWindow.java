package ru.hse.plugin.ui.window;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hse.plugin.data.*;
import ru.hse.plugin.managers.BackendConnection;
import ru.hse.plugin.ui.renderers.ProblemsTreeRenderer;
import ru.hse.plugin.ui.renderers.TeamsListRenderer;
import ru.hse.plugin.utils.DataKeys;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import java.awt.event.*;
import java.util.List;

public class RekoderMainToolWindow extends SimpleToolWindowPanel implements DataProvider {
    private Tree problemsTree;
    private JBList<Object> teams;

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
        teams = new JBList<>(teamsModel);
        new ListSpeedSearch<>(teams);
        teams.setCellRenderer(new TeamsListRenderer());
        teams.setEmptyText("Teams");
        teams.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        teams.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println(teams.getSelectedValue());
                }
            }
        }); // TODO: вынести в отдельный файл

        problemsTree = new Tree();
        new TreeSpeedSearch(problemsTree);
        problemsTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) {
                Folder folder = (Folder) event.getPath().getLastPathComponent();
                if (!folder.isLoaded()) {
                    folder.setLoaded();
                    List<TreeFile> files = BackendConnection.loadFolder(folder.getName(), Credentials.getInstance());
                    files.forEach(folder::add);
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

        horizontalSplitter.setSecondComponent(new JBScrollPane(teams, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

        return horizontalSplitter;
    }

    @Override
    public @Nullable Object getData(@NotNull String dataId) {
        if (DataKeys.PROBLEMS_TREE.is(dataId)) {
            return problemsTree;
        } else if (DataKeys.TEAMS_LIST.is(dataId)) {
            return teams;
        } else if (DataKeys.PROBLEM_PANEL.is(dataId)) {
            return problemPanel;
        }
        return super.getData(dataId);
    }
}
