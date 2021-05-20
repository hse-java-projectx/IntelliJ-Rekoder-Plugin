package ru.hse.plugin.ui.window;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.HtmlPanel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Commands;
import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.data.Problem;
import ru.hse.plugin.data.Submission;
import ru.hse.plugin.exceptions.UnauthorizedException;
import ru.hse.plugin.executors.CommandsExecutor;
import ru.hse.plugin.managers.BackendManager;
import ru.hse.plugin.managers.ProblemManager;
import ru.hse.plugin.ui.listeners.TestListener;
import ru.hse.plugin.utils.CompilerUtils;
import ru.hse.plugin.utils.ComponentUtils;
import ru.hse.plugin.utils.NotificationUtils;
import ru.hse.plugin.utils.ThreadUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.concurrent.atomic.AtomicReference;

public class SubmissionPanel extends JPanel {
    private final HtmlPanel problemName = new SimpleHtmlPanel();
    private final ComboBox<Submission> submissions = new ComboBox<>();
    private final HtmlPanel problemStatement = new SimpleHtmlPanel();
    private final HtmlPanel author = new SimpleHtmlPanel();
    private final ComboBox<String> languages = new ComboBox<>();
    private final JLabel verdict = new JLabel();
    private final HtmlPanel timeConsumed = new SimpleHtmlPanel();
    private final HtmlPanel memoryConsumed = new SimpleHtmlPanel();
    //    private final ComboBox<String> files = new ComboBox<>();
    private final JButton submit = new JButton("Submit");
    private final JButton test = new JButton("Test");
    private final JButton reloadSubmission = new JButton("Reload Submission");
    private final JCheckBox testBeforeSubmit = new JCheckBox("Test before submit");

    private final Project project;
    private final ToolWindow toolWindow;
    private final TestsPanel testsPanel;
    private Submission currentSubmission;
    private Problem currentProblem;

    public SubmissionPanel(Project project, ToolWindow toolWindow, TestsPanel testsPanel) {
        super(new GridBagLayout());
        this.project = project;
        this.toolWindow = toolWindow;
        this.testsPanel = testsPanel;
        GridBagConstraints c = new GridBagConstraints();
        setupProblemName(c);
        setupSubmissions(c);
        setupAuthor(c);
        setupLanguages(c);
        setupVerdict(c);
        setupTimeConsumed(c);
        setupMemoryConsumed(c);
//        setupFile(c);
        setupButtons(c);

        setupProblemStatement(c);
    }


    public void setCurrentProblem(Problem newProblem) {
        this.currentProblem = newProblem;
        submissions.setModel(new DefaultComboBoxModel<>());
        Submission newSubmission = new Submission();
        newSubmission.setSent(false);
        newSubmission.setOrder("new");
        newSubmission.setAuthor(Credentials.getInstance().getLogin());
        if (!newProblem.getSubmissions().isEmpty()) {
            Submission lastSubmission = newProblem.getSubmissions().get(newProblem.getSubmissions().size() - 1);
            newSubmission.setSourceCode(lastSubmission.getSourceCode());
            newSubmission.setCompiler(lastSubmission.getCompiler());
        }
        setCurrentSubmission(newSubmission);
        submissions.addItem(newSubmission);
        java.util.List<Submission> submissionList = newProblem.getSubmissions();
        for (int k = submissionList.size() - 1; k >= 0; k--) {
            Submission submission = submissionList.get(k);
            submission.setOrder(k + 1);
            submissions.addItem(submission);
        }
        problemStatement.setBody(newProblem.getStatement());
        problemName.setBody(newProblem.getName());
        testsPanel.setTests(newProblem.getTests());
    }

