package ru.hse.plugin.ui.window;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.ide.highlighter.ProjectFileType;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.ui.HtmlPanel;
import com.intellij.util.ui.JBUI;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Arrays;

public class ProblemPanel extends JPanel {
    private final HtmlPanel problemName = new SimpleHtmlPanel();
    private final JLabel currentState = new JLabel();
    private final JLabel numberOfAttemptsLabel = new JLabel("Number of attempts:");
    private final JTextArea problemCondition = new JTextArea();
    private final HtmlPanel source = new SimpleHtmlPanel();


    ProblemPanel(Project project, ToolWindow toolWindow) {
        super(new GridBagLayout());
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
        add(problemNameLabel, c);

        // Problem name
        problemName.setBody("AVL Tree");

        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.0;
//        c.weighty = 0.05;
        c.insets = JBUI.insets(5);
        add(problemName, c);

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
        add(problemConditionLabel, c);

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
        add(currentStateLabel, c);

        // Current state
        currentState.setForeground(JBColor.GREEN);
        currentState.setText("Passed");

        c.gridx = 1;
        c.gridy = 3;
        c.weighty = 0.0;
//        c.weighty = 0.05;
        c.insets = JBUI.insets(5);
        add(currentState, c);

        // Number of attempts
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
        add(numberOfAttemptsLabel, c);

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
        add(numberOfAttempts, c);

        // Problem condition
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
        add(conditionPane, c);


        JLabel sourceLabel = new JLabel("Source:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
//        c.weighty = 0.05;
        c.weighty = 0.0;
        c.insets = JBUI.insets(5);
        add(sourceLabel, c);

        source.setBody("<a href=\"https://codeforces.com/\">codeforces</a>");
        c.gridx = 1;
        c.gridy = 4;
        c.weighty = 0.0;
//        c.weighty = 0.05;
        c.insets = JBUI.insets(5);
        add(source, c);


        JButton startSolving = new JButton("Start solving");

        startSolving.addActionListener(a -> {
            ContentManager contentManager = toolWindow.getContentManager();
            contentManager.setSelectedContent(contentManager.getContent(1));
        });

        Arrays.stream(FileTypeRegistry.getInstance().getRegisteredFileTypes()).forEach(f -> System.out.println(f.getName()));
//        System.out.println(FileTypeRegistry.getInstance().findFileTypeByName("C++"));
//        Language l = Language.findLanguageByID("");
//        System.out.println(l);



        JButton previewCode = new JButton("Preview code");
        previewCode.addActionListener(a -> {
            ContentManager contentManager = toolWindow.getContentManager();
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            String text = "f :: Num a => a -> a -> a\n" +
                    "f x y = x*x + y*y\n" +
                    "\n" +
                    "x :: Int\n" +
                    "x = 3\n" +
                    "y :: Float\n" +
                    "y = 2.4\n" +
                    "main = print (f x y) -- не будет работать, поскольку тип x ≠ типу y";
            Document document = EditorFactory.getInstance().createDocument(text);
//            Editor editor = EditorFactory.getInstance().createEditor(document, project, JavaFileType.INSTANCE, true);
            Editor editor = EditorFactory.getInstance().createEditor(document, project, FileTypeRegistry.getInstance().findFileTypeByName("Haskell"), true);
            Content codeContent = contentFactory.createContent(new JBScrollPane(editor.getComponent()), "Code", false);
            // TODO: закрывать editor
            Disposer.register(codeContent, () -> {
                EditorFactory.getInstance().releaseEditor(editor);
            });

            codeContent.setCloseable(true);
            contentManager.addContent(codeContent);
            contentManager.setSelectedContent(codeContent);
        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.add(startSolving);
        buttonsPanel.add(previewCode);


        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 5;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.LAST_LINE_START;
//        c.weighty = 0.1;
        add(buttonsPanel, c);
    }
}
