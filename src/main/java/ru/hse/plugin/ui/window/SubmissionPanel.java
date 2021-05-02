package ru.hse.plugin.ui.window;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionListener;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.ui.HtmlPanel;
import com.intellij.util.ui.JBUI;
import ru.hse.plugin.data.Submission;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.RunManager;

import javax.swing.*;
import java.awt.*;

public class SubmissionPanel extends JPanel {
    private final HtmlPanel problemName = new SimpleHtmlPanel();
    private final ComboBox<Submission> submissions = new ComboBox<>();
    private final JTextArea problemCondition = new JTextArea();
    private final HtmlPanel author = new SimpleHtmlPanel();
    private final ComboBox<String> languages = new ComboBox<>();
    private final HtmlPanel timeConsumed = new SimpleHtmlPanel();
    private final HtmlPanel memoryConsumed = new SimpleHtmlPanel();
    private final ComboBox<String> files = new ComboBox<>();

    private final Project project;
    private final ToolWindow toolWindow;

    public SubmissionPanel(Project project, ToolWindow toolWindow) {
        super(new GridBagLayout());
        this.project = project;
        this.toolWindow = toolWindow;
        GridBagConstraints c = new GridBagConstraints();
        setupProblemName(c);
        setupSubmissions(c);
        setupAuthor(c);
        setupLanguages(c);
        setupTimeConsumed(c);
        setupMemoryConsumed(c);
        setupFile(c);
        setupButtons(c);

        setupProblemCondition(c);
    }

    public void clearSubmission() {
        problemName.setBody("");
        problemCondition.setText("");
        author.setBody("");
        timeConsumed.setBody("");
        memoryConsumed.setBody("");
        submissions.setModel(new DefaultComboBoxModel<>());
        languages.setModel(new DefaultComboBoxModel<>());
        files.setModel(new DefaultComboBoxModel<>());
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
        MutableComboBoxModel<Submission> model = new DefaultComboBoxModel<>();
        submissions.setModel(model);
//        for (int k = 0; k < 100; k++) {
//            Submission submission = new Submission();
//            submission.setName("Version " + k);
//            submissions.addItem(submission);
//        }
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

    private void setupTimeConsumed(GridBagConstraints c) {
        setupLabel(0, 3, "Time:", c);
        c.gridx = 1;
        c.gridy = 3;
        c.weighty = 0.0;
        c.weightx = 1.0;
        c.insets = JBUI.insets(5);
        add(timeConsumed, c);
    }

    private void setupMemoryConsumed(GridBagConstraints c) {
        setupLabel(0, 4, "Memory:", c);
        c.gridx = 1;
        c.gridy = 4;
        c.weighty = 0.0;
        c.weightx = 1.0;
        c.insets = JBUI.insets(5);
        add(memoryConsumed, c);
    }

    private void setupButtons(GridBagConstraints c) {
        JPanel buttonsPanel = setupButtonsPanel();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        c.weighty = 0.0;
        c.weightx = 1.0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        add(buttonsPanel, c);
    }

    private JPanel setupButtonsPanel() {
        JButton testAndSubmit = new JButton("Test and Submit");
        JButton test = new JButton("Test");

        test.addActionListener(a -> {
            RunManager.getInstance(project).getAllConfigurationsList().forEach(System.out::println);
            RunnerAndConfigurationSettings settings = RunManager.getInstance(project).getSelectedConfiguration();
            if (settings != null) {
//                ExecutionListener
//                System.out.println(RunManager.getInstance(project).getSelectedConfiguration().getConfiguration());
                ProgramRunner<RunnerSettings> runner = ProgramRunner.getRunner(DefaultRunExecutor.EXECUTOR_ID, settings.getConfiguration());
                try {
                    runner.execute(new ExecutionEnvironment(new DefaultRunExecutor(), runner, settings, project));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
//            System.out.println(RunManager.getInstance(project).getSelectedConfiguration().getName());
//            ProgramRunner.getRunner();
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(testAndSubmit);
        buttonsPanel.add(test);
        return buttonsPanel;
    }

    private void setupFile(GridBagConstraints c) {
        setupLabel(2, 5, "File:", c);
        MutableComboBoxModel<String> model = new DefaultComboBoxModel<>();
        files.setModel(model);
//        files.addItem("main.cpp");
        c.gridx = 3;
        c.weightx = 0.0;
        c.gridy = 5;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.LINE_END;
//        c.insets = JBUI.insets(5, 5, 5, 25);
        add(files, c);
    }

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
