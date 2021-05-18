package ru.hse.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hse.plugin.data.Command;
import ru.hse.plugin.data.Commands;

import javax.swing.*;
import java.awt.*;

public class CommandsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (new SampleDialogWrapper().showAndGet()) {
            System.out.println("Hi");
        }
    }

    private class SampleDialogWrapper extends DialogWrapper {

        public SampleDialogWrapper() {
            super(true); // use current window as parent
            init();
            setTitle("Test DialogWrapper");
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setPreferredSize(new Dimension(500, 300));

            Command command = new Command();
            command.setProblemOwner("codeforces");
            command.setCommandText("cf-cli submit $code");

            panel.add(new CommandPanel(command));
            panel.add(new CommandPanel(command));
            panel.add(new CommandPanel(command));
            panel.add(new CommandPanel(command));
            panel.add(new CommandPanel(command));
            panel.add(new CommandPanel(command));
            panel.add(new CommandPanel(command));
            panel.add(new CommandPanel(command));
            panel.add(new CommandPanel(command));
            panel.add(new CommandPanel(command));


            return new JBScrollPane(panel);
        }
    }

    private class CommandPanel extends JPanel {
        private final JBTextField problemOwner;
        private final JBTextField commandText;
        private final JCheckBox enabled;

        public CommandPanel(Command command) {
            super();
            setPreferredSize(new Dimension(300, 100));
            problemOwner = new JBTextField(command.getProblemOwner());
            commandText = new JBTextField(command.getCommandText());
            enabled = new JCheckBox();
            enabled.setSelected(command.isEnabled());
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(problemOwner);
            panel.add(commandText);
            add(panel);
            add(enabled);
        }
    }
}
