package ru.hse.plugin.managers;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.treeStructure.Tree;
import ru.hse.plugin.data.*;
import ru.hse.plugin.ui.window.ProblemPanel;
import ru.hse.plugin.ui.window.RekoderToolWindowFactory;
import ru.hse.plugin.utils.DataKeys;
import ru.hse.plugin.utils.ThreadUtils;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ExplorerManager {
    public static TreeModel getContentHolderTreeModel(ContentHolder contentHolder) {
        if (contentHolder.getProblemsModel() != null) {
            return contentHolder.getProblemsModel();
        }
        List<Folder> rootFolders;
        BackendManager backendManager = new BackendManager(Credentials.getInstance());
        if (contentHolder instanceof User) {
            rootFolders = backendManager.getPersonalRootFolders();
        } else {
            rootFolders = backendManager.getRootFolders((Team) contentHolder);
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
        ThreadUtils.runWriteAction(() -> {
            tree.setModel(model);
            tree.updateUI();
        });
    }

    public static void clearProblemsTree(Project project) {
        Tree tree = getProblemsTree(project);
        ThreadUtils.runWriteAction(() -> {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.setRoot(new DefaultMutableTreeNode("Root"));
            tree.updateUI();
        });
    }




    public static void updateTeamsList(Project project) {
        BackendManager backendManager = new BackendManager(Credentials.getInstance());
        JBList<ContentHolder> teams = getTeamsList(project);
        List<Team> teamsList = backendManager.getTeams();
        CollectionListModel<ContentHolder> model = new CollectionListModel<>();
        User user = backendManager.getUser();
        model.add(user);
        model.add(teamsList);
        ThreadUtils.runWriteAction(() -> {
            teams.setModel(model);
            teams.setSelectedIndex(0);
            teams.updateUI();
        });
        updateProblemsTree(project, getContentHolderTreeModel(user));
    }

    public static void clearTeamsList(Project project) {
        JBList<ContentHolder> teams = getTeamsList(project);
        ThreadUtils.runWriteAction(() -> {
            CollectionListModel<ContentHolder> model = (CollectionListModel<ContentHolder>) teams.getModel();
            model.removeAll();
            teams.updateUI();
        });
    }




    public static void updateProblemPanel(Project project, Problem problem) {
        ProblemPanel problemPanel = getProblemPanel(project);
        ThreadUtils.runWriteAction(() -> {
            problemPanel.setProblem(problem);
            problemPanel.updateUI();
        });
    }

    public static void clearProblemPanel(Project project) {
        ProblemPanel problemPanel = getProblemPanel(project);
        ThreadUtils.runWriteAction(() -> {
            problemPanel.clearProblem();
            problemPanel.updateUI();
        });
    }


    private static ProblemPanel getProblemPanel(Project project) {
        AtomicReference<ProblemPanel> problemPanel = new AtomicReference<>();
        ReadAction.run(() -> problemPanel.set(RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.PROBLEM_PANEL)));
        return problemPanel.get();
    }

    private static JBList<ContentHolder> getTeamsList(Project project) {
        AtomicReference<JBList<ContentHolder>> list = new AtomicReference<>();
        ReadAction.run(() -> list.set(RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.TEAMS_LIST)));
        return list.get();
    }

    private static Tree getProblemsTree(Project project) {
        AtomicReference<Tree> tree = new AtomicReference<>();
        ReadAction.run(() -> tree.set(RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.PROBLEMS_TREE)));
        return tree.get();
    }
}