    private void setCurrentSubmission(Submission newSubmission) {
        currentSubmission = newSubmission;
        author.setBody(newSubmission.getAuthor());
        verdict.setText(newSubmission.getVerdict()); // TODO: должен быть enum и нужно красить в цвет
        timeConsumed.setBody(newSubmission.getTimeConsumed());
        memoryConsumed.setBody(newSubmission.getMemoryConsumed());
        submit.setEnabled(!newSubmission.isSent());
        test.setEnabled(true);
        reloadSubmission.setEnabled(newSubmission.isSent());
        testBeforeSubmit.setEnabled(!newSubmission.isSent());

        languages.removeAllItems();
        if (newSubmission.isSent()) {
            languages.addItem(newSubmission.getCompiler());
            languages.setEnabled(false);
        } else {
            for (String language : CompilerUtils.getAllLanguages()) {
                languages.addItem(language);
            }
            languages.setEnabled(true);
            if (newSubmission.getCompiler() != null && !newSubmission.getCompiler().isEmpty()) {
                languages.setItem(newSubmission.getCompiler());
//                languages.setSelectedItem(newSubmission.getCompiler());
            }
        }
        if (newSubmission.getSourceCode().isEmpty()) {
            return;
        }
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            WriteAction.run(() -> {
                CommandProcessor.getInstance().executeCommand(project, () -> {
                    Document document = editor.getDocument();
                    document.setText(newSubmission.getSourceCode());
                }, "Change File Text", this.getClass());
            });

        } else {
            StringSelection selection = new StringSelection(newSubmission.getSourceCode());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            NotificationUtils.showToolWindowMessage("Code is in clipboard", NotificationType.INFORMATION, project);
        }
    }

    public void clearProblem() {
        ComponentUtils.clearComponent(problemName);
        ComponentUtils.clearComponent(problemStatement);
        ComponentUtils.clearComponent(author);
        ComponentUtils.clearComponent(verdict);
        ComponentUtils.clearComponent(timeConsumed);
        ComponentUtils.clearComponent(memoryConsumed);
        ComponentUtils.clearComponent(submissions);
        ComponentUtils.clearComponent(languages);
        submit.setEnabled(false);
        test.setEnabled(false);
        reloadSubmission.setEnabled(false);
    }

    private void setupProblemName(GridBagConstraints c) {
        setupLabel(0, 0, "Problem name:", c);
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.0;
        c.weightx = 1.0;
        c.insets = JBUI.insets(5);
        add(problemName, c);
    }

    private void setupSubmissions(GridBagConstraints c) {
        setupLabel(2, 0, "Submissions:", c);
        submissions.setModel(new DefaultComboBoxModel<>());
        submissions.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }
            Submission newSubmission = (Submission) e.getItem();
            if (!this.currentSubmission.isSent()) {
                ReadAction.run(() -> {
                    currentSubmission.setCompiler(languages.getItem());
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    if (editor == null) {
                        return;
                    }
                    CommandProcessor.getInstance().executeCommand(project, () -> {
                        Document document = editor.getDocument();
                        this.currentSubmission.setSourceCode(document.getText());
                    }, "Save File Text", this.getClass());
                });
            }
            ThreadUtils.runWriteAction(() -> {
                setCurrentSubmission(newSubmission);
            });
        });
        c.gridx = 3;
        c.weightx = 0.0;
        c.gridy = 0;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.LINE_END;
//        c.insets = JBUI.insets(5, 5, 5, 25);
        add(submissions, c);
    }

    private void setupProblemStatement(GridBagConstraints c) {
//        Disposer.register(project, problemStatement);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.5;
        c.gridwidth = 4;
        c.insets = JBUI.insets(5);
        add(new JBScrollPane(problemStatement), c);
    }

    private void setupAuthor(GridBagConstraints c) {
        setupLabel(0, 2, "Author:", c);
        c.gridx = 1;
        c.gridy = 2;
        c.weighty = 0.0;
        c.weightx = 1.0;
        c.insets = JBUI.insets(5);
        add(author, c);
    }

    private void setupLanguages(GridBagConstraints c) {
        setupLabel(2, 2, "Language:", c);
        MutableComboBoxModel<String> model = new DefaultComboBoxModel<>();
        languages.setModel(model);
        languages.setEnabled(false);
        c.gridx = 3;
        c.weightx = 0.0;
        c.gridy = 2;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.LINE_END;
//        c.insets = JBUI.insets(5, 5, 5, 25);
        add(languages, c);
    }

    private void setupVerdict(GridBagConstraints c) {
        setupLabel(0, 3, "Verdict:", c);
        c.gridx = 1;
        c.gridy = 3;
        c.weighty = 0.0;
        c.weightx = 1.0;
        c.insets = JBUI.insets(5);
        add(verdict, c);
    }

    private void setupTimeConsumed(GridBagConstraints c) {
        setupLabel(0, 4, "Time:", c);
        c.gridx = 1;
        c.gridy = 4;
        c.weighty = 0.0;
        c.weightx = 1.0;
        c.insets = JBUI.insets(5);
        add(timeConsumed, c);
    }

    private void setupMemoryConsumed(GridBagConstraints c) {
        setupLabel(0, 5, "Memory:", c);
        c.gridx = 1;
        c.gridy = 5;
        c.weighty = 0.0;
        c.weightx = 1.0;
        c.insets = JBUI.insets(5);
        add(memoryConsumed, c);
    }

    private void setupButtons(GridBagConstraints c) {
        JPanel buttonsPanel = setupButtonsPanel();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 6;
        c.weighty = 0.0;
        c.weightx = 1.0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        add(buttonsPanel, c);

        testBeforeSubmit.setEnabled(false);
        testBeforeSubmit.setSelected(true);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 6;
        c.weighty = 0.0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.insets = JBUI.insets(5);
        c.anchor = GridBagConstraints.LAST_LINE_END;
        add(testBeforeSubmit, c);
    }

    private JPanel setupButtonsPanel() {
        submit.setEnabled(false);
        test.setEnabled(false);
        reloadSubmission.setEnabled(false);

        submit.addActionListener(new SubmitListener());
        test.addActionListener(new TestListener(project));
        reloadSubmission.addActionListener(new ReloadSubmissionListener());

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(submit);
        buttonsPanel.add(test);
        buttonsPanel.add(reloadSubmission);
        return buttonsPanel;
    }

