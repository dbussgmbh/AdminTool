package com.example.core.ui;

import com.example.core.plugin.LoadedPlugin;
import com.example.core.plugin.PluginRegistry;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Full-width header + resizable/collapsible SideMenu with persisted width.
 */
public class MainLayout extends Composite<Div> implements RouterLayout {

    private static final String POS_KEY = "nav.split.pos";          // e.g. "18%"
    private static final String POS_LAST_KEY = "nav.split.posLast"; // last non-collapsed
    private static final String COLLAPSED_KEY = "nav.collapsed";    // "true"/"false"

    private final Div content = new Div();

    public MainLayout(PluginRegistry registry) {
        getContent().setSizeFull();
        getContent().getStyle().set("display", "flex").set("flex-direction", "column");

        SideNav nav = buildNav(registry);

        content.setSizeFull();
        content.getStyle().set("padding", "var(--lumo-space-m)");

        SplitLayout split = new SplitLayout();
        split.setSizeFull();
        split.addToPrimary(nav);
        split.addToSecondary(content);
        split.setSplitterPosition(18);
        split.getStyle().set("flex", "1");

        var toggleMenuBtn = buildToggleButton(nav, split);
        var header = buildHeader(toggleMenuBtn);

        getContent().add(header, split);

        initPersistence(nav, split);
        initResponsiveOverlay(nav, split);
    }

    private SideNav buildNav(PluginRegistry registry) {
        SideNav nav = new SideNav();
        nav.setWidthFull();
        nav.getStyle()
                .set("overflow", "auto")
                .set("min-width", "220px")
                .set("border-right", "1px solid var(--lumo-contrast-10pct)")
                .set("padding", "var(--lumo-space-s)");

        nav.addItem(new SideNavItem("Home", HomeView.class));
        nav.addItem(new SideNavItem("Plugin Manager", PluginManagerView.class));

        for (LoadedPlugin lp : registry.all()) {
            var p = lp.plugin();
            nav.addItem(new SideNavItem(p.menuLabel(), p.id())); // without "p/"
        }
        return nav;
    }

    private SplitLayout buildSplit(SideNav nav) {
        SplitLayout split = new SplitLayout();
        split.setPrimaryStyle("minWidth", "220px");
        split.setPrimaryStyle("maxWidth", "50%");

        // Put nav on the left, content placeholder on the right
        split.addToPrimary(nav);
        split.addToSecondary(new Div()); // replaced later with 'content'

        // default position if no localStorage
        split.setSplitterPosition(18); // percent

        return split;
    }

    private Button buildToggleButton(SideNav nav, SplitLayout split) {
        Icon icon = VaadinIcon.MENU.create();
        icon.getStyle().set("width", "20px").set("height", "20px");

        Button toggle = new Button(icon);
        toggle.addClassName("menu-toggle");
        toggle.getElement().setAttribute("aria-label", "Menü ein-/ausblenden");
        toggle.getElement().setAttribute("title", "Menü ein-/ausblenden");

        toggle.addClickListener(e -> {
            // Collapse/Expand with persisted last width
            split.getElement().executeJs("""
              const split = this;
              const nav = $0;
              const posKey = $1, posLastKey = $2, collapsedKey = $3;

              const isCollapsed = localStorage.getItem(collapsedKey) === "true";

              if (!isCollapsed) {
                // collapse: remember current, hide nav, set 0%
                localStorage.setItem(posLastKey, split.splitterPosition || "18%");
                localStorage.setItem(collapsedKey, "true");
                nav.style.display = "none";
                split.splitterPosition = "0%";
              } else {
                // expand: restore last saved
                const last = localStorage.getItem(posLastKey) || localStorage.getItem(posKey) || "18%";
                localStorage.setItem(collapsedKey, "false");
                nav.style.display = "";
                split.splitterPosition = last;
              }
            """, nav.getElement(), POS_KEY, POS_LAST_KEY, COLLAPSED_KEY);
        });

        return toggle;
    }

    private HorizontalLayout buildHeader(Button toggleMenuBtn) {
        H1 logo = new H1("eKP Web-Admin");
        logo.getStyle().set("margin", "0").set("font-size", "var(--lumo-font-size-l)");

        Image image = new Image("images/dataport.png", "Dataport Image");
        image.setHeight("28px");


        Span version = new Span("V1.02");
        version.getStyle().set("opacity", "0.7");

        Button logout = new Button("Log out", VaadinIcon.SIGN_OUT.create());
        logout.addClassName("header-btn");

        // Spacer
        Div spacer = new Div();
        spacer.getStyle().set("flex", "1");

        HorizontalLayout header = new HorizontalLayout(
                toggleMenuBtn,
                image,
                logo,
                spacer,
                logout,
                version
        );
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.getStyle()
                .set("gap", "var(--lumo-space-m)")
                .set("padding", "var(--lumo-space-s) var(--lumo-space-m)")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("background", "var(--lumo-base-color)");

        // Make the toggle look nicer
        toggleMenuBtn.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "10px");

