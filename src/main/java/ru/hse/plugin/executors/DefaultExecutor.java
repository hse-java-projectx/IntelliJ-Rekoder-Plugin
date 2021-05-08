package ru.hse.plugin.executors;

import com.intellij.execution.*;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputType;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class DefaultExecutor {
    public Optional<String> execute(Project project, ProgressIndicator indicator, String input) {
        RunnerAndConfigurationSettings configurationSettings = RunManager.getInstance(project).getSelectedConfiguration();
        try {
            configurationSettings.checkSettings();
        } catch (RuntimeConfigurationException ex) {
            return Optional.empty();
        }

        final boolean[] processNotStarted = {false};

        configurationSettings.setActivateToolWindowBeforeRun(false);
        ExecutionListener executionListener = new ExecutionListener() {
            @Override
            public void processNotStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
                processNotStarted[0] = true;
            }

            @Override
            public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
                try {
                    Writer writer = new BufferedWriter(new OutputStreamWriter(handler.getProcessInput(), StandardCharsets.UTF_8));
                    writer.write(input);
                    writer.write("\n");
                    writer.flush();
                } catch (IOException e) {
                    System.out.println("failed to write input");
                }
            }
        };

        List<String> output = new ArrayList<>();
        ProcessAdapter processAdapter = new ProcessAdapter() {
            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                if (outputType.equals(ProcessOutputType.STDOUT)) {
                    output.add(event.getText());
                }
            }
        };

        if (!executeRunConfiguration(project, indicator, configurationSettings, executionListener, processAdapter)) {
            return Optional.empty();
        }

        if (indicator.isCanceled() || processNotStarted[0]) {
            return Optional.empty();
        }

        output.forEach(System.out::println);
        return Optional.of(String.join("", output));
    }

    private boolean executeRunConfiguration(Project project,
                                            ProgressIndicator indicator,
                                            RunnerAndConfigurationSettings configuration,
                                            ExecutionListener executionListener,
                                            ProcessAdapter processAdapter) {
        MessageBusConnection connection = project.getMessageBus().connect();
        CountDownLatch latch = new CountDownLatch(1);

        connection.subscribe(ExecutionManager.EXECUTION_TOPIC, new ExecutionListener() {
            @Override
            public void processNotStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
                latch.countDown();
                executionListener.processNotStarted(executorId, env);
            }

            @Override
            public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
                executionListener.processStarted(executorId, env, handler);
            }
        });

        AtomicBoolean result = new AtomicBoolean(false);

        ApplicationManager.getApplication().invokeLater(() -> {
            ProgramRunner<RunnerSettings> runner = ProgramRunner.getRunner(DefaultRunExecutor.EXECUTOR_ID, configuration.getConfiguration());
            ExecutionEnvironment environment;
            try {
                environment = ExecutionEnvironmentBuilder.create(DefaultRunExecutor.getRunExecutorInstance(), configuration).activeTarget().build();
                if (runner == null || environment == null) {
                    latch.countDown();
                    return;
                }

                environment.setCallback(descriptor -> {
                    if (descriptor == null) {
                        latch.countDown();
                        return;
                    }
                    ProcessHandler processHandler = descriptor.getProcessHandler();
                    if (processHandler != null) {
                        processHandler.addProcessListener(new ProcessAdapter() {
                            @Override
                            public void processTerminated(@NotNull ProcessEvent event) {
                                latch.countDown();
                            }
                        });
                        processHandler.addProcessListener(processAdapter);
                    }
                });

                runner.execute(environment);
                result.set(true);
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        });

        while (!indicator.isCanceled()) {
            try {
                if (latch.await(100, TimeUnit.MILLISECONDS)) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Running");
        }

        return true;
    }
}
