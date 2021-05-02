package ru.hse.plugin.managers;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;
import com.intellij.ui.treeStructure.Tree;
import ru.hse.plugin.data.*;
import ru.hse.plugin.ui.window.ProblemPanel;
import ru.hse.plugin.ui.window.RekoderToolWindowFactory;
import ru.hse.plugin.utils.DataKeys;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

public class MainWindowManager {
    public static void updateProblemsTree(Project project) {
        Tree tree = RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.PROBLEMS_TREE);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        List<Folder> rootFolders = BackendConnection.getPersonalRootFolders(Credentials.getInstance());
        for (Folder folder : rootFolders) {
            root.add(folder);
        }

        DefaultTreeModel problemsModel = new DefaultTreeModel(root);

        tree.setModel(problemsModel);
    }

    public static void clearProblemsTree(Project project) {
        Tree tree = RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.PROBLEMS_TREE);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(new DefaultMutableTreeNode("Root"));
    }

    public static void updateTeamsList(Project project) {
        JBList<Object> teams = RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.TEAMS_LIST);
        List<Team> teamsList = BackendConnection.getTeams(Credentials.getInstance());
        DefaultListModel<Object> model = new DefaultListModel<>();
        model.addElement(new User("Personal"));
        model.addAll(teamsList);
        teams.setModel(model);
        teams.setSelectedIndex(0);
    }

    public static void clearTeamsList(Project project) {
        JBList<Object> teams = RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.TEAMS_LIST);
        DefaultListModel<Object> model = (DefaultListModel<Object>) teams.getModel();
        model.removeAllElements();
    }

    public static void updateProblemPanel(Project project, Problem problem) {
        ProblemPanel problemPanel = RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.PROBLEM_PANEL);
        problemPanel.setProblem(problem);
    }

    public static void clearProblemPanel(Project project) {
        ProblemPanel problemPanel = RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.PROBLEM_PANEL);
        problemPanel.clearProblem();
    }
}
