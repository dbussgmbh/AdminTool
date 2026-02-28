package com.example.core.plugin;

import java.net.URLClassLoader;
import java.nio.file.Path;

public record LoadedPlugin(
        PluginUi plugin,
        Path jarPath,
        URLClassLoader classLoader
) {}
