package ru.hse.plugin.data;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(name = "Commands", storages = @Storage("rekoder_commands.xml"))
public class Commands implements PersistentStateComponent<Commands> {
    private List<Command> commands = new ArrayList<>();

    private Commands() {

    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public static Commands getInstance() {
        return ServiceManager.getService(Commands.class);
    }

    @Override
    public @Nullable Commands getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull Commands state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
