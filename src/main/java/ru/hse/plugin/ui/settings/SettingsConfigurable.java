package ru.hse.plugin.ui.settings;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBUI;
import icons.RekoderIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hse.plugin.data.Command;
import ru.hse.plugin.data.Commands;
import ru.hse.plugin.utils.ThreadUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SettingsConfigurable implements SearchableConfigurable {
    public static final String DISPLAY_NAME = "Rekoder";
    private JPanel mainPanel;
    private volatile boolean wasDeleted = false;
    private volatile boolean wasAdded = false;

    @Override
    public @NotNull String getId() {
        return "rekoder";
    }

    @Override
    public  String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public @Nullable JComponent createComponent() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.setBorder(JBUI.Borders.empty());

        reset();

        JButton newCommandButton = new JButton("New command");
        newCommandButton.addActionListener(a -> {
            wasAdded = true;
            addCommand(new Command(), 0);
            mainPanel.updateUI();
        });
        newCommandButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(a -> {
            ThreadUtils.runInEdt(() -> {
                new HelpDialogWrapper().showAndGet();
            });
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(newCommandButton);
        buttonsPanel.add(helpButton);

        return FormBuilder.createFormBuilder().
                addComponent(buttonsPanel).
                addComponentFillVertically(mainPanel, 0).
                getPanel();
    }

    @Override
    public boolean isModified() {
        return wasAdded || wasDeleted || Arrays.stream(mainPanel.getComponents()).
                filter(c -> c instanceof CommandPanel).
                map(c -> (CommandPanel)c).
                anyMatch(CommandPanel::isModified);
    }

    @Override
    public void apply() {
        wasDeleted = false;
        wasAdded = false;
        Commands commands = Commands.getInstance();
        ArrayList<Command> commandList = Arrays.stream(mainPanel.getComponents()).
                filter(c -> c instanceof CommandPanel).
                map(c -> ((CommandPanel) c).makeCommand()).
                collect(Collectors.toCollection(ArrayList::new));
        commands.setCommands(commandList);
    }

    @Override
    public void reset() {
        mainPanel.removeAll();
        wasAdded = false;
        wasDeleted = false;
        Commands commands = Commands.getInstance();
        commands.getCommands().forEach(this::addCommand);
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
    }

    private void addCommand(Command command) {
        CommandPanel commandPanel = new CommandPanel(command);
        commandPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, commandPanel.getPreferredSize().height));
        ThreadUtils.runWriteAction(() -> {
            mainPanel.add(commandPanel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        });
    }

    private void addCommand(Command command, int index) {
        CommandPanel commandPanel = new CommandPanel(command);
        commandPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, commandPanel.getPreferredSize().height));
        ThreadUtils.runWriteAction(() -> {
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)), index);
            mainPanel.add(commandPanel, index);
        });
    }

    private class CommandPanel extends JPanel {
        private final JBTextField problemOwnerField;
        private final JBTextField commandTextField;
        private final JCheckBox enabledBox;

        private String problemOwner;
        private String commandText;
        private boolean isEnabled;

        public CommandPanel(Command command) {
            super(new GridBagLayout());

            problemOwnerField = new JBTextField(command.getProblemOwner());
            commandTextField = new JBTextField(command.getCommandText());

            enabledBox = new JCheckBox();
            enabledBox.setSelected(command.isEnabled());
            enabledBox.setText("Enable");
            enabledBox.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton delete = new JButton(RekoderIcons.DELETE_TEST);
            delete.addActionListener(a -> {
                ThreadUtils.runWriteAction(() -> {
                    wasDeleted = true;
                    Component[] components = mainPanel.getComponents();
                    for (int k = 0; k < components.length; k++) {
                        if (components[k].equals(this)) {
                            mainPanel.remove(k);
                            mainPanel.remove(k);
                            mainPanel.updateUI();
                            return;
                        }
                    }
                });
            });
            delete.setAlignmentX(Component.CENTER_ALIGNMENT);

            GridBag gridBag = new GridBag();
            gridBag.setDefaultFill(GridBagConstraints.HORIZONTAL);
            gridBag.setDefaultInsets(0, 5, 0, 5);

            JLabel problemOwnerLabel = new JLabel("Problem owner:");
            JLabel commandTextLabel = new JLabel("Command text:");

            add(problemOwnerLabel, gridBag.nextLine().next());
            add(problemOwnerField, gridBag.next().weightx(1.0));
            add(enabledBox, gridBag.next().anchor(GridBagConstraints.LINE_END));

            add(commandTextLabel, gridBag.nextLine().next());
            add(commandTextField, gridBag.next().weightx(1.0));
            add(delete, gridBag.next().anchor(GridBagConstraints.LINE_END));

            saveState();
        }

        public boolean isModified() {
            return !problemOwner.equals(problemOwnerField.getText()) ||
                    !commandText.equals(commandTextField.getText()) ||
                    !(isEnabled == enabledBox.isSelected());
        }

        private void saveState() {
            problemOwner = problemOwnerField.getText();
            commandText = commandTextField.getText();
            isEnabled = enabledBox.isSelected();
        }

        public Command makeCommand() {
            saveState();
            return new Command(problemOwner, commandText, isEnabled);
        }
    }

    private static class HelpDialogWrapper extends DialogWrapper {
        public HelpDialogWrapper() {
            super(true);
            init();
            setTitle("Help");
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            return FormBuilder.createFormBuilder().
                    addComponent(new JLabel("Available variables:")).
                    addLabeledComponent("$PROBLEM_NAME", new JLabel("Name of the problem")).
                    addLabeledComponent("$PROBLEM_STATEMENT", new JLabel("Statement of the problem")).
                    addLabeledComponent("$SOURCE_CODE", new JLabel("Source code of the submission")).
                    addLabeledComponent("$LANGUAGE", new JLabel("Programming language of the submission")).
                    addLabeledComponent("$FILE_NAME", new JLabel("Name of the file with the source code")).
                    addLabeledComponent("$FILE_PATH", new JLabel("Path of the file with the source code")).
                    addLabeledComponent("$PROBLEM_URL", new JLabel("Problem URL is exists, empty otherwise")).
                    addLabeledComponent("$PROBLEM_ID", new JLabel("Rekoder problem id")).
                    addLabeledComponent("$SUBMISSION_ID", new JLabel("Rekoder submission id")).
                    addLabeledComponent("$REKODER_TOKEN", new JLabel("User token")).
                    getPanel();
        }

        @Override
        protected Action @NotNull [] createActions() {
            return new Action[]{ getOKAction() };
        }
    }
}
