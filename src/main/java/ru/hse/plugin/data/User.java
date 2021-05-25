package ru.hse.plugin.data;

import com.google.api.client.util.Key;

import java.util.List;

public class User extends ContentHolder {
    @Key
    private String name = "";
    @Key
    private int rootFolderId = -1;

    public User() {

    }

    public User(String name) {
        this.name = name;
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
