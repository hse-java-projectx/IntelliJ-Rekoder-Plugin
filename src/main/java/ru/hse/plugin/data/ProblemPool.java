package ru.hse.plugin.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProblemPool {
    private final Map<Integer, Problem> problemsById = new ConcurrentHashMap<>();
    private static final ProblemPool problemPool = new ProblemPool();

    private ProblemPool() {

    }

    public static ProblemPool getInstance() {
        return problemPool;
    }

    public void addProblem(Problem problem) {
        problemsById.put(problem.getId(), problem);
    }

    public Problem getProblem(int id) {
        return problemsById.get(id);
    }

    public void clear() {
        problemsById.clear();
    }
}
