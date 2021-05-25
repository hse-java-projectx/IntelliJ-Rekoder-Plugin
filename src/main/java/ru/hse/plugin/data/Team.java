package ru.hse.plugin.data;

import com.google.api.client.util.Key;

public class Team extends ContentHolder {
    @Key
    private String name = "";
    @Key
    private int rootFolderId = -1;
    @Key
    private String id = "";

    public Team() {

    }

    public String getName() {
        return name;
    }

    public int getRootFolderId() {
        return rootFolderId;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        if (name == null || name.isEmpty()) {
            return id;
        }
        return name;
    }
}
