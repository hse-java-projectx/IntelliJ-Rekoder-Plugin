package ru.hse.plugin.ui.listeners;

import com.intellij.openapi.project.Project;
import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.data.Problem;
import ru.hse.plugin.managers.BackendManager;
import ru.hse.plugin.managers.MainWindowManager;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class ProblemClickListener implements TreeSelectionListener {
    private final Project project;

    public ProblemClickListener(Project project) {
        this.project = project;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        Object component = e.getPath().getLastPathComponent();
        if (!(component instanceof Problem)) {
            return;
        }
        Problem problem = (Problem) component;
        if (!problem.isLoaded()) {
            problem.loadFrom(BackendManager.loadProblem(problem.getName(), Credentials.getInstance()));
        }
        MainWindowManager.updateProblemPanel(project, problem);
    }
}
