package ru.hse.plugin.ui.window;

import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.chains.SimpleDiffRequestChain;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContentImpl;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.diff.impl.DiffWindow;
import icons.RekoderIcons;
import ru.hse.plugin.data.Test;
import ru.hse.plugin.utils.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestsPanel extends JPanel {
    private final JPanel testsPanel = new JPanel();
    private final JButton newTestButton = new JButton("+") {
        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            int s = (int)(Math.min(d.getWidth(), d.getHeight()));
            return new Dimension (s,s);
        }
    };
    private final Project project;

    public TestsPanel(Project project) {
        this.project = project;
        testsPanel.setLayout(new BoxLayout(testsPanel, BoxLayout.X_AXIS));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setName("Tests");

        newTestButton.setEnabled(false);
        newTestButton.addActionListener(e -> {
            testsPanel.add(new TestPanel(project, testsPanel, "", ""), 0);
            testsPanel.updateUI();
        });

        JBScrollPane scrollPane = new JBScrollPane(testsPanel, JBScrollPane.VERTICAL_SCROLLBAR_NEVER, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        MouseWheelListener listener = scrollPane.getMouseWheelListeners()[0];
        scrollPane.addMouseWheelListener(e -> {
            MouseWheelEvent event = new MouseWheelEvent(e.getComponent(), e.getID(), e.getWhen(),
                    e.getModifiersEx() | InputEvent.SHIFT_DOWN_MASK,
                    e.getX(), e.getY(),
                    e.getClickCount(), e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation());
            listener.mouseWheelMoved(event);
        });

        add(newTestButton);
        add(scrollPane);
    }

    public void setTests(List<? extends Test> tests) {
        testsPanel.removeAll();
        tests.forEach(test -> testsPanel.add(new TestPanel(project, testsPanel, test.getInput(), test.getExpectedOutput())));
        newTestButton.setEnabled(true);
        testsPanel.updateUI();
    }

    public List<Test> getTests() {
        return Arrays.stream(testsPanel.getComponents()).
                map(c -> (TestPanel) c).
//                filter(t -> !t.getInput().isEmpty() && !t.getOutput().isEmpty()).
                collect(Collectors.toList());
    }

    public void clearTests() {
        testsPanel.removeAll();
        newTestButton.setEnabled(false);
    }

    private static class TestPanel extends JPanel implements Test {
        private final JBTextArea inputArea;
        private final JBTextArea outputArea;
        private String lastExpectedOutput;
        private String actualOutput;
        private final JButton button;
        private final JButton diff;
        JLabel label = new JLabel();
        private Status status = Status.NOT_TESTED;

        public TestPanel(Project project, JPanel parent, String input, String output) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            setPreferredSize(new Dimension(200, 200));
            JBSplitter s1 = new JBSplitter(true, 0.45f);
            s1.setResizeEnabled(false);
            JBSplitter s2 = new JBSplitter(true, 0.2f);
            s2.setResizeEnabled(false);

            inputArea = new JBTextArea(input);
//        inputArea.setMinimumSize(new Dimension(200, 50));
            outputArea = new JBTextArea(output);
//        outputArea.setMinimumSize(new Dimension(200, 50));
            button = new JButton(RekoderIcons.DELETE_TEST) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension d = super.getPreferredSize();
                    int s = (int)(Math.min(d.getWidth(), d.getHeight()));
                    return new Dimension (s,s);
                }
            };
            JPanel s3 = new JPanel(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();

            JPanel s4 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            diff = new JButton(RekoderIcons.DIFF) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension d = super.getPreferredSize();
                    int s = (int)(Math.min(d.getWidth(), d.getHeight()));
                    return new Dimension (s,s);
                }
            };
            diff.setEnabled(false);
            diff.addActionListener(a -> {
                Document expectedDocument = EditorFactory.getInstance().createDocument(lastExpectedOutput);
                DiffContent expected = new DocumentContentImpl(expectedDocument);

                Document actualDocument = EditorFactory.getInstance().createDocument(actualOutput);
                DiffContent actual = new DocumentContentImpl(actualDocument);

                DiffWindow diffWindow = new DiffWindow(project, new SimpleDiffRequestChain(new SimpleDiffRequest("Diff", expected, actual, "expected", "actual")), DiffDialogHints.DEFAULT);
                diffWindow.show();
            });
            s4.add(diff);
            s4.add(button);

            label.setHorizontalAlignment(JLabel.CENTER);
            updateLabel();

            constraints.gridy = 0;
            constraints.gridx = 0;
            constraints.weightx = 1.0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            s3.add(label, constraints);

            constraints.gridy = 0;
            constraints.gridx = 1;
//            constraints.fill = GridBagConstraints.HORIZONTAL;
            s3.add(s4, constraints);

            button.addActionListener(a -> {
                if (getInput().isEmpty() && getExpectedOutput().isEmpty()) {
                    parent.remove(this);
                    parent.updateUI();
                } else {
                    JBPopupFactory.getInstance().createConfirmation("Sure?", "Yes", "No", () -> {parent.remove(this); parent.updateUI();}, 0).showInCenterOf(button);
                }
            }); // TODO: вынести в отдельный файл

            s1.setFirstComponent(new JBScrollPane(inputArea, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
            s2.setFirstComponent(s3);
            s2.setSecondComponent(new JBScrollPane(outputArea, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
            s1.setSecondComponent(s2);

            add(s1);
        }

        @Override
        public String getInput() {
            return inputArea.getText();
        }

        @Override
        public void setInput(String input) {
            inputArea.setText(input);
        }

        @Override
        public String getExpectedOutput() {
            return outputArea.getText();
        }

        @Override
        public void setExpectedOutput(String output) {
            outputArea.setText(output);
        }

        @Override
        public String getActualOutput() {
            return actualOutput;
        }

        @Override
        public void setActualOutput(String actualOutput) {
            this.actualOutput = actualOutput;
        }

        @Override
        public Status getStatus() {
            return status;
        }

        @Override
        public void setStatus(Status status) {
            this.status = status;
            diff.setEnabled(status.equals(Status.FAILED));
            if (status.equals(Status.FAILED)) {
                lastExpectedOutput = StringUtils.trimEndLines(outputArea.getText());
            }
            outputArea.setEditable(!status.equals(Status.TESTING));
            inputArea.setEditable(!status.equals(Status.TESTING));
            updateLabel();
        }

        private void updateLabel() {
            label.setText(status.toString());
            label.setForeground(status.getColor());
            label.updateUI();
        }
    }
}
