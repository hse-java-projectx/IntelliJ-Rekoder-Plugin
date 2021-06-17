package ru.hse.plugin.data;

import com.google.api.client.util.Key;

import java.util.Collections;
import java.util.List;

public class Submission {
    @Key
    private int id = -1;
    @Key("authorId")
    private String author = "";
    @Key
    private String sourceCode = "";
    @Key
    private String compiler = "";
    @Key
    private Feedback feedback = new Feedback();
    private boolean isSent = true;
    private String order = "-1";

    public void loadFrom(Submission other) {
        this.id = other.id;
        this.author = other.author;
        this.sourceCode = other.sourceCode;
        this.compiler = other.compiler;
        this.feedback = other.feedback;
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
        return feedback.verdict;
    }

    public void setVerdict(String verdict) {
        this.feedback.verdict = verdict;
    }

    public String getTimeConsumed() {
        return feedback.timeConsumed;
    }

    public void setTimeConsumed(String timeConsumed) {
        this.feedback.timeConsumed = timeConsumed;
    }

    public String getMemoryConsumed() {
        return feedback.memoryConsumed;
    }

    public void setMemoryConsumed(String memoryConsumed) {
        this.feedback.memoryConsumed = memoryConsumed;
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

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setOrder(int order) {
        this.order = String.valueOf(order);
    }

    public boolean isSuccessful() {
        return feedback.successful;
    }

    @Override
    public String toString() {
        return order;
    }

    public static class Feedback {
        @Key
        private String verdict = "na";
        @Key
        private String comment = "";
        @Key
        private String timeConsumed = "";
        @Key
        private String memoryConsumed = "";
        @Key
        private boolean successful = false;
    }
}
