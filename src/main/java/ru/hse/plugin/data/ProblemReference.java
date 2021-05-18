package ru.hse.plugin.data;

import icons.RekoderIcons;

import javax.swing.*;

public class ProblemReference extends TreeFile {
    private final int id;

    public ProblemReference(Problem parent) {
        this.id = parent.getId();
    }

    public Problem getProblem() {
        return ProblemPool.getInstance().getProblem(id);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        Problem problem = getProblem();
        if (problem == null) {
            return null;
        }
        return problem.toString();
    }

    @Override
    public Icon getIcon() {
        return RekoderIcons.PROBLEM;
    }
}
