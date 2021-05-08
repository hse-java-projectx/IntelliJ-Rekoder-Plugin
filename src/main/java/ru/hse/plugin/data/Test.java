package ru.hse.plugin.data;

import com.intellij.ui.JBColor;

import java.awt.*;

public interface Test {
    String getInput();
    void setInput(String input);
    String getOutput();
    void setOutput(String output);
    Status getStatus();
    void setStatus(Status status);

    enum Status {
        PASSED {
            @Override
            public Color getColor() {
                return JBColor.GREEN;
            }

            @Override
            public String toString() {
                return "Passed";
            }
        },
        NOT_TESTED {
            @Override
            public Color getColor() {
                return JBColor.ORANGE;
            }

            @Override
            public String toString() {
                return "Not tasted";
            }
        },
        FAILED {
            @Override
            public Color getColor() {
                return JBColor.RED;
            }

            @Override
            public String toString() {
                return "Failed";
            }
        },
        ERROR {
            @Override
            public Color getColor() {
                return JBColor.BLACK;
            }

            @Override
            public String toString() {
                return "Error";
            }
        },
        TESTING {
            @Override
            public Color getColor() {
                return JBColor.YELLOW;
            }

            @Override
            public String toString() {
                return "Testing";
            }
        };
        public abstract Color getColor();
    }
}
