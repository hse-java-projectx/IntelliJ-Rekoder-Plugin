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
    @Key
    private String inputFormat = "";
    @Key
    private String outputFormat = "";
    @Key("problemUrl")
    private String source = "";
    @Key
    private List<String> tags = Collections.emptyList();
    @Key
    private List<TestImpl> tests = Collections.emptyList();
    @Key
    private Owner owner;
    @Key
    private Integer originalProblemId = null;

    private int numberOfSuccessfulSubmissions = 0;

    private List<Submission> submissions = Collections.emptyList();
    private boolean submissionsSet = false;

    public Problem() {

    }

    public String getStatement() {
        StringBuilder builder = new StringBuilder();
        builder.append(statement);
        if (inputFormat != null && !inputFormat.isEmpty()) {
            builder.append("\n").append("<h2>Input format</h2>").append("\n");
            builder.append(inputFormat);
        }
        if (outputFormat != null && !outputFormat.isEmpty()) {
            builder.append("\n").append("<h2>Output format</h2>").append("\n");
            builder.append(outputFormat);
        }
        return builder.toString();
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

    public void addSubmission(Submission submission) {
        submissions.add(submission);
        if (submission.isSuccessful()) {
            numberOfSuccessfulSubmissions++;
        }
    }

    public List<TestImpl> getTests() {
        return tests;
    }

    public Integer getOriginalProblemId() {
        return originalProblemId;
    }

    public void setTests(List<Test> tests) {
        this.tests = tests.stream().map(t -> new TestImpl(t.getInput(), t.getExpectedOutput())).collect(Collectors.toList());
    }

    public void setSubmissions(List<Submission> submissions) {
        submissionsSet = true;
        this.submissions = new ArrayList<>(submissions);
        this.numberOfSuccessfulSubmissions = (int) this.submissions.stream().filter(Submission::isSuccessful).count();
    }

    public boolean isSubmissionsSet() {
        return submissionsSet;
    }

    public int getId() {
        return id;
    }

    public String getOwnerId() {
        return owner.id;
    }

    public OwnerType getOwnerType() {
        if (owner.type.equals("USER")) {
            return OwnerType.USER;
        } else {
            return OwnerType.TEAM;
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public enum OwnerType {
        USER,
        TEAM,
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

    public static class Owner {
        @Key
        private String type;
        @Key
        private String id;
    }
}
