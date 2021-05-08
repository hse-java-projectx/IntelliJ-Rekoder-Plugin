package ru.hse.plugin.ui.window;

import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import ru.hse.plugin.data.Test;
import ru.hse.plugin.data.TestImpl;

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

    public TestsPanel() {
        testsPanel.setLayout(new BoxLayout(testsPanel, BoxLayout.X_AXIS));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setName("Tests");

        newTestButton.setEnabled(false);
        newTestButton.addActionListener(e -> {
            testsPanel.add(new TestPanel(testsPanel, "", "", true), 0);
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

    public void setTests(List<? extends Test> tests, boolean canChangeTests) {
        testsPanel.removeAll();
        tests.forEach(test -> testsPanel.add(new TestPanel(testsPanel, test.getInput(), test.getOutput(), canChangeTests)));
        testsPanel.updateUI();
        newTestButton.setEnabled(canChangeTests);
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
        private JButton button;
        JLabel label = new JLabel();
        private Status status = Status.NOT_TESTED;

        public TestPanel(JPanel parent, String input, String output, boolean canChangeTest) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            setPreferredSize(new Dimension(150, 150));
            JBSplitter s1 = new JBSplitter(true, 0.45f);
            s1.setResizeEnabled(false);
            JBSplitter s2 = new JBSplitter(true, 0.2f);
            s2.setResizeEnabled(false);

            inputArea = new JBTextArea(input);
            inputArea.setEditable(canChangeTest);
//        inputArea.setMinimumSize(new Dimension(200, 50));
            outputArea = new JBTextArea(output);
            outputArea.setEditable(canChangeTest);
//        outputArea.setMinimumSize(new Dimension(200, 50));
            button = new JButton("Delete");
            button.setEnabled(canChangeTest);
            JBSplitter s3 = new JBSplitter(false);
            s3.setResizeEnabled(false);
            label.setHorizontalAlignment(JLabel.CENTER);
            updateLabel();
            s3.setFirstComponent(label);
            s3.setSecondComponent(button);
            button.addActionListener(a -> {
                if (getInput().isEmpty() && getOutput().isEmpty()) {
                    parent.remove(this);
                    parent.updateUI();
                } else {
                    JBPopupFactory.getInstance().createConfirmation("Sure?", "Yes", "No", () -> {parent.remove(this); parent.updateUI();}, 0).showInCenterOf(s3);
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
        public String getOutput() {
            return outputArea.getText();
        }

        @Override
        public void setOutput(String output) {
            outputArea.setText(output);
        }

        @Override
        public Status getStatus() {
            return status;
        }

        @Override
        public void setStatus(Status status) {
            this.status = status;
            updateLabel();
        }

        private void updateLabel() {
            label.setText(status.toString());
            label.setForeground(status.getColor());
            label.updateUI();
        }
    }
}
