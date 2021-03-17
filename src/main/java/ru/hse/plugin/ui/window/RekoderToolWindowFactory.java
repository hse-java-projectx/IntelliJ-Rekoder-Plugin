package ru.hse.plugin.ui.window;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RekoderToolWindowFactory implements ToolWindowFactory {
    private static final String MAIN_WINDOW_ID = "rekoderWindow";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        RekoderMainToolWindow mainToolWindow = new RekoderMainToolWindow();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainToolWindow, "Personal", false);
        toolWindow.getContentManager().addContent(content);
    }

    @NotNull
    public static DataContext getDataContext(@NotNull Project project) {
        ToolWindow rekoderToolWindow = ToolWindowManager.getInstance(project).getToolWindow(MAIN_WINDOW_ID);
        Objects.requireNonNull(rekoderToolWindow);
        Content content = rekoderToolWindow.getContentManager().getContent(0);
        return DataManager.getInstance().getDataContext(content.getComponent());
    }
}
