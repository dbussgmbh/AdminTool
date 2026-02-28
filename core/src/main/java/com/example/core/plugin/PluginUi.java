package com.example.core.plugin;

import com.vaadin.flow.component.Component;

import java.util.List;
import java.util.Map;

/**
 * Dynamic plugin UI SPI.
 *
 * Implementations are discovered via {@link java.util.ServiceLoader} from plugin jars.
 *
 * Each plugin provides:
 * - id(): stable unique id (used in route /p/{id})
 * - menuLabel(): text shown in the left menu (can be loaded from plugin's own properties)
 * - createView(): returns a Vaadin component to display inside the host route.
 */
public interface PluginUi {

    String id();

    String menuLabel();

    /**
     * @param subPath everything after /p/{pluginId}/... (may be empty, may start with '/')
     * @param queryParams query params
     */
    Component createView(String subPath, Map<String, List<String>> queryParams);
}
