package ru.hse.plugin.ui.renderers;

import com.intellij.ui.ColoredListCellRenderer;
import icons.RekoderIcons;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Team;
import ru.hse.plugin.data.User;

import javax.swing.*;

public class TeamsListRenderer extends ColoredListCellRenderer<Object> {

    @Override
    protected void customizeCellRenderer(@NotNull JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
        append(value.toString());
        if (value instanceof User) {
            setIcon(RekoderIcons.USER);
        } else if (value instanceof Team) {
            setIcon(RekoderIcons.TEAM);
        }
    }
}
