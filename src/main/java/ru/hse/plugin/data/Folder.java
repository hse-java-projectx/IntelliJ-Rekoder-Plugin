package ru.hse.plugin.data;

import javax.swing.tree.DefaultMutableTreeNode;

public class Folder extends DefaultMutableTreeNode {
    private final String name;

    public Folder(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