        return header;
    }

    private void initPersistence(SideNav nav, SplitLayout split) {
        // Restore position and save on drag end
        split.getElement().executeJs("""
          const split = this;
          const posKey = $0, posLastKey = $1;

          const savedPos = localStorage.getItem(posKey);
          if (savedPos) split.splitterPosition = savedPos;

          split.addEventListener('splitter-dragend', () => {
            localStorage.setItem(posKey, split.splitterPosition);
            localStorage.setItem(posLastKey, split.splitterPosition);
          });
        """, POS_KEY, POS_LAST_KEY);

        // Restore collapsed state
        getContent().getElement().executeJs("""
          const nav = $0;
          const split = $1;
          const collapsed = localStorage.getItem($2) === "true";
          if (collapsed) {
            nav.style.display = "none";
            split.splitterPosition = "0%";
          }
        """, nav.getElement(), split.getElement(), COLLAPSED_KEY);
    }

    /**
     * Optional nicer mobile behavior:
     * - On small screens, collapse side menu into an overlay drawer under header.
     * - Keeps desktop behavior (resizable split).
     */
    private void initResponsiveOverlay(SideNav nav, SplitLayout split) {
        // Create overlay container for nav (client-side) and move nav in/out based on breakpoint.
        // Breakpoint: 900px
        getContent().getElement().executeJs("""
          const root = $0;
          const nav = $1;
          const split = $2;

          // Create overlay drawer once
          let drawer = root.querySelector('[data-nav-drawer]');
          if (!drawer) {
            drawer = document.createElement('div');
            drawer.setAttribute('data-nav-drawer', 'true');
            drawer.style.position = 'fixed';
            drawer.style.top = '56px'; // header height approx
            drawer.style.left = '0';
            drawer.style.bottom = '0';
            drawer.style.width = '280px';
            drawer.style.maxWidth = '85vw';
            drawer.style.background = 'var(--lumo-base-color)';
            drawer.style.borderRight = '1px solid var(--lumo-contrast-10pct)';
            drawer.style.boxShadow = 'var(--lumo-box-shadow-m)';
            drawer.style.transform = 'translateX(-110%)';
            drawer.style.transition = 'transform 160ms ease';
            drawer.style.zIndex = '1000';
            drawer.style.overflow = 'auto';

            const backdrop = document.createElement('div');
            backdrop.setAttribute('data-nav-backdrop', 'true');
            backdrop.style.position = 'fixed';
            backdrop.style.top = '56px';
            backdrop.style.left = '0';
            backdrop.style.right = '0';
            backdrop.style.bottom = '0';
            backdrop.style.background = 'rgba(0,0,0,0.25)';
            backdrop.style.opacity = '0';
            backdrop.style.pointerEvents = 'none';
            backdrop.style.transition = 'opacity 160ms ease';
            backdrop.style.zIndex = '999';

            backdrop.addEventListener('click', () => {
              drawer.style.transform = 'translateX(-110%)';
              backdrop.style.opacity = '0';
              backdrop.style.pointerEvents = 'none';
            });

            root.appendChild(backdrop);
            root.appendChild(drawer);
          }

          const backdrop = root.querySelector('[data-nav-backdrop]');

          function openDrawer() {
            drawer.style.transform = 'translateX(0)';
            backdrop.style.opacity = '1';
            backdrop.style.pointerEvents = 'auto';
          }
          function closeDrawer() {
            drawer.style.transform = 'translateX(-110%)';
            backdrop.style.opacity = '0';
            backdrop.style.pointerEvents = 'none';
          }

          // Expose functions for the toggle button to use on mobile
          root.__openNavDrawer = openDrawer;
          root.__closeNavDrawer = closeDrawer;
          root.__isNavDrawerOpen = () => drawer.style.transform === 'translateX(0)';

          // Move nav into drawer for mobile, back to split for desktop
          function applyMode() {
            const mobile = window.matchMedia('(max-width: 900px)').matches;

            if (mobile) {
              // Ensure nav is visible in drawer
              if (nav.parentElement !== drawer) {
                // Hide split primary area and move nav
                nav.style.display = '';
                drawer.appendChild(nav);
                split.style.display = 'block';
                // On mobile: disable resizing effect by collapsing split position
                split.splitterPosition = '0%';
              }
            } else {
              // Desktop: ensure nav in split primary
              if (nav.parentElement === drawer) {
                closeDrawer();
                // Put nav back
                split.firstElementChild.appendChild(nav);
                // Restore collapsed state / width
                const collapsed = localStorage.getItem($3) === "true";
                if (collapsed) {
                  nav.style.display = 'none';
                  split.splitterPosition = '0%';
                } else {
                  nav.style.display = '';
                  const savedPos = localStorage.getItem($4) || localStorage.getItem($5) || '18%';
                  split.splitterPosition = savedPos;
                }
              }
            }
          }

          window.addEventListener('resize', applyMode);
          applyMode();
        """, getContent().getElement(), nav.getElement(), split.getElement(), COLLAPSED_KEY, POS_LAST_KEY, POS_KEY);

        // Make the toggle button open/close drawer on mobile instead of collapsing split
        // (We can't access the button element here easily; you can keep this optional part
        // by calling JS from the toggle click listener, see below.)
    }

    @Override
    public void showRouterLayoutContent(com.vaadin.flow.component.HasElement contentElement) {
        content.removeAll();
        content.getElement().appendChild(contentElement.getElement());
    }
}