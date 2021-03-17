package ru.hse.plugin.ui.window;

import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import ru.hse.plugin.data.Test;

import javax.swing.*;
import java.awt.*;

public class TestPanel extends Test {
    private JBTextArea inputArea;
    private JBTextArea outputArea;
    private JButton button;
    private JPanel panel = new JPanel();

    TestPanel(JPanel parent) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        panel.setPreferredSize(new Dimension(150, 150));
        JBSplitter s1 = new JBSplitter(true, 0.45f);
        s1.setResizeEnabled(false);
        JBSplitter s2 = new JBSplitter(true, 0.2f);
        s2.setResizeEnabled(false);

        inputArea = new JBTextArea();
//        inputArea.setMinimumSize(new Dimension(200, 50));
        outputArea = new JBTextArea();
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
            JBPopupFactory.getInstance().createConfirmation("Sure?", "Yes", "No", () -> {parent.remove(panel); parent.updateUI();}, 0).showInCenterOf(s3);
        });

        s1.setFirstComponent(new JBScrollPane(inputArea, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        s2.setFirstComponent(s3);
        s2.setSecondComponent(new JBScrollPane(outputArea, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        s1.setSecondComponent(s2);

        panel.add(s1);
    }

    @Override
    public String getInput() {
        return inputArea.getText();
    }

    @Override
    public String getOutput() {
        return outputArea.getText();
    }

    JComponent getComponent() {
        return panel;
    }
}
