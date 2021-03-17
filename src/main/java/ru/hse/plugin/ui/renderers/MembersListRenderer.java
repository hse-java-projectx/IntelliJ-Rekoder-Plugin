package ru.hse.plugin.ui.renderers;

import com.intellij.ui.ColoredListCellRenderer;
import icons.RekoderIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MembersListRenderer extends ColoredListCellRenderer<Object> {

    @Override
    protected void customizeCellRenderer(@NotNull JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
        append(value.toString());
        setIcon(RekoderIcons.OTHER_USER);
    }
}
