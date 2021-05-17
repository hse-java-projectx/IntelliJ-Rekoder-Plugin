package ru.hse.plugin.data;

import com.google.api.client.util.Key;
import com.intellij.ui.JBColor;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class Problem extends TreeFile {
    @Key
    protected int id;
    @Key
    protected String name = "";
    @Key
    protected String statement = "";
    protected State state = State.NOT_STARTED;
    protected String source = "";
    @Key
    protected List<String> tags = Collections.emptyList();
    @Key
    protected List<Submission> submissions = Collections.emptyList();
    @Key
    private List<TestImpl> tests = Collections.emptyList();

    protected boolean isLoaded = false;

    public Problem() {

    }

    public Problem(String name, String statement, State state, int numberOfAttempts, int id, String source, List<String> tags, List<Submission> submissions) {
        this.name = name;
        this.statement = statement;
        this.state = state;
        this.id = id;
        this.source = source;
        this.tags = tags;
        this.submissions = submissions;
    }

    public void loadFrom(Problem other) {
        isLoaded = true;
        name = other.name;
        statement = other.statement;
        state = other.state;
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
        return submissions.size();
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

    public void setLoaded() {
        isLoaded = true;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public List<TestImpl> getTests() {
        return tests;
    }

    public void setTests(List<TestImpl> tests) {
        this.tests = tests;
    }

    public int getId() {
        return id;
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
