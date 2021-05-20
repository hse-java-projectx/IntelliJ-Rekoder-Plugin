package ru.hse.plugin.data;

import com.google.api.client.util.Key;
import com.intellij.ui.JBColor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Problem {
    @Key
    private int id = -1;
    @Key
    private String name = "";
    @Key
    private String statement = "";
    private String source = "";
    @Key
    private List<String> tags = Collections.emptyList();
    @Key
    private List<TestImpl> tests = Collections.emptyList();
    @Key
    private int numberOfSuccessfulSubmissions = 0;

    private List<Submission> submissions = Collections.emptyList();

    public Problem() {

    }

    public String getStatement() {
        return statement;
    }

    public State getState() {
        if (submissions.isEmpty()) {
            return State.NOT_STARTED;
        }
        if (numberOfSuccessfulSubmissions > 0) {
            return State.PASSED;
        }
        return State.NOT_PASSED;
    }

    public int getNumberOfAttempts() {
        return submissions.size();
    }

    public String getSource() {
        return source;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getName() {
        return name;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public List<TestImpl> getTests() {
        return tests;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests.stream().map(t -> new TestImpl(t.getInput(), t.getExpectedOutput())).collect(Collectors.toList());
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = new ArrayList<>(submissions);
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
