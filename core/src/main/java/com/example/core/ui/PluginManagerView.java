package com.example.core.ui;

import com.example.core.plugin.PluginJarLoader;
import com.example.core.plugin.PluginRegistry;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.IOException;

@Route(value = "plugins", layout = MainLayout.class)
@PageTitle("Plugin Manager")
public class PluginManagerView extends VerticalLayout {

    public PluginManagerView(PluginJarLoader loader, PluginRegistry registry) {
        setSpacing(true);

        add(new H2("Plugin Manager"));
        add(new Paragraph("Plugin-Verzeichnis: " + loader.pluginsDir()));
        add(new Paragraph("Lege neue *.jar Dateien dort ab und klicke 'Neu laden'."));

        var reload = new Button("Plugins neu laden", e -> {
            try {
                loader.reload();
                Notification.show("Plugins neu geladen");
                getUI().ifPresent(ui -> ui.getPage().reload());
            } catch (IOException ex) {
                ex.printStackTrace();
                Notification.show("Fehler beim Laden: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });
        add(reload);

        var list = new Pre();
        list.setText(registry.all().stream()
                .map(lp -> lp.plugin().id() + " -> " + lp.plugin().menuLabel() + " (" + lp.jarPath().getFileName() + ")")
                .reduce("", (a,b) -> a + (a.isEmpty() ? "" : "\n") + b));
        add(new Div(new Paragraph("Geladene Plugins:")), list);
    }
}
