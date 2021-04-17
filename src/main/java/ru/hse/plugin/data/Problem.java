package ru.hse.plugin.data;

import com.intellij.ui.JBColor;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class Problem extends DefaultMutableTreeNode {
    public enum State {
        PASSED {
            @Override
            public String toString() {
                return "Passed";
            }

            @Override
            public Color getColor() {
                return JBColor.GREEN;
            }
        },
        NOT_STARTED {
            @Override
            public String toString() {
                return "Not started";
            }

            @Override
            public Color getColor() {
                return JBColor.YELLOW;
            }
        },
        NOT_PASSED {
            @Override
            public String toString() {
                return "Not passed";
            }

            @Override
            public Color getColor() {
                return JBColor.RED;
            }
        };

        public abstract Color getColor();
    }

    protected String name = "";
    protected String condition = "";
    protected State state = State.NOT_STARTED;
    protected int numberOfAttempts = 0;
    protected String source = "";
    protected List<String> tags = Collections.emptyList();

    public Problem() {

    }
    public Problem(String name, String condition, State state, int numberOfAttempts, String source, List<String> tags) {
        this.name = name;
        this.condition = condition;
        this.state = state;
        this.numberOfAttempts = numberOfAttempts;
        this.source = source;
        this.tags = tags;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public void setNumberOfAttempts(int numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
