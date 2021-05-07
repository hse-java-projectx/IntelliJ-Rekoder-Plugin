package ru.hse.plugin.managers;

import com.intellij.openapi.project.Project;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.treeStructure.Tree;
import ru.hse.plugin.data.*;
import ru.hse.plugin.ui.window.ProblemPanel;
import ru.hse.plugin.ui.window.RekoderToolWindowFactory;
import ru.hse.plugin.utils.DataKeys;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.util.List;

public class ExplorerManager {
    public static TreeModel getContentHolderTreeModel(ContentHolder contentHolder) {
        if (contentHolder.getProblemsModel() != null) {
            return contentHolder.getProblemsModel();
        }
        List<Folder> rootFolders;
        if (contentHolder instanceof User) {
            rootFolders = BackendManager.getPersonalRootFolders(Credentials.getInstance());
        } else {
            rootFolders = BackendManager.getRootFolders((Team) contentHolder, Credentials.getInstance());
        }
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        for (Folder folder : rootFolders) {
            root.add(folder);
        }
        DefaultTreeModel problemsModel = new DefaultTreeModel(root);
        contentHolder.setProblemsModel(problemsModel);
        return problemsModel;
    }


    public static void updateProblemsTree(Project project, TreeModel model) {
        Tree tree = getProblemsTree(project);
        tree.setModel(model);
        tree.updateUI();
    }

    public static void clearProblemsTree(Project project) {
        Tree tree = getProblemsTree(project);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(new DefaultMutableTreeNode("Root"));
        tree.updateUI();
    }




    public static void updateTeamsList(Project project) {
        JBList<ContentHolder> teams = getTeamsList(project);
        List<Team> teamsList = BackendManager.getTeams(Credentials.getInstance());
        CollectionListModel<ContentHolder> model = new CollectionListModel<>();
        User user = new User("Personal");
        model.add(user);
        model.add(teamsList);
        teams.setModel(model);
        teams.setSelectedIndex(0);
        teams.updateUI();
        updateProblemsTree(project, getContentHolderTreeModel(user));
    }

    public static void clearTeamsList(Project project) {
        JBList<ContentHolder> teams = getTeamsList(project);
        CollectionListModel<ContentHolder> model = (CollectionListModel<ContentHolder>) teams.getModel();
        model.removeAll();
        teams.updateUI();
    }




    public static void updateProblemPanel(Project project, Problem problem) {
        ProblemPanel problemPanel = getProblemPanel(project);
        problemPanel.setProblem(problem);
        problemPanel.updateUI();
    }

    public static void clearProblemPanel(Project project) {
        ProblemPanel problemPanel = getProblemPanel(project);
        problemPanel.clearProblem();
        problemPanel.updateUI();
    }





    private static ProblemPanel getProblemPanel(Project project) {
        return RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.PROBLEM_PANEL);
    }

    private static JBList<ContentHolder> getTeamsList(Project project) {
        return RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.TEAMS_LIST);
    }

    private static Tree getProblemsTree(Project project) {
        return RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.PROBLEMS_TREE);
    }
}
