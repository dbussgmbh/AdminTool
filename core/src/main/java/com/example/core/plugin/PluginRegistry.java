package com.example.core.plugin;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PluginRegistry {

    private final Map<String, LoadedPlugin> plugins = new ConcurrentHashMap<>();

    public Collection<LoadedPlugin> all() {
        return plugins.values()
                .stream()
                .sorted(Comparator.comparing(lp -> lp.plugin().menuLabel(), String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Optional<LoadedPlugin> find(String id) {
        return Optional.ofNullable(plugins.get(id));
    }

    public void put(LoadedPlugin loaded) {
        plugins.put(loaded.plugin().id(), loaded);
    }

    public void clearAndClose() {
        for (LoadedPlugin lp : plugins.values()) {
            try {
                lp.classLoader().close();
            } catch (Exception ignored) { }
        }
        plugins.clear();
    }
}
