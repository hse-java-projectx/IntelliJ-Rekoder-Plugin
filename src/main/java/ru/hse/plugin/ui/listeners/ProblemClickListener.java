package ru.hse.plugin.ui.listeners;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.data.Problem;
import ru.hse.plugin.data.ProblemReference;
import ru.hse.plugin.exceptions.UnauthorizedException;
import ru.hse.plugin.managers.BackendManager;
import ru.hse.plugin.managers.ExplorerManager;
import ru.hse.plugin.utils.NotificationUtils;

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
        if (!(component instanceof ProblemReference)) {
            return;
        }
        ProblemReference problemReference = (ProblemReference) component;
        Problem problem = problemReference.getProblem();
        if (problem == null) return;
        if (problem.isSubmissionsSet()) {
            ExplorerManager explorerManager = new ExplorerManager(project);
            explorerManager.updateProblemPanel(problem);
            return;
        }
        ProgressManager progressManager = new ProgressManagerImpl();
        progressManager.run(new Task.Backgroundable(project, "Loading problem submissions", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                BackendManager backendManager = new BackendManager(Credentials.getInstance());
                try {
                    problem.setSubmissions(backendManager.getProblemSubmissions(problem));
                } catch (UnauthorizedException ex) {
                    NotificationUtils.showAuthorisationFailedNotification(project);
                    return;
                }
                ExplorerManager explorerManager = new ExplorerManager(project);
                explorerManager.updateProblemPanel(problem);
            }
        });
    }
}
