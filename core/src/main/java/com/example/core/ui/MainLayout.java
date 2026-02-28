package com.example.core.ui;

import com.example.core.plugin.PluginRegistry;
import com.example.core.plugin.LoadedPlugin;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class MainLayout extends AppLayout {

    public MainLayout(PluginRegistry registry) {
        setPrimarySection(Section.DRAWER);

        var title = new H1("FVM Admin Tool");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

        var header = new HorizontalLayout(new DrawerToggle(), title);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.getStyle().set("padding", "var(--lumo-space-m)");
        addToNavbar(header);

        var nav = new SideNav();
        nav.addItem(new SideNavItem("Home", HomeView.class));
        nav.addItem(new SideNavItem("Plugin Manager", PluginManagerView.class));

        for (LoadedPlugin lp : registry.all()) {
            String path = "p/" + lp.plugin().id();
            nav.addItem(new SideNavItem(lp.plugin().menuLabel(), path));
        }

        addToDrawer(nav);
    }
}
