package ru.hse.plugin.managers;

import ru.hse.plugin.data.*;

import javax.annotation.Nullable;
import java.util.*;

public class BackendManager {

    // returns token
    @Nullable
    public static String login(String login, String password) {
        return "token";
    }

    public static List<Team> getTeams(Credentials credentials) {
        List<Team> list = new ArrayList<>();
        Random rand = new Random();
        int steps = rand.nextInt(10) + 1;
        for (int k = 0; k < steps; k++) {
            list.add(new Team("Team " + rand.nextInt()));
        }
        return list;
    }

    public static List<Folder> getRootFolders(Team team, Credentials credentials) {
        List<Folder> list = new ArrayList<>();
        Random rand = new Random();
        int steps = rand.nextInt(10) + 1;
        for (int k = 0; k < steps; k++) {
            list.add(new Folder("Folder" + rand.nextInt()));
        }
        return list;
    }

    public static List<Folder> getPersonalRootFolders(Credentials credentials) {
        List<Folder> list = new ArrayList<>();
        Random rand = new Random();
        int steps = rand.nextInt(10) + 1;
        for (int k = 0; k < steps; k++) {
            list.add(new Folder("Folder" + rand.nextInt()));
        }
        return list;
    }

    public static List<TreeFile> loadFolder(String folderId, Credentials credentials) {
        Random rand = new Random();
        List<TreeFile> list = new ArrayList<>();
        int steps = rand.nextInt(10) + 1;
        for (int k = 0; k < steps; k++) {
            list.add(new Folder("Folder" + rand.nextInt()));
        }
        steps = rand.nextInt(10) + 1;
        for (int k = 0; k < steps; k++) {
            Problem problem = new Problem();
            problem.setName("Problem" + rand.nextInt());
            list.add(problem);
        }
        return list;
    }

    public static Problem loadProblem(String problemId, Credentials credentials) {
        Problem problem = new Problem();
        problem.setName(problemId);
        problem.setCondition(getRandomString(300));
        problem.setSource(getRandomString(15));
        problem.setState(Problem.State.values()[rnd.nextInt(3)]);
        problem.setNumberOfAttempts(rnd.nextInt(1000));
        problem.setTags(Arrays.asList(getRandomString(4), getRandomString(7)));
        return problem;
    }

    public static List<String> getProblemCompilers(Problem problem, Credentials credentials) {
        return Arrays.asList("gcc", "g++", "clang");
    }

    public static List<Submission> getProblemSubmissions(Problem problem, Credentials credentials) {
        // or maybe List<String> where String = id of submission
        return Collections.emptyList();
    }

    private static final Random rnd = new Random();
    private static final String ALL_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static String getRandomString(int len) {
        char[] buffer = new char[len];
        for (int k = 0; k < buffer.length; k++) {
            buffer[k] = ALL_CHARS.charAt(rnd.nextInt(ALL_CHARS.length()));
        }
        return String.valueOf(buffer);
    }
}
