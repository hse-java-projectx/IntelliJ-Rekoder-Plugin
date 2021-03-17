package ru.hse.plugin.managers;

import ru.hse.plugin.data.Folder;
import ru.hse.plugin.data.Problem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProblemsTreeManager {
    public static List<Folder> getRootFolders() {
        List<Folder> list = new ArrayList<>();
        Random rand = new Random();
        int steps = rand.nextInt(10) + 1;
        for (int k = 0; k < steps; k++) {
            list.add(new Folder("Folder" + rand.nextInt()));
        }
        return list;
    }

    public static void loadFolder(Folder folder) {
        Random rand = new Random();
        int steps = rand.nextInt(10) + 1;
        for (int k = 0; k < steps; k++) {
            folder.add(new Folder("Folder" + rand.nextInt()));
        }
        steps = rand.nextInt(10) + 1;
        for (int k = 0; k < steps; k++) {
            folder.add(new Problem("Problem" + rand.nextInt()));
        }
    }
}
