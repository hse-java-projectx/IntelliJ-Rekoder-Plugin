package ru.hse.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.components.JBList;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Folder;
import ru.hse.plugin.managers.MembersManager;
import ru.hse.plugin.managers.ProblemsTreeManager;
import ru.hse.plugin.managers.TeamsManager;
import ru.hse.plugin.ui.window.RekoderToolWindowFactory;
import ru.hse.plugin.utils.DataKeys;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

public class LoginAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        setupProblemsTree(e);
        setupTeamsList(e);
        setUpMembersList(e);
    }

    private void setupProblemsTree(AnActionEvent e) {
        Tree tree = RekoderToolWindowFactory.getDataContext(e.getProject()).getData(DataKeys.PROBLEMS_TREE);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        List<Folder> rootFolders = ProblemsTreeManager.getRootFolders();
        for (Folder folder : rootFolders) {
            root.add(folder);
        }

        DefaultTreeModel problemsModel = new DefaultTreeModel(root);

        tree.setModel(problemsModel);
    }

    private void setupTeamsList(AnActionEvent e) {
        JBList<Object> teams = RekoderToolWindowFactory.getDataContext(e.getProject()).getData(DataKeys.TEAMS_LIST);
        List<Object> teamsList = TeamsManager.getTeams();
        DefaultListModel<Object> model = new DefaultListModel<>();
        model.addAll(teamsList);
        teams.setModel(model);
    }

    private void setUpMembersList(AnActionEvent e) {
        JBList<Object> members = RekoderToolWindowFactory.getDataContext(e.getProject()).getData(DataKeys.MEMBERS_LIST);
        List<Object> membersList = MembersManager.getMembers();
        DefaultListModel<Object> model = new DefaultListModel<>();
        model.addAll(membersList);
        members.setModel(model);
    }
}
