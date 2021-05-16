package ru.hse.plugin.data;

import com.google.api.client.util.Key;

import java.util.Collections;
import java.util.List;

public class Submission {
    private String name = "";
    private String author = "";
    @Key
    private String sourceCode = "";
    @Key
    private String compiler = "";
    private String verdict = "";
    private String timeConsumed = "";
    private String memoryConsumed = "";
    private boolean isSent = false;

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    @Override
    public String toString() {
        return name;
    }
}
