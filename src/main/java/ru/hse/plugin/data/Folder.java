package ru.hse.plugin.data;

import com.google.api.client.util.Key;

import java.util.List;

public class Folder extends TreeFile {
    @Key
    private String name;
    @Key("subfolders")
    private List<Folder> subFolders;


    private boolean loaded = false;

    public Folder() {

    }

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



    public List<Folder> getSubFolders() {
        return subFolders;
    }

    public void setSubFolders(List<Folder> subFolders) {
        this.subFolders = subFolders;
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
