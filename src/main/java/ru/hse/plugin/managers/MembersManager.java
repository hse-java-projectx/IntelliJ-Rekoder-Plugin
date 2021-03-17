package ru.hse.plugin.managers;

import ru.hse.plugin.data.Team;
import ru.hse.plugin.data.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MembersManager {
    public static List<Object> getMembers() {
        List<Object> list = new ArrayList<>();
        Random rand = new Random();
        int steps = rand.nextInt(20) + 1;
        for (int k = 0; k < steps; k++) {
            list.add(new User("Member " + rand.nextInt()));
        }
        return list;
    }
}
