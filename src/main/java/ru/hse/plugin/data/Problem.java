package ru.hse.plugin.data;

import javax.swing.tree.DefaultMutableTreeNode;

public class Problem extends DefaultMutableTreeNode {
    protected final String name;

    public Problem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
