package ru.hse.plugin.data;

public class TestImpl implements Test {
    private String input;
    private String output;

    public TestImpl(String input, String output) {
        this.input = input;
        this.output = output;
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
    public String getOutput() {
        return output;
    }

    @Override
    public void setOutput(String output) {
        this.output = output;
    }
}
