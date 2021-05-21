package ru.hse.plugin.executors;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.notification.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import ru.hse.plugin.data.Problem;
import ru.hse.plugin.data.Submission;
import ru.hse.plugin.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandsExecutor {
    private static final String PROBLEM_NAME = "\\$PROBLEM_NAME";
    private static final String PROBLEM_STATEMENT = "\\$PROBLEM_STATEMENT";
    private static final String SOURCE_CODE = "\\$SOURCE_CODE";
    private static final String LANGUAGE = "\\$LANGUAGE";
    private static final String FILENAME = "\\$FILENAME";
    private static final String FILEPATH = "\\$FILEPATH";
    private static final Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");


    private final Problem problem;
    private final Submission submission;
    private final VirtualFile file;
    private final Project project;


    public CommandsExecutor(Problem problem, Submission submission, VirtualFile file, Project project) {
        this.problem = problem;
        this.submission = submission;
        this.file = file;
        this.project = project;
    }

    public void execute(String command) {
        List<String> commandArgs = replaceConstants(splitCommand(command));
        try {
            List<String> output = new ArrayList<>();
            OSProcessHandler processHandler = new OSProcessHandler(new GeneralCommandLine(commandArgs));
            processHandler.addProcessListener(new ProcessAdapter() {
                @Override
                public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                    output.add(event.getText());
                }
            });
            processHandler.startNotify();
            processHandler.waitFor();
            String message = String.join("", output);
            NotificationUtils.log(project, message, NotificationType.INFORMATION);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private List<String> splitCommand(String command) {
        List<String> matchList = new ArrayList<>();
        Matcher regexMatcher = regex.matcher(command);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                matchList.add(regexMatcher.group(2));
            } else {
                matchList.add(regexMatcher.group());
            }
        }
        return matchList;
    }

    private List<String> replaceConstants(List<String> args) {
        for (int k = 0; k < args.size(); k++) {
            String element = args.get(k);
            element = element.replaceAll(PROBLEM_NAME, problem.getName());
            element = element.replaceAll(PROBLEM_STATEMENT, problem.getStatement());
            element = element.replaceAll(SOURCE_CODE, submission.getSourceCode());
            element = element.replaceAll(LANGUAGE, submission.getCompiler());
            element = element.replaceAll(FILENAME, file.getName());
            element = element.replaceAll(FILEPATH, file.getPath());
            args.set(k, element);
        }
        return args;
    }
}
