package ru.hse.plugin.settings;

import com.intellij.ui.jcef.JBCefAppRequiredArgumentsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class GpuDisabler implements JBCefAppRequiredArgumentsProvider {
    @NotNull
    @Override
    public List<String> getOptions() {
        return Arrays.asList("--disable-gpu", "--disable-gpu-compositing");
    }
}
