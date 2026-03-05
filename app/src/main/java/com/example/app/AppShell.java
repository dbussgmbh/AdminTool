package com.example.app;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.theme.Theme;

@Uses(Tab.class)
@Uses(Tabs.class)
@Uses(RadioButtonGroup.class)
@Theme("admintheme")
public class AppShell implements AppShellConfigurator {
}