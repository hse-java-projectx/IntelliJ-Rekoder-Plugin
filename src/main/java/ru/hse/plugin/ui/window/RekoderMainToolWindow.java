package ru.hse.plugin.ui.window;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.ColoredSideBorder;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.HtmlPanel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hse.plugin.data.Folder;
import ru.hse.plugin.data.Team;
import ru.hse.plugin.data.User;
import ru.hse.plugin.managers.ProblemsTreeManager;
import ru.hse.plugin.ui.renderers.MembersListRenderer;
import ru.hse.plugin.ui.renderers.ProblemsTreeRenderer;
import ru.hse.plugin.ui.renderers.TeamsListRenderer;
import ru.hse.plugin.utils.DataKeys;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import java.awt.*;
import java.awt.event.*;

public class RekoderMainToolWindow extends SimpleToolWindowPanel implements DataProvider {
    private Tree problemsTree;
    private JBList<Object> teams;
    private JBList<Object> members;

    private final SimpleToolWindowPanel mainPanel = new SimpleToolWindowPanel(true, true);

    public RekoderMainToolWindow() {
        super(true, true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JComponent explorer = setupExplorerPart();
        JComponent problemInfo = setupProblemInfoPart();

        JBSplitter s1 = new JBSplitter(true, 0.3f);
        s1.setFirstComponent(explorer);
        s1.setSecondComponent(problemInfo);

        panel.add(s1);
        mainPanel.setContent(panel);

        ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar("rekoder Toolbar",
                (DefaultActionGroup) actionManager.getAction("rekoder.MainWindowToolbar"), true);

        mainPanel.setToolbar(actionToolbar.getComponent());
        setContent(mainPanel);
    }

    public RekoderMainToolWindow(ToolWindow toolWindow) {
        super(true, true);
    }

    private JComponent setupExplorerPart() {
        DefaultListModel<Object> teamsModel = new DefaultListModel<>();
        DefaultListModel<Object> membersModel = new DefaultListModel<>();
        teams = new JBList<>(teamsModel);
        teams.setCellRenderer(new TeamsListRenderer());
        teams.setEmptyText("Teams");
        members = new JBList<>(membersModel);
        members.setCellRenderer(new MembersListRenderer());
        members.setEmptyText("Members");

        teams.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println(teams.getSelectedValue());
                }
            }
        }); // TODO: вынести в отдельный файл

        problemsTree = new Tree();
        problemsTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                Folder folder = (Folder) event.getPath().getLastPathComponent();
                if (!folder.isLoaded()) {
                    folder.setLoaded();
                    ProblemsTreeManager.loadFolder(folder);
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }
        }); // TODO: вынести в отдельный файл
        problemsTree.setRootVisible(false);
        problemsTree.getEmptyText().clear().appendLine("Please Login to view problems");
        problemsTree.setCellRenderer(new ProblemsTreeRenderer());

        JBSplitter horizontalSplitter = new JBSplitter(false, 0.6f);
        horizontalSplitter.setFirstComponent(new JBScrollPane(problemsTree, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));


        JBSplitter verticalSplitter = new JBSplitter(true);
        verticalSplitter.setFirstComponent(new JBScrollPane(teams, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        verticalSplitter.setSecondComponent(new JBScrollPane(members, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        horizontalSplitter.setSecondComponent(verticalSplitter);

        return horizontalSplitter;
    }

    JComponent setupProblemInfoPart() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Problem name label
        JLabel problemNameLabel = new JLabel("Problem name:");
//        problemNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
//        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weighty = 0.0;
//        c.weighty = 0.05;
//        c.weighty = 0.1;
        c.insets = JBUI.insets(5);
        infoPanel.add(problemNameLabel, c);

        // Problem name
        HtmlPanel problemName = new HtmlPanel() {
            @Override
            protected @NotNull String getBody() {
                return getText();
            }
        };
        problemName.setBody("AVL Tree");

        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.0;
//        c.weighty = 0.05;
        c.insets = JBUI.insets(5);
        infoPanel.add(problemName, c);

        // Problem condition label
        JLabel problemConditionLabel = new JLabel("Condition:");
//        problemNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.0;
//        c.weighty = 0.05;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
//        c.weighty = 0.1;
        infoPanel.add(problemConditionLabel, c);

        // Current state label
        JLabel currentStateLabel = new JLabel("Current state:");
//        problemNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
//        c.weighty = 0.05;
        c.insets = JBUI.insets(5);
        infoPanel.add(currentStateLabel, c);

        // Current state
        JLabel currentState = new JLabel();
        currentState.setForeground(JBColor.GREEN);
        currentState.setText("Passed");

        c.gridx = 1;
        c.gridy = 3;
        c.weighty = 0.0;
//        c.weighty = 0.05;
        c.insets = JBUI.insets(5);
        infoPanel.add(currentState, c);

        // Number of attempts
        JLabel numberOfAttemptsLabel = new JLabel("Number of attempts:");
        numberOfAttemptsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
//        problemNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
//        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.weightx = 0.1;
        c.weighty = 0.0;
        c.gridy = 3;
//        c.anchor = GridBagConstraints.FIRST_LINE_END;
//        c.weighty = 0.05;
        c.insets = JBUI.insets(5);
        infoPanel.add(numberOfAttemptsLabel, c);

        JLabel numberOfAttempts = new JLabel();
        numberOfAttempts.setForeground(JBColor.BLUE);
        numberOfAttempts.setText("100");
        numberOfAttempts.setHorizontalAlignment(SwingConstants.RIGHT);
        numberOfAttempts.setOpaque(true);
        numberOfAttempts.setBackground(JBColor.WHITE);
        numberOfAttempts.setBorder(JBUI.Borders.empty(5));
//        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.weightx = 0.0;
        c.gridy = 3;
        c.weighty = 0.0;
//        c.weighty = 0.05;
//        c.ipadx = 10;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.insets = JBUI.insets(5, 5, 5, 25);
        infoPanel.add(numberOfAttempts, c);

        // Problem condition
        JTextArea problemCondition = new JTextArea();
//                = new HtmlPanel() {
//            @Override
//            protected @NotNull String getBody() {
//                return getText();
//            }
//        };
        problemCondition.setLineWrap(true);
        problemCondition.setWrapStyleWord(true);
        problemCondition.setText("На столе в ряд выложены \uD835\uDC5B кубиков, каждый из которых покрашен в черный или белый цвет. Кубики пронумерованы слева направо, начиная с единицы.\n" +
                "\n" +
                "Вы можете ноль или более раз применить к последовательности кубиков следующую операцию: выбрать два соседних кубика и инвертировать их цвета (заменить белый на чёрный, и наоборот).\n" +
                "\n" +
                "Определите такую последовательность операций, что после их применения все кубики станут либо полностью белыми, либо полностью чёрными. Вам не нужно минимизировать количество операций, но их количество не должно превосходить 3⋅\uD835\uDC5B. Если невозможно сделать все кубики одноцветными, сообщите об этом.\n" +
                "\n" +
                "Входные данные\n" +
                "В первой строке следует целое число \uD835\uDC5B (2≤\uD835\uDC5B≤200) — количество кубиков.\n" +
                "\n" +
                "Во второй строке следует строка \uD835\uDC60 длины \uD835\uDC5B, состоящая из символов «W» и «B». Если \uD835\uDC56-й символ строки равен «W», то \uD835\uDC56-й кубик изначально имеет белый цвет. Если \uD835\uDC56-й символ строки равен «B», то \uD835\uDC56-й кубик изначально имеет чёрный цвет.\n" +
                "\n" +
                "Выходные данные\n" +
                "Если невозможно сделать все кубики одноцветными с помощью описанных операций, выведите −1.\n" +
                "\n" +
                "В противном случае, в первую строку выведите целое число \uD835\uDC58 (0≤\uD835\uDC58≤3⋅\uD835\uDC5B) — количество операций, которые нужно произвести. Во второй строке выведите \uD835\uDC58 целых чисел \uD835\uDC5D1,\uD835\uDC5D2,…,\uD835\uDC5D\uD835\uDC58 (1≤\uD835\uDC5D\uD835\uDC57≤\uD835\uDC5B−1), где \uD835\uDC5D\uD835\uDC57 равно позиции левого из двух соседних кубиков, у которых нужно инвертировать цвета во время \uD835\uDC57-й операции.\n" +
                "\n" +
                "Если ответов несколько, разрешается вывести любой из них.");
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0.7;
        c.weightx = 1;
        c.gridwidth = 4;
        c.insets = JBUI.insets(0, 5, 5, 5);
        JBScrollPane conditionPane = new JBScrollPane(problemCondition);
        conditionPane.setBackground(JBColor.CYAN);
        infoPanel.add(conditionPane, c);


        JLabel sourceLabel = new JLabel("Source:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
//        c.weighty = 0.05;
        c.weighty = 0.0;
        c.insets = JBUI.insets(5);
        infoPanel.add(sourceLabel, c);

        HtmlPanel source = new HtmlPanel() {
            @Override
            protected @NotNull String getBody() {
                return getText();
            }
        };
        source.setBody("<a href=\"https://codeforces.com/\">codeforces</a>");
        c.gridx = 1;
        c.gridy = 4;
        c.weighty = 0.0;
//        c.weighty = 0.05;
        c.insets = JBUI.insets(5);
        infoPanel.add(source, c);


        JButton startSolving = new JButton("Start solving");
        JButton previewCode = new JButton("Preview code");
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(startSolving);
        buttonsPanel.add(previewCode);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.LAST_LINE_START;
//        c.weighty = 0.1;
        infoPanel.add(buttonsPanel, c);


        return infoPanel;


//        JBTextArea code = new JBTextArea(10, 20);
//        code.setBackground(JBColor.GRAY);
//        return new JBScrollPane(code, JBScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    @Override
    public @Nullable Object getData(@NotNull String dataId) {
        if (DataKeys.PROBLEMS_TREE.is(dataId)) {
            return problemsTree;
        } else if (DataKeys.TEAMS_LIST.is(dataId)) {
            return teams;
        } else if (DataKeys.MEMBERS_LIST.is(dataId)) {
            return members;
        }
        return super.getData(dataId);
    }
}
