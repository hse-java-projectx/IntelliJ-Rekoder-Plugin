package ru.hse.plugin.data;

import com.google.api.client.util.Key;

import java.util.List;

public class Folder extends TreeFile {
    @Key
    private String name;
    @Key
    private int id;


    private boolean loaded = false;

    public Folder() {

    }

    public Folder(String name, int id) {
        this.name = name;
        this.id = id;
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
