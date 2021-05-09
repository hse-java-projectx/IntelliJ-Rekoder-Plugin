package ru.hse.plugin.data;

import com.intellij.ui.JBColor;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class Problem extends TreeFile {
    protected String name = "";
    protected String statement = "";
    protected State state = State.NOT_STARTED;
    protected int numberOfAttempts = 0;
    protected String source = "";
    protected List<String> tags = Collections.emptyList();
    protected List<Submission> submissions = Collections.emptyList();
    protected boolean isLoaded = false;

    public Problem() {

    }
    public Problem(String name, String statement, State state, int numberOfAttempts, String source, List<String> tags, List<Submission> submissions) {
        this.name = name;
        this.statement = statement;
        this.state = state;
        this.numberOfAttempts = numberOfAttempts;
        this.source = source;
        this.tags = tags;
        this.submissions = submissions;
    }

    public void loadFrom(Problem other) {
        isLoaded = true;
        name = other.name;
        statement = other.statement;
        state = other.state;
        numberOfAttempts = other.numberOfAttempts;
        source = other.source;
        tags = other.tags;
        submissions = other.submissions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
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

    public boolean isLoaded() {
        return isLoaded;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    @Override
    public String toString() {
        return name;
    }

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
}
