package ru.hse.plugin.ui.listeners;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.ContentHolder;
import ru.hse.plugin.exceptions.UnauthorizedException;
import ru.hse.plugin.managers.ExplorerManager;
import ru.hse.plugin.utils.NotificationUtils;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreeModel;

public class ContentHolderClickListener implements ListSelectionListener {
    private final Project project;

    public ContentHolderClickListener(Project project) {
        this.project = project;
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public void valueChanged(ListSelectionEvent e) {
        if (!(e.getSource() instanceof JBList)) {
            return;
        }
        JBList<ContentHolder> list = (JBList<ContentHolder>) e.getSource();
        ContentHolder contentHolder = list.getSelectedValue();
        if (contentHolder == null) {
            return;
        }
        ProgressManager progressManager = new ProgressManagerImpl();
        progressManager.run(new Task.Backgroundable(project, "Loading folders", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ExplorerManager explorerManager = new ExplorerManager(project);
                try {
                    TreeModel model = explorerManager.getContentHolderTreeModel(contentHolder);
                    explorerManager.updateProblemsTree(model);
                } catch (UnauthorizedException ex) {
                    NotificationUtils.showAuthorisationFailedNotification(project);
                }
            }
        });
    }
}
