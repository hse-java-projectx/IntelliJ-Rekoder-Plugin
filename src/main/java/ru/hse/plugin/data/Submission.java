package ru.hse.plugin.data;

public class Submission {
    private String name;
    private String sourceCode;
    private String compiler;
    private String verdict;
    private String timeConsumed;
    private String memoryConsumed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getCompiler() {
        return compiler;
    }

    public void setCompiler(String compiler) {
        this.compiler = compiler;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public String getTimeConsumed() {
        return timeConsumed;
    }

    public void setTimeConsumed(String timeConsumed) {
        this.timeConsumed = timeConsumed;
    }

    public String getMemoryConsumed() {
        return memoryConsumed;
    }

    public void setMemoryConsumed(String memoryConsumed) {
        this.memoryConsumed = memoryConsumed;
    }

    @Override
    public String toString() {
        return name;
    }
}
