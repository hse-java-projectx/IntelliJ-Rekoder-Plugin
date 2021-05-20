package ru.hse.plugin.data;

import com.google.api.client.util.Key;

import java.util.List;

public class User extends ContentHolder {
    @Key
    private String name = "";
    @Key("teamIds")
    private List<String> teams;
    @Key
    private int rootFolderId = -1;

    public User() {

    }

    public User(String name, List<String> teams) {
        this.name = name;
        this.teams = teams;
    }

    public String getName() {
        return name;
    }

    public List<String> getTeams() {
        return teams;
    }

    public int getRootFolderId() {
        return rootFolderId;
    }

    @Override
    public String toString() {
        return name;
    }
}
