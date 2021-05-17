package ru.hse.plugin.managers;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.treeStructure.Tree;
import ru.hse.plugin.data.*;
import ru.hse.plugin.exceptions.UnauthorizedException;
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
    private final Project project;

    public ExplorerManager(Project project) {
        this.project = project;
    }

    public void clearEverything() {
        clearProblemPanel();
        clearProblemsTree();
        clearTeamsList();
    }

    public TreeModel getContentHolderTreeModel(ContentHolder contentHolder) throws UnauthorizedException {
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


    public void updateProblemsTree(TreeModel model) {
        Tree tree = getProblemsTree();
        ThreadUtils.runWriteAction(() -> {
            tree.setModel(model);
            tree.updateUI();
        });
    }

    public void clearProblemsTree() {
        Tree tree = getProblemsTree();
        ThreadUtils.runWriteAction(() -> {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.setRoot(new DefaultMutableTreeNode("Root"));
            tree.updateUI();
        });
    }




    public void updateTeamsList() throws UnauthorizedException {
        BackendManager backendManager = new BackendManager(Credentials.getInstance());
        JBList<ContentHolder> teams = getTeamsList();
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
        updateProblemsTree(getContentHolderTreeModel(user));
    }

    public void clearTeamsList() {
        JBList<ContentHolder> teams = getTeamsList();
        ThreadUtils.runWriteAction(() -> {
            CollectionListModel<ContentHolder> model = (CollectionListModel<ContentHolder>) teams.getModel();
            model.removeAll();
            teams.updateUI();
        });
    }




    public void updateProblemPanel(Problem problem) {
        ProblemPanel problemPanel = getProblemPanel();
        ThreadUtils.runWriteAction(() -> {
            problemPanel.setProblem(problem);
            problemPanel.updateUI();
        });
    }

    public void clearProblemPanel() {
        ProblemPanel problemPanel = getProblemPanel();
        ThreadUtils.runWriteAction(() -> {
            problemPanel.clearProblem();
            problemPanel.updateUI();
        });
    }


    private ProblemPanel getProblemPanel() {
        AtomicReference<ProblemPanel> problemPanel = new AtomicReference<>();
        ReadAction.run(() -> problemPanel.set(RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.PROBLEM_PANEL)));
        return problemPanel.get();
    }

    private JBList<ContentHolder> getTeamsList() {
        AtomicReference<JBList<ContentHolder>> list = new AtomicReference<>();
        ReadAction.run(() -> list.set(RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.TEAMS_LIST)));
        return list.get();
    }

    private Tree getProblemsTree() {
        AtomicReference<Tree> tree = new AtomicReference<>();
        ReadAction.run(() -> tree.set(RekoderToolWindowFactory.getExplorerDataContext(project).getData(DataKeys.PROBLEMS_TREE)));
        return tree.get();
    }
}
