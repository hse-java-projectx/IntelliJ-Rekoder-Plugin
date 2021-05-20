package ru.hse.plugin.data;

import com.google.api.client.util.Key;

public class Team extends ContentHolder {
    @Key
    private String name = "";
    @Key
    private int rootFolderId = -1;

    public Team() {

    }

    public String getName() {
        return name;
    }

    public int getRootFolderId() {
        return rootFolderId;
    }

    @Override
    public String toString() {
        return name;
    }
}
