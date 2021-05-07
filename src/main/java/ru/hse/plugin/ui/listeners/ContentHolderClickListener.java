package ru.hse.plugin.ui.listeners;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;
import ru.hse.plugin.data.ContentHolder;
import ru.hse.plugin.managers.ExplorerManager;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreeModel;

public class ContentHolderClickListener implements ListSelectionListener {
    private final Project project;

    public ContentHolderClickListener(Project project) {
        this.project = project;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!(e.getSource() instanceof JBList)) {
            return;
        }
        JBList<ContentHolder> list = (JBList<ContentHolder>) e.getSource();
        ContentHolder contentHolder = list.getSelectedValue();
        if (contentHolder != null) {
            TreeModel model = ExplorerManager.getContentHolderTreeModel(contentHolder);
            ExplorerManager.updateProblemsTree(project, model);
        }
    }
}
