package ru.hse.plugin.data;

public class TestImpl implements Test {
    private String input;
    private String expectedOutput;
    private String actualOutput;
    private Status status;

    public TestImpl(String input, String expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.status = Status.NOT_TESTED;
    }

    @Override
    public String getInput() {
        return input;
    }

    @Override
    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public String getExpectedOutput() {
        return expectedOutput;
    }

    @Override
    public void setExpectedOutput(String output) {
        this.expectedOutput = output;
    }

    @Override
    public String getActualOutput() {
        return actualOutput;
    }

    @Override
    public void setActualOutput(String actualOutput) {
        this.actualOutput = actualOutput;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }
}
