package ru.hse.plugin.data;

import javax.swing.tree.TreeModel;

public class ContentHolder {
    private TreeModel problemsModel;

    public TreeModel getProblemsModel() {
        return problemsModel;
    }

    public void setProblemsModel(TreeModel problemsModel) {
        this.problemsModel = problemsModel;
    }
}
