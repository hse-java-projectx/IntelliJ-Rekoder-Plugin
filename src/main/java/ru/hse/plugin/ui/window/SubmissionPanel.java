package ru.hse.plugin.ui.window;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import com.intellij.util.ui.HtmlPanel;
import com.intellij.util.ui.JBUI;
import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.data.Problem;
import ru.hse.plugin.data.Submission;
import ru.hse.plugin.managers.BackendManager;
import ru.hse.plugin.ui.listeners.TestListener;
import ru.hse.plugin.utils.ComponentUtils;
import ru.hse.plugin.utils.NotificationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class SubmissionPanel extends JPanel {
    private final HtmlPanel problemName = new SimpleHtmlPanel();
    private final ComboBox<Submission> submissions = new ComboBox<>();
    private final JCEFHtmlPanel problemStatement = new RekoderHtmlPanel(JBCefApp.getInstance().createClient(), null);
    private final HtmlPanel author = new SimpleHtmlPanel();
    private final ComboBox<String> languages = new ComboBox<>();
    private final JLabel verdict = new JLabel();
    private final HtmlPanel timeConsumed = new SimpleHtmlPanel();
    private final HtmlPanel memoryConsumed = new SimpleHtmlPanel();
    //    private final ComboBox<String> files = new ComboBox<>();
    private final JButton testAndSubmit = new JButton("Test and Submit");
    private final JButton test = new JButton("Test");
    private final JButton reloadSubmission = new JButton("Reload Submission");

    private final Project project;
    private final ToolWindow toolWindow;
    private final TestsPanel testsPanel;
    private Submission submission;
    private Problem problem;

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

    public void setProblem(Problem problem) {
        this.problem = problem;
        submissions.setModel(new DefaultComboBoxModel<>());
        Submission newSubmission = new Submission();
        newSubmission.setName("new");
        newSubmission.setAuthor(Credentials.getInstance().getLogin());
        if (!problem.getSubmissions().isEmpty()) {
            Submission lastSubmission = problem.getSubmissions().get(0);
            newSubmission.setSourceCode(lastSubmission.getSourceCode());
            newSubmission.setCompiler(lastSubmission.getCompiler());
        }
        setSubmission(newSubmission);
        submissions.addItem(newSubmission);
        problem.getSubmissions().forEach(submissions::addItem);
        problemStatement.setHtml(problem.getStatement());
        problemName.setBody(problem.getName());
        testsPanel.setTests(problem.getTests());
    }

    private void setSubmission(Submission submission) {
        this.submission = submission;
        author.setBody(submission.getAuthor());
        verdict.setText(submission.getVerdict()); // TODO: должен быть enum и нужно красить в цвет
        timeConsumed.setBody(submission.getTimeConsumed());
        memoryConsumed.setBody(submission.getMemoryConsumed());
        testAndSubmit.setEnabled(!submission.isSent());
        test.setEnabled(true);
        reloadSubmission.setEnabled(submission.isSent());
        if (submission.getSourceCode().isEmpty()) {
            return;
        }
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            WriteAction.run(() -> {
                CommandProcessor.getInstance().executeCommand(project, () -> {
                    Document document = editor.getDocument();
                    document.setText(submission.getSourceCode());
                }, "Change File Text", this.getClass());
            });

        } else {
            StringSelection selection = new StringSelection(submission.getSourceCode());
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
//        ComponentUtils.clearComponent(files);
        testAndSubmit.setEnabled(false);
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
            Submission newSubmission = (Submission) e.getItem();
            if (!this.submission.isSent()) {
                ReadAction.run(() -> {
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    if (editor == null) {
                        return;
                    }
                    CommandProcessor.getInstance().executeCommand(project, () -> {
                        Document document = editor.getDocument();
                        this.submission.setSourceCode(document.getText());
                    }, "Save File Text", this.getClass());
                });
            }
            ApplicationManager.getApplication().runWriteAction(() -> {
                setSubmission(newSubmission);
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
        Disposer.register(project, problemStatement);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.5;
        c.gridwidth = 4;
        c.insets = JBUI.insets(5);
        add(new JBScrollPane(problemStatement.getComponent()), c);
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
//        for (int k = 0; k < 100; k++) {
//            languages.addItem("Language " + k);
//        }
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
    }

    private JPanel setupButtonsPanel() {
        testAndSubmit.setEnabled(false);
        test.setEnabled(false);
        reloadSubmission.setEnabled(false);

        test.addActionListener(new TestListener(project));

        reloadSubmission.addActionListener(a -> {
            Submission newVersion = new BackendManager(Credentials.getInstance()).loadSubmission(submission.getName());
            ApplicationManager.getApplication().runWriteAction(() -> {
                setSubmission(newVersion);
            });
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(testAndSubmit);
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
}
