package ru.hse.plugin.data;

public class Folder extends TreeFile {
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

    public String getName() {
        return name;
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
