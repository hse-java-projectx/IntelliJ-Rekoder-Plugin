package ru.hse.plugin.utils;

import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.ui.components.JBList;
import com.intellij.ui.treeStructure.Tree;

public interface DataKeys {
    DataKey<Tree> PROBLEMS_TREE = DataKey.create("PROBLEMS_TREE");
    DataKey<JBList<Object>> TEAMS_LIST = DataKey.create("TEAMS_LIST");
    DataKey<JBList<Object>> MEMBERS_LIST = DataKey.create("MEMBERS_LIST");
}
