package ru.hse.ui.window;

import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import java.awt.*;

public class RekoderMainToolWindow {
    private TreeModel problemsModel;
    private final DefaultListModel<String> teamsModel = new DefaultListModel<>();
    private final DefaultListModel<String> membersModel = new DefaultListModel<>();

    private final SimpleToolWindowPanel mainPanel = new SimpleToolWindowPanel(true, true);

    public RekoderMainToolWindow() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JComponent explorer = setupExplorerPart();
        JComponent problemInfo = setupProblemInfoPart();
        JComponent tests = setupTestsPart();

        JBSplitter s1 = new JBSplitter(true, 0.3f);
        s1.setFirstComponent(explorer);

        JBSplitter s2 = new JBSplitter(true, 0.6f);
        s2.setFirstComponent(problemInfo);
        s2.setSecondComponent(tests);
        s1.setSecondComponent(s2);

        panel.add(s1);
        mainPanel.setContent(panel);
    }

    public RekoderMainToolWindow(ToolWindow toolWindow) {
    }

    private JComponent setupExplorerPart() {
        JBList<String> teams = new JBList<>(teamsModel);
        JBList<String> members = new JBList<>(membersModel);

        for (int k = 0; k < 100; k++) {
            teamsModel.addElement("Team " + k);
            membersModel.addElement("Member " + k);
        }

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        MutableTreeNode folder1 = new DefaultMutableTreeNode("Folder1");
        folder1.insert(new DefaultMutableTreeNode("Folder1.1"), 0);
        folder1.insert(new DefaultMutableTreeNode("Problem1.1"), 1);
        MutableTreeNode folder2 = new DefaultMutableTreeNode("Folder2");
        folder2.insert(new DefaultMutableTreeNode("Folder2.1"), 0);
        folder2.insert(new DefaultMutableTreeNode("Problem2.1"), 1);
        root.add(folder1);
        root.add(folder2);

        problemsModel = new DefaultTreeModel(root);
        Tree problemsTree = new Tree(problemsModel);
        problemsTree.setRootVisible(false);

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
            tests.add(new JButton("Test" + k));
        }
        return new JBScrollPane(tests, JBScrollPane.VERTICAL_SCROLLBAR_NEVER, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    @NotNull
    public JPanel getContent() {
        return mainPanel;
    }
    
}
