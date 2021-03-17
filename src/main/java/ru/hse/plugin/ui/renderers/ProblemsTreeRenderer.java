package ru.hse.plugin.ui.renderers;

import com.intellij.ui.ColoredTreeCellRenderer;
import icons.RekoderIcons;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Folder;
import ru.hse.plugin.data.Problem;

import javax.swing.*;

public class ProblemsTreeRenderer extends ColoredTreeCellRenderer {

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        append(value.toString());
        if (value instanceof Folder) {
            setIcon(RekoderIcons.FOLDER);
        } else if (value instanceof Problem) {
            setIcon(RekoderIcons.PROBLEM);
        }
    }
}
