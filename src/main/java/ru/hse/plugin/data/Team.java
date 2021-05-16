package ru.hse.plugin.data;

import com.google.api.client.util.Key;

public class Team extends ContentHolder {
    @Key
    private final String name;

    public Team(String name) {
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
