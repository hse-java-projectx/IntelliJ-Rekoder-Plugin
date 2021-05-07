package ru.hse.plugin.utils;

import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.ui.components.JBList;
import com.intellij.ui.treeStructure.Tree;
import ru.hse.plugin.data.ContentHolder;
import ru.hse.plugin.ui.window.ProblemPanel;
import ru.hse.plugin.ui.window.SubmissionPanel;
import ru.hse.plugin.ui.window.TestsPanel;

public interface DataKeys {
    DataKey<Tree> PROBLEMS_TREE = DataKey.create("PROBLEMS_TREE");
    DataKey<JBList<ContentHolder>> TEAMS_LIST = DataKey.create("TEAMS_LIST");
    DataKey<ProblemPanel> PROBLEM_PANEL = DataKey.create("PROBLEM_PANEL");
    DataKey<SubmissionPanel> SUBMISSION_PANEL = DataKey.create("SUBMISSION_PANEL");
    DataKey<TestsPanel> TESTS_PANEL = DataKey.create("TESTS_PANEL");
}
