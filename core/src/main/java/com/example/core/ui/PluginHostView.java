package com.example.core.ui;

import com.example.core.plugin.PluginRegistry;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.*;

import java.util.Set;

@Route(value = ":pluginId", layout = MainLayout.class)
@RouteAlias(value = ":pluginId/*", layout = MainLayout.class)
public class PluginHostView extends Div implements BeforeEnterObserver {

    private final PluginRegistry registry;

    public PluginHostView(PluginRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        removeAll();

        String pluginId = event.getRouteParameters().get("pluginId").orElse("");

        String fullPath = event.getLocation().getPath();
        String prefix = pluginId;
        String subPath = fullPath.length() > prefix.length() ? fullPath.substring(prefix.length()) : "";
        if (subPath.isEmpty()) subPath = "/";
        var queryParams = event.getLocation().getQueryParameters().getParameters();

        var plugin = registry.find(pluginId);
        if (plugin.isEmpty()) {
            add(new H2("Plugin nicht gefunden"));
            add(new Paragraph("Kein Plugin mit id: " + pluginId));
            add(new Paragraph("Tipp: Plugin-JAR nach ./plugins legen und im Plugin Manager auf 'Neu laden' klicken."));
            return;
        }

        var title=plugin.get().plugin().pageTitle();
        event.getUI().access(() -> event.getUI().getPage().setTitle(title));

        Component view = plugin.get().plugin().createView(subPath, queryParams);
        add(view);
    }
}
