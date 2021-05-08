package ru.hse.plugin.ui.window;

import com.intellij.execution.*;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.HtmlPanel;
import com.intellij.util.ui.JBUI;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Credentials;
import ru.hse.plugin.data.Problem;
import ru.hse.plugin.data.Submission;
import ru.hse.plugin.data.Test;
import ru.hse.plugin.executors.DefaultExecutor;
import ru.hse.plugin.managers.BackendManager;
import ru.hse.plugin.managers.ProblemManager;
import ru.hse.plugin.utils.ComponentUtils;
import ru.hse.plugin.utils.NotificationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Optional;

public class SubmissionPanel extends JPanel {
    private final HtmlPanel problemName = new SimpleHtmlPanel();
    private final ComboBox<Submission> submissions = new ComboBox<>();
    private final JTextArea problemCondition = new JTextArea();
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

        setupProblemCondition(c);
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
        submissions.setModel(new DefaultComboBoxModel<>());
        Submission newSubmission = new Submission();
        newSubmission.setName("new");
        newSubmission.setAuthor(Credentials.getInstance().getLogin());
        if (!problem.getSubmissions().isEmpty()) {
            Submission lastSubmission = problem.getSubmissions().get(0);
            newSubmission.setTests(lastSubmission.getTests());
            newSubmission.setSourceCode(lastSubmission.getSourceCode());
            newSubmission.setCompiler(lastSubmission.getCompiler());
        }
        setSubmission(newSubmission);
        submissions.addItem(newSubmission);
        problem.getSubmissions().forEach(submissions::addItem);
        problemCondition.setText(problem.getCondition());
        problemName.setBody(problem.getName());
    }

    private void setSubmission(Submission submission) {
        this.submission = submission;
        author.setBody(submission.getAuthor());
        verdict.setText(submission.getVerdict()); // TODO: должен быть enum и нужно красить в цвет
        timeConsumed.setBody(submission.getTimeConsumed());
        memoryConsumed.setBody(submission.getMemoryConsumed());
        testsPanel.setTests(submission.getTests(), !submission.isSent());
        testAndSubmit.setEnabled(!submission.isSent());
        test.setEnabled(true);
        reloadSubmission.setEnabled(submission.isSent());
        if (submission.getSourceCode().isEmpty()) {
            return;
        }
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            Document document = editor.getDocument();
            document.setText(submission.getSourceCode());
        } else {
            StringSelection selection = new StringSelection(submission.getSourceCode());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            NotificationUtils.showToolWindowMessage("Code is in clipboard", NotificationType.INFORMATION, project);
        }
    }

    public void clearProblem() {
        ComponentUtils.clearComponent(problemName);
        ComponentUtils.clearComponent(problemCondition);
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
            Submission submission = (Submission) e.getItem();
            if (!this.submission.isSent()) {
                this.submission.setTests(testsPanel.getTests());
            }
            setSubmission(submission);
        });
        c.gridx = 3;
        c.weightx = 0.0;
        c.gridy = 0;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.LINE_END;
//        c.insets = JBUI.insets(5, 5, 5, 25);
        add(submissions, c);
    }

    private void setupProblemCondition(GridBagConstraints c) {
        problemCondition.setLineWrap(true);
        problemCondition.setWrapStyleWord(true);
        problemCondition.setEditable(false);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.5;
        c.gridwidth = 4;
        c.insets = JBUI.insets(5);
        add(new JBScrollPane(problemCondition), c);
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

        test.addActionListener(a -> {
            java.util.List<Test> list = ProblemManager.getTests(project);
            if (list.isEmpty()) return;
            Test test = list.get(0);


            DefaultExecutor runExecutor = new DefaultExecutor();
            ProgressManager progressManager = new ProgressManagerImpl();
            progressManager.run(new Task.Backgroundable(project, "Testing", true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    ApplicationManager.getApplication().executeOnPooledThread(() -> showFakeProgress(indicator));
                    Optional<String> result = runExecutor.execute(project, indicator, test.getInput());
                    if (result.isPresent()) {
                        System.out.println("Expected: " + test.getOutput());
                        System.out.println("Actual: " + result.get());
                    } else {
                        System.out.println("Failed to execute");
                    }
                }
            });
//            RunManager.getInstance(project).getAllConfigurationsList().forEach(System.out::println);
//            RunnerAndConfigurationSettings settings = RunManager.getInstance(project).getSelectedConfiguration();
//            new ConfigurationContext()
//            new ProgressIndicator();
//            DumbService.getInstance(project).runReadActionInSmartMode();
//            if (settings != null) {
////                ExecutionListener
////                System.out.println(RunManager.getInstance(project).getSelectedConfiguration().getConfiguration());
//                RunConfiguration runConfiguration = settings.getConfiguration();
//                Element input = new Element("Input");
//                input.setText("Abc");
//                runConfiguration.readExternal(input);
//                Element output = new Element("Output");
//                runConfiguration.writeExternal(output);
//                ProgramRunner<RunnerSettings> runner = ProgramRunner.getRunner(DefaultRunExecutor.EXECUTOR_ID, settings.getConfiguration());
//
//                try {
//                    runner.execute(new ExecutionEnvironment(DefaultRunExecutor.getRunExecutorInstance(), runner, settings, project));
//                    System.out.println("Output: " + output.getText());
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println(RunManager.getInstance(project).getSelectedConfiguration().getName());
//            ProgramRunner.getRunner();
        });

        reloadSubmission.addActionListener(a -> {
            Submission newVersion = BackendManager.loadSubmission(submission.getName(), Credentials.getInstance());
            setSubmission(newVersion);
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(testAndSubmit);
        buttonsPanel.add(test);
        buttonsPanel.add(reloadSubmission);
        return buttonsPanel;
    }

    private void showFakeProgress(ProgressIndicator indicator) {
        indicator.setIndeterminate(false);
        indicator.setFraction(0.01);
        try {
            while (indicator.isRunning()) {
                Thread.sleep(1000);
                double fraction = indicator.getFraction();
                indicator.setFraction(fraction + (1 - fraction) * 0.2);
            }
        }
        catch (InterruptedException ignore) {
        }
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
