package com.example.core.ui;

import com.example.core.plugin.PluginRegistry;
import com.example.core.plugin.LoadedPlugin;
import com.example.core.utils.OSInfoUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
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

        var nav = new SideNav();
        nav.addItem(new SideNavItem("Home", HomeView.class));
        nav.addItem(new SideNavItem("Plugin Manager", PluginManagerView.class));

        for (LoadedPlugin lp : registry.all()) {
            nav.addItem(new SideNavItem(lp.plugin().menuLabel(), lp.plugin().id()));
        }
        createHeader();
        addToDrawer(nav);
    }

    private void createHeader() {
        H1 logo = new H1("eKP Web-Admin");
        logo.addClassNames("text-l", "m-m");
        Image image = new Image("images/dataport.png", "Dataport Image");

        Button logout = new Button("Log out ");
        Button resetPassword = new Button("Reset Password");
        resetPassword.setVisible(false);

        HorizontalLayout header= new HorizontalLayout(new DrawerToggle(),logo, logout, resetPassword);
        Span sp= new Span("V1.02");
        header.add(image,sp);
        System.out.println("Betriebssystem: " + OSInfoUtil.getOsName());

        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");
        addToNavbar(header);

    }
}
