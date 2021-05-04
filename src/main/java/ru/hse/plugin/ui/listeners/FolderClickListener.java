package ru.hse.plugin.ui.listeners;

import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.data.Folder;
import ru.hse.plugin.data.TreeFile;
import ru.hse.plugin.managers.BackendManager;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import java.util.List;

public class FolderClickListener implements TreeWillExpandListener {
    @Override
    public void treeWillExpand(TreeExpansionEvent event) {
        Folder folder = (Folder) event.getPath().getLastPathComponent();
        if (!folder.isLoaded()) {
            folder.setLoaded();
            List<TreeFile> files = BackendManager.loadFolder(folder.getName(), Credentials.getInstance());
            files.forEach(folder::add);
        }
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) {

    }
}
