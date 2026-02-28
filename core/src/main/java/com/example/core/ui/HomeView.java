package com.example.core.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Home")
public class HomeView extends Div {

    public HomeView() {
        add(new H2("Home"));
        add(new Paragraph("Links siehst du das Side-Menü. Plugin-Menüpunkte werden dynamisch aus geladenen Plugin-JARs generiert."));
        add(new Paragraph("Routing ist dynamisch über /p/{pluginId} und wird von PluginHostView gerendert."));
    }
}
