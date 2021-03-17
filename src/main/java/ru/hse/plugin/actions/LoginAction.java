package ru.hse.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.ui.window.RekoderToolWindowFactory;
import ru.hse.plugin.utils.DataKeys;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

public class LoginAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Tree tree = RekoderToolWindowFactory.getDataContext(e.getProject()).getData(DataKeys.PROBLEMS_TREE);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        MutableTreeNode folder1 = new DefaultMutableTreeNode("Folder1");
        folder1.insert(new DefaultMutableTreeNode("Folder1.1"), 0);
        folder1.insert(new DefaultMutableTreeNode("Problem1.1"), 1);
        MutableTreeNode folder2 = new DefaultMutableTreeNode("Folder2");
        folder2.insert(new DefaultMutableTreeNode("Folder2.1"), 0);
        folder2.insert(new DefaultMutableTreeNode("Problem2.1"), 1);
        root.add(folder1);
        root.add(folder2);

        DefaultTreeModel problemsModel = new DefaultTreeModel(root);
        System.out.println("Hello");

        tree.setModel(problemsModel);
    }
}
