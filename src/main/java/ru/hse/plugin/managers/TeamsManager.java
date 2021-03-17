package ru.hse.plugin.managers;

import ru.hse.plugin.data.Folder;
import ru.hse.plugin.data.Team;
import ru.hse.plugin.data.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeamsManager {
    public static List<Object> getTeams() {
        List<Object> list = new ArrayList<>();
        list.add(new User("Personal"));
        Random rand = new Random();
        int steps = rand.nextInt(10) + 1;
        for (int k = 0; k < steps; k++) {
            list.add(new Team("Team " + rand.nextInt()));
        }
        return list;
    }
}