//    private void setupFile(GridBagConstraints c) {
//        setupLabel(2, 6, "File:", c);
//        MutableComboBoxModel<String> model = new DefaultComboBoxModel<>();
//        files.setModel(model);
////        files.addItem("main.cpp");
//        c.gridx = 3;
//        c.weightx = 0.0;
//        c.gridy = 6;
//        c.weighty = 0.0;
//        c.anchor = GridBagConstraints.LINE_END;
////        c.insets = JBUI.insets(5, 5, 5, 25);
//        add(files, c);
//    }

    private void setupLabel(int x, int y, String name, GridBagConstraints c) {
        JLabel label = new JLabel(name);
        if (x == 0) {
            label.setHorizontalAlignment(SwingConstants.LEFT);
            c.anchor = GridBagConstraints.LINE_START;
        } else if (x == 2) {
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = x;
        c.gridy = y;
        c.weighty = 0.0;
        c.weightx = 0.0;
        c.insets = JBUI.insets(5);
        add(label, c);
    }

    private class SubmitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ProgressManager progressManager = new ProgressManagerImpl();
            progressManager.run(new Task.Backgroundable(project, "Submitting", true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    try {
                        Problem problem = currentProblem;
                        Submission submission = currentSubmission;
                        AtomicReference<Editor> editor = new AtomicReference<>();

                        ThreadUtils.runInEdtAndWait(() -> {
                            editor.set(FileEditorManager.getInstance(project).getSelectedTextEditor());
                            if (editor.get() == null) {
                                return;
                            }
                            ReadAction.run(() -> {
                                submission.setCompiler(languages.getItem());
                                CommandProcessor.getInstance().executeCommand(project, () -> {
                                    Document document = editor.get().getDocument();
                                    submission.setSourceCode(document.getText());
                                }, "Get File Text", this.getClass());
                            });
                        });
                        if (editor.get() == null) {
                            NotificationUtils.showToolWindowMessage("Can't get code", NotificationType.ERROR, project);
                            return;
                        }
                        ThreadUtils.runWriteAction(() -> {
                            submissions.setEnabled(false);
                            languages.setEnabled(false);
                        });
                        if (testBeforeSubmit.isSelected()) {
                            ProblemManager problemManager = new ProblemManager(project);
                            if (!problemManager.runTests()) {
                                NotificationUtils.showToolWindowMessage("Tests failed", NotificationType.ERROR, project);
                                return;
                            }
                        }
                        ProblemManager problemManager = new ProblemManager(project);
                        ReadAction.run(() -> {
                            problem.setTests(problemManager.getTests());
                        });
                        BackendManager backendManager = new BackendManager(Credentials.getInstance());
                        try {
                            backendManager.sendSubmission(problem, submission);
                            backendManager.sendProblemState(problem);
                            ThreadUtils.runWriteAction(() -> {
                                submission.setSent(true);
                                problem.getSubmissions().add(submission);
                                setCurrentProblem(problem);
                            });
                            Document document = editor.get().getDocument();
                            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
                            progressManager.run(new Task.Backgroundable(project, "Running commands", true) {
                                @Override
                                public void run(@NotNull ProgressIndicator indicator) {
                                    CommandsExecutor commandsExecutor = new CommandsExecutor(problem, submission, virtualFile, project);
                                    Commands commands = Commands.getInstance();
                                    commands.getCommands().stream().
                                            filter(c -> c.getProblemOwner().isEmpty() || c.getProblemOwner().equals(problem.getSource())).
                                            forEach(c -> commandsExecutor.execute(c.getCommandText()));
                                }
                            });
                        } catch (UnauthorizedException unauthorizedException) {
                            NotificationUtils.showAuthorisationFailedNotification(project);
                        }
                    } finally {
                        ThreadUtils.runWriteAction(() -> {
                            submissions.setEnabled(true);
                            languages.setEnabled(true);
                        });
                    }
                }
            });
        }
    }

    private class ReloadSubmissionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                ThreadUtils.runWriteAction(() -> submissions.setEnabled(false));
                ProgressManager progressManager = new ProgressManagerImpl();
                progressManager.run(new Task.Backgroundable(project, "Load submission", true) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        try {
                            Submission newVersion = new BackendManager(Credentials.getInstance()).loadSubmission(currentSubmission.getId());
                            currentSubmission.loadFrom(newVersion);
                            ThreadUtils.runWriteAction(() -> setCurrentSubmission(currentSubmission));
                        } catch (UnauthorizedException ex) {
                                NotificationUtils.showAuthorisationFailedNotification(project);
                            }
                        }
                });
            } finally {
                ThreadUtils.runWriteAction(() -> submissions.setEnabled(true));
            }
        }
    }
}
