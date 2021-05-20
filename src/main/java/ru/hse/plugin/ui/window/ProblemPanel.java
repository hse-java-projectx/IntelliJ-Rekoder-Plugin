package ru.hse.plugin.ui.window;

//import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import com.intellij.util.ui.HtmlPanel;
import com.intellij.util.ui.JBUI;
import ru.hse.plugin.data.Problem;
import ru.hse.plugin.data.Submission;
import ru.hse.plugin.managers.ProblemManager;
import ru.hse.plugin.utils.CompilerUtils;
import ru.hse.plugin.utils.ComponentUtils;
import ru.hse.plugin.utils.ThreadUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

public class ProblemPanel extends JPanel {
    private final HtmlPanel problemName = new SimpleHtmlPanel();
    private final JLabel problemState = new JLabel();
    private final JLabel numberOfAttempts = new JLabel();
    private final HtmlPanel problemStatement = new SimpleHtmlPanel();
    private final HtmlPanel problemSource = new SimpleHtmlPanel();
    private final JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private Problem problem;
    private final JButton startSolving = new JButton("Start solving");
    private final JButton previewCode = new JButton("Preview code");

    private final Project project;
    private final ToolWindow toolWindow;

    public void setProblem(Problem problem) {
        this.problem = problem;
        problemName.setBody(problem.getName());
        problemState.setText(problem.getState().toString());
        problemState.setForeground(problem.getState().getColor());
        numberOfAttempts.setText(String.valueOf(problem.getNumberOfAttempts()));
        problemStatement.setBody(problem.getStatement());
        problemSource.setBody(problem.getSource());
        tagsPanel.removeAll();
        problem.getTags().forEach(t -> tagsPanel.add(new JLabel(t)));
        startSolving.setEnabled(true);
        previewCode.setEnabled(!problem.getSubmissions().isEmpty());
    }

    public void clearProblem() {
        this.problem = null;
        ComponentUtils.clearComponent(problemName);
        ComponentUtils.clearComponent(problemState);
        ComponentUtils.clearComponent(numberOfAttempts);
        ComponentUtils.clearComponent(problemStatement);
        ComponentUtils.clearComponent(problemSource);
        tagsPanel.removeAll();
        startSolving.setEnabled(false);
        previewCode.setEnabled(false);
    }

    public ProblemPanel(Project project, ToolWindow toolWindow) {
        super(new GridBagLayout());
        this.project = project;
        this.toolWindow = toolWindow;
        GridBagConstraints c = new GridBagConstraints();
        setupProblemName(c);
        setupProblemState(c);
        setupNumberOfAttempts(c);
        setupProblemSource(c);
        setupTags(c);
        setupButtons(project, toolWindow, c);
        setupProblemStatement(c);
    }

    private void setupProblemName(GridBagConstraints c) {
        setupLabel(0, 0, "Problem name:", c);
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.0;
        c.insets = JBUI.insets(5);
        add(problemName, c);
    }

    private void setupProblemState(GridBagConstraints c) {
        setupLabel(0, 3, "State:", c);
        c.gridx = 1;
        c.gridy = 3;
        c.weighty = 0.0;
        c.weightx = 1.0;
        c.insets = JBUI.insets(5);
        add(problemState, c);
    }

    private void setupNumberOfAttempts(GridBagConstraints c) {
        setupLabel(2, 3, "Number of attempts:", c);
        numberOfAttempts.setForeground(JBColor.BLUE);
        numberOfAttempts.setHorizontalAlignment(SwingConstants.RIGHT);
        numberOfAttempts.setOpaque(true);
        numberOfAttempts.setBackground(JBColor.WHITE);
        numberOfAttempts.setBorder(JBUI.Borders.empty(5));
        c.gridx = 3;
        c.weightx = 0.0;
        c.gridy = 3;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.insets = JBUI.insets(5, 5, 5, 25);
        add(numberOfAttempts, c);
    }

    private void setupProblemStatement(GridBagConstraints c) {
        setupLabel(0, 1, "Statement:", c);
//        Disposer.register(project, problemStatement);
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0.5;
        c.gridwidth = 4;
        c.insets = JBUI.insets(0, 5, 5, 5);
        add(new JBScrollPane(problemStatement), c);
    }

    private void setupProblemSource(GridBagConstraints c) {
        setupLabel(0, 4, "Source:", c);
        c.gridx = 1;
        c.gridy = 4;
        c.weighty = 0.0;
        c.insets = JBUI.insets(5);
        add(problemSource, c);
    }

    private void setupTags(GridBagConstraints c) {
        setupLabel(0, 5, "Tags:", c);
        c.gridx = 1;
        c.gridy = 5;
        c.weighty = 0.0;
        c.insets = JBUI.insets(5);
        add(tagsPanel, c);
    }

    private void setupButtons(Project project, ToolWindow toolWindow, GridBagConstraints c) {
        JPanel buttonsPanel = setupButtonsPanel(project, toolWindow);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 6;
        c.weighty = 0.0;
        c.gridwidth = 4;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        add(buttonsPanel, c);
    }

    private JPanel setupButtonsPanel(Project project, ToolWindow toolWindow) {
        // Buttons
        startSolving.addActionListener(a -> {
            ThreadUtils.runWriteAction(() -> {
                ProblemManager problemManager = new ProblemManager(project);
                problemManager.setProblem(getProblem());
                ContentManager contentManager = toolWindow.getContentManager();
                contentManager.setSelectedContent(contentManager.getContent(1));
            });
        });

        previewCode.addActionListener(a -> {
            Problem problem = getProblem();
            Submission lastSubmission = problem.getSubmissions().get(problem.getSubmissions().size() - 1);
            ContentManager contentManager = toolWindow.getContentManager();
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Document document = EditorFactory.getInstance().createDocument(lastSubmission.getSourceCode());

            String filename = "main." + CompilerUtils.getFileExtension(lastSubmission.getCompiler());
            LightVirtualFile lightVirtualFile = new LightVirtualFile(filename, lastSubmission.getSourceCode());

            Editor editor = EditorFactory.getInstance().createEditor(document, project, lightVirtualFile, true);
            Content codeContent = contentFactory.createContent(new JBScrollPane(editor.getComponent()), "Code", false);
            Disposer.register(codeContent, () -> {
                EditorFactory.getInstance().releaseEditor(editor);
            });

            codeContent.setCloseable(true);
            contentManager.addContent(codeContent);
            contentManager.setSelectedContent(codeContent);
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startSolving.setEnabled(false);
        previewCode.setEnabled(false);
        buttonsPanel.add(startSolving);
        buttonsPanel.add(previewCode);
        return buttonsPanel;
    }

    private Problem getProblem() {
        return problem;
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
