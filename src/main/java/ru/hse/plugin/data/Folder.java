package ru.hse.plugin.data;

import javax.swing.tree.DefaultMutableTreeNode;

public class Folder extends DefaultMutableTreeNode {
    private final String name;
    private boolean loaded = false;

    public Folder(String name) {
        this.name = name;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded() {
        loaded = true;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
