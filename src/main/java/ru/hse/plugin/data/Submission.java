package ru.hse.plugin.data;

import com.google.api.client.util.Key;

import java.util.Collections;
import java.util.List;

public class Submission {
    @Key
    private int id = -1;
    private String author = "";
    @Key
    private String sourceCode = "";
    @Key
    private String compiler = "";
    private String verdict = "";
    private String timeConsumed = "";
    private String memoryConsumed = "";
    private boolean isSent = true;
    private String order = "-1";

    public void loadFrom(Submission other) {
        this.id = other.id;
        this.author = other.author;
        this.sourceCode = other.sourceCode;
        this.compiler = other.compiler;
        this.verdict = other.verdict;
        this.timeConsumed = other.timeConsumed;
        this.memoryConsumed = other.memoryConsumed;
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

    public String getId() {
        return String.valueOf(id);
    }


    public void setOrder(int order) {
        this.order = String.valueOf(order);
    }

    @Override
    public String toString() {
        return order;
    }
}
