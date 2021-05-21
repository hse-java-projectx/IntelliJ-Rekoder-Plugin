package ru.hse.plugin.ui.listeners;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.data.Folder;
import ru.hse.plugin.data.TreeFile;
import ru.hse.plugin.exceptions.HttpException;
import ru.hse.plugin.exceptions.UnauthorizedException;
import ru.hse.plugin.managers.BackendManager;
import ru.hse.plugin.utils.NotificationUtils;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import java.util.List;

public class FolderClickListener implements TreeWillExpandListener {
    private final Project project;

    public FolderClickListener(Project project) {
        this.project = project;
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event) {
        Folder folder = (Folder) event.getPath().getLastPathComponent();
        if (folder.isLoaded()) {
            return;
        }
        try {
            List<TreeFile> files = new BackendManager(Credentials.getInstance()).getFolderFiles(folder.getId());
            files.forEach(folder::add);
            folder.setLoaded();
        } catch (UnauthorizedException ex) {
            NotificationUtils.showAuthorisationFailedNotification(project);
        } catch (HttpException ex) {
            NotificationUtils.showNetworkProblemNotification(project);
            NotificationUtils.log(project, ex.getMessage(), NotificationType.ERROR);
        }
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) {

    }
}
