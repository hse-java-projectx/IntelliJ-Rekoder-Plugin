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
        problem.setStatement("Diamond Miner is a game that is similar to Gold Miner, but there are $$$n$$$ miners instead of $$$1$$$ in this game. The mining area can be described as a plane. The $$$n$$$ miners can be regarded as $$$n$$$ points on the y-axis. There are $$$n$$$ diamond mines in the mining area. We can regard them as $$$n$$$ points on the x-axis. For some reason, no miners or diamond mines can be at the origin (point $$$(0, 0)$$$). Every miner should mine exactly one diamond mine. Every miner has a hook, which can be used to mine a diamond mine. If a miner at the point $$$(a,b)$$$ uses his hook to mine a diamond mine at the point $$$(c,d)$$$, he will spend $$$\\sqrt{(a-c)^2+(b-d)^2}$$$ energy to mine it (the distance between these points). The miners can't move or help each other. The object of this game is to minimize the sum of the energy that miners spend. Can you find this minimum?");
        problem.setSource(getRandomString(15));
        problem.setState(Problem.State.values()[rnd.nextInt(3)]);
        problem.setNumberOfAttempts(rnd.nextInt(1000));
        problem.setTags(Arrays.asList(getRandomString(4), getRandomString(7)));
        if (rnd.nextBoolean()) {
            problem.setSubmissions(Collections.emptyList());
        } else {
            problem.setSubmissions(Collections.singletonList(loadSubmission("", credentials)));
        }
        return problem;
    }

    public static Submission loadSubmission(String submissionId, Credentials credentials) {
        Submission submission = new Submission();
        submission.setSourceCode("public class Main {\n" +
                "    public static void main(String[]args){\n" +
                "        \n" +
                "    }\n" +
                "}");
        submission.setAuthor(credentials.getLogin());
        submission.setName("1");
        submission.setCompiler("gcc");
        submission.setVerdict("WA");
        submission.setMemoryConsumed("100");
        submission.setTimeConsumed("3");
        submission.setTests(Arrays.asList(new TestImpl("abc", "cba"), new TestImpl("aac", "caa")));
        submission.setSent(true);
        return submission;
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
