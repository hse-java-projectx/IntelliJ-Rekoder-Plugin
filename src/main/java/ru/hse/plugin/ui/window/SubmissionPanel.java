package ru.hse.plugin.ui.window;

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

    public SubmissionPanel() {
        super(new GridBagLayout());
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

    private void setupProblemName(GridBagConstraints c) {
        setupLabel(0, 0, "Problem name:", c);
        problemName.setBody("AVL Tree");
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
        for (int k = 0; k < 100; k++) {
            Submission submission = new Submission();
            submission.setName("Version " + k);
            submissions.addItem(submission);
        }
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
        c.gridy = 1;
        c.weighty = 0.5;
        c.gridwidth = 4;
        c.insets = JBUI.insets(5);
        add(new JBScrollPane(problemCondition), c);
    }

    private void setupAuthor(GridBagConstraints c) {
        setupLabel(0, 2, "Author:", c);
        author.setBody("Alex99999");
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
        for (int k = 0; k < 100; k++) {
            languages.addItem("Language " + k);
        }
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
        timeConsumed.setBody("540 ms");
        c.gridx = 1;
        c.gridy = 3;
        c.weighty = 0.0;
        c.weightx = 1.0;
        c.insets = JBUI.insets(5);
        add(timeConsumed, c);
    }

    private void setupMemoryConsumed(GridBagConstraints c) {
        setupLabel(0, 4, "Memory:", c);
        memoryConsumed.setBody("700 MB");
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

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(testAndSubmit);
        buttonsPanel.add(test);
        return buttonsPanel;
    }

    private void setupFile(GridBagConstraints c) {
        setupLabel(2, 5, "File:", c);
        MutableComboBoxModel<String> model = new DefaultComboBoxModel<>();
        files.setModel(model);
        files.addItem("main.cpp");
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
