package com.example.core.plugin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.jar.JarFile;

/**
 * Loads plugin jars from a directory into isolated URLClassLoaders and discovers PluginUi via ServiceLoader.
 *
 * Supports both:
 * - plain jars (classes/resources at jar root)
 * - Spring Boot repackaged jars (classes/resources under BOOT-INF/classes)
 */
@Service
public class PluginJarLoader {

    private final PluginRegistry registry;
    private final Path pluginsDir;

    public PluginJarLoader(PluginRegistry registry,
                           @Value("${app.plugins.dir:plugins}") String pluginsDir) throws IOException {
        this.registry = registry;
        this.pluginsDir = Paths.get(pluginsDir).toAbsolutePath();
        Files.createDirectories(this.pluginsDir);
    }

    public Path pluginsDir() {
        return pluginsDir;
    }

    public synchronized List<LoadedPlugin> reload() throws IOException {
        registry.clearAndClose();

        List<LoadedPlugin> loaded = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(pluginsDir, "*.jar")) {
            for (Path jar : stream) {
                LoadedPlugin lp = loadOne(jar);
                if (lp != null) {
                    loaded.add(lp);
                    registry.put(lp);
                } else {
                    System.err.println("[plugins] No PluginUi implementation found in " + jar.getFileName());
                }
            }
        }
        return loaded;
    }

    private LoadedPlugin loadOne(Path jar) {
        try {
            URL rootUrl = jar.toUri().toURL();

            // If it's a Spring Boot jar, also add BOOT-INF/classes/ as a classpath root
            List<URL> urls = new ArrayList<>();
            urls.add(rootUrl);

            try (JarFile jf = new JarFile(jar.toFile())) {
                if (jf.getEntry("BOOT-INF/classes/") != null) {
                    URL bootClasses = new URL("jar:" + rootUrl.toExternalForm() + "!/BOOT-INF/classes/");
                    urls.add(bootClasses);
                }
            }

            ClassLoader parent = Thread.currentThread().getContextClassLoader();
            URLClassLoader cl = new URLClassLoader(urls.toArray(URL[]::new), parent);

            ServiceLoader<PluginUi> sl = ServiceLoader.load(PluginUi.class, cl);
            for (PluginUi plugin : sl) {
                return new LoadedPlugin(plugin, jar, cl);
            }

            cl.close();
            return null;
        } catch (Exception e) {
            System.err.println("[plugins] Failed to load plugin jar: " + jar.getFileName());
            e.printStackTrace();
            return null;
        }
    }
}
