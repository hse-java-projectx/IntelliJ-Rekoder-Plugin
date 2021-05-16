package ru.hse.plugin.data;

import com.google.api.client.util.Key;

import java.util.List;

public class User extends ContentHolder {
    @Key
    private String name;
    @Key("teamIds")
    private List<String> teams;

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

    @Override
    public String toString() {
        return name;
    }
}
