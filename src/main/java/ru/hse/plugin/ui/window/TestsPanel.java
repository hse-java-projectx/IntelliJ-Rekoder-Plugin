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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestsPanel extends JPanel {
    public TestsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setName("Tests");
        for (int k = 0; k < 3; k++) {
            setTests(Arrays.asList(new TestImpl("", ""), new TestImpl("", ""), new TestImpl("", "")));
        }
    }

    public void setTests(List<? extends Test> tests) {
        this.removeAll();
        tests.forEach(test -> this.add(new TestPanel(this, test.getInput(), test.getOutput())));
    }

    public List<Test> getTests() {
        return Arrays.stream(this.getComponents()).
                map(c -> {
                    TestPanel testPanel = (TestPanel) c;
                    return new TestImpl(testPanel.getInput(), testPanel.getOutput());
                }).collect(Collectors.toList());
    }

    public void clearTests() {
        this.removeAll();
    }

    private static class TestPanel extends JPanel implements Test {
        private final JBTextArea inputArea;
        private final JBTextArea outputArea;
        private JButton button;

        public TestPanel(JPanel parent, String input, String output) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            setPreferredSize(new Dimension(150, 150));
            JBSplitter s1 = new JBSplitter(true, 0.45f);
            s1.setResizeEnabled(false);
            JBSplitter s2 = new JBSplitter(true, 0.2f);
            s2.setResizeEnabled(false);

            inputArea = new JBTextArea(input);
//        inputArea.setMinimumSize(new Dimension(200, 50));
            outputArea = new JBTextArea(output);
//        outputArea.setMinimumSize(new Dimension(200, 50));
            button = new JButton("Delete");
            JBSplitter s3 = new JBSplitter(false);
            s3.setResizeEnabled(false);
            JLabel label = new JLabel("Passed");
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setForeground(JBColor.GREEN);
            s3.setFirstComponent(label);
            s3.setSecondComponent(button);
            button.addActionListener(a -> {
                JBPopupFactory.getInstance().createConfirmation("Sure?", "Yes", "No", () -> {parent.remove(this); parent.updateUI();}, 0).showInCenterOf(s3);
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
    }
}
