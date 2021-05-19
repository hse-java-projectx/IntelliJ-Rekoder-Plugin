package ru.hse.plugin.data;

public class Command {
    private String problemOwner;
    private String commandText;
    private boolean isEnabled;

    public Command() {
        problemOwner = "";
        commandText = "";
        isEnabled = false;
    }

    public Command(String problemOwner, String commandText, boolean isEnabled) {
        this.problemOwner = problemOwner;
        this.commandText = commandText;
        this.isEnabled = isEnabled;
    }

    public String getProblemOwner() {
        return problemOwner;
    }

    public void setProblemOwner(String problemOwner) {
        this.problemOwner = problemOwner;
    }

    public String getCommandText() {
        return commandText;
    }

    public void setCommandText(String commandText) {
        this.commandText = commandText;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
