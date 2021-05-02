package ru.hse.plugin.ui.window;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.ContentManagerListener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RekoderToolWindowFactory implements ToolWindowFactory {
    private static final String MAIN_WINDOW_ID = "rekoderWindow";

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        RekoderMainToolWindow mainToolWindow = new RekoderMainToolWindow(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainToolWindow, "Explorer", false);
        content.setCloseable(false);
        toolWindow.getContentManager().addContent(content);

        RekoderProblemWindow problemToolWindow = new RekoderProblemWindow(project, toolWindow);
        Content content1 = contentFactory.createContent(problemToolWindow, "Problem", false);
        content1.setCloseable(false);
        toolWindow.getContentManager().addContent(content1);
        toolWindow.getContentManager().setSelectedContent(content);
    }

    @NotNull
    public static DataContext getExplorerDataContext(@NotNull Project project) {
        ToolWindow rekoderToolWindow = ToolWindowManager.getInstance(project).getToolWindow(MAIN_WINDOW_ID);
        Objects.requireNonNull(rekoderToolWindow);
        Content content = rekoderToolWindow.getContentManager().getContent(0);
        return DataManager.getInstance().getDataContext(content.getComponent());
    }

    @NotNull
    public static DataContext getProblemDataContext(@NotNull Project project) {
        ToolWindow rekoderToolWindow = ToolWindowManager.getInstance(project).getToolWindow(MAIN_WINDOW_ID);
        Objects.requireNonNull(rekoderToolWindow);
        Content content = rekoderToolWindow.getContentManager().getContent(1);
        return DataManager.getInstance().getDataContext(content.getComponent());
    }
}
