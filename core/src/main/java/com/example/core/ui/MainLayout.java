package com.example.core.ui;

import com.example.core.plugin.LoadedPlugin;
import com.example.core.plugin.PluginRegistry;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.Theme;



public class MainLayout extends Composite<Div> implements RouterLayout {

    private static final String POS_KEY = "nav.split.pos";          // e.g. "18%"
    private static final String POS_LAST_KEY = "nav.split.posLast"; // last non-collapsed
    private static final String COLLAPSED_KEY = "nav.collapsed";    // "true"/"false"

    private final Div content = new Div();

    private SideNav nav;
    private SplitLayout split;

    public MainLayout(PluginRegistry registry) {
        Div root = getContent();
        root.setSizeFull();
        root.getStyle().set("display", "flex").set("flex-direction", "column");

        nav = buildNav(registry);

        // Content area background like admin dashboards
        content.setSizeFull();
        content.getStyle().set("background", "var(--lumo-contrast-5pct)");

        content.getStyle().set("padding", "15px 20px 20px 8px");

        split = new SplitLayout();
        split.setSizeFull();
        split.addToPrimary(nav);
        split.addToSecondary(content);
        split.setSplitterPosition(8);
        split.getStyle().set("flex", "1");

        // Make menu width feel "admin"
        split.setPrimaryStyle("minWidth", "160px");
        split.setPrimaryStyle("maxWidth", "420px");

        Button toggleMenuBtn = buildToggleButton(nav, split);
        Component header = buildHeader(toggleMenuBtn);

        root.add(header, split);

        initPersistence(nav, split);
        initResponsiveOverlay(nav, split);
    }

    // ---------------------------
    // NAV
    // ---------------------------
    private SideNav buildNav(PluginRegistry registry) {
        SideNav nav = new SideNav();
        nav.setWidth("100%");
        nav.getStyle()
                .set("overflow", "auto")
                .set("padding", "var(--lumo-space-xs)")
                .set("background", "var(--lumo-base-color)")
                .set("border-right", "1px solid var(--lumo-contrast-10pct)");

        nav.addItem(new SideNavItem("Home", HomeView.class));
        nav.addItem(new SideNavItem("Plugin Manager", PluginManagerView.class));

        for (LoadedPlugin lp : registry.all()) {
            var p = lp.plugin();
            nav.addItem(new SideNavItem(p.menuLabel(), p.id())); // plugin routes handled by your host
        }
        return nav;
    }

    // ---------------------------
    // HEADER (classic admin)
    // ---------------------------
    private Component buildHeader(Button toggleMenuBtn) {
        // Title
        H1 title = new H1("eKP Web-Admin");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-xl)")
                .set("font-weight", "600");

        // Toggle button styling
        toggleMenuBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        toggleMenuBtn.getStyle()
                .set("width", "44px")
                .set("height", "44px");

        HorizontalLayout left = new HorizontalLayout(toggleMenuBtn, title);
        left.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        left.setSpacing(true);

        // Notifications
//        Icon bell = VaadinIcon.BELL.create();
//        bell.getStyle()
//                .set("width", "24px")
//                .set("height", "24px");

       // Button notifications = new Button(bell);
//        notifications.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
//        notifications.getStyle()
//                .set("position", "relative")
//                .set("border", "1px solid var(--lumo-contrast-10pct)")
//                .set("border-radius", "10px")
//                .set("width", "40px")
//                .set("height", "40px");

        // Badge (optional)
        Span badge = new Span("3");
        badge.getStyle()
                .set("position", "absolute")
                .set("top", "-6px")
                .set("right", "-6px")
                .set("background", "var(--lumo-error-color)")
                .set("color", "white")
                .set("border-radius", "999px")
                .set("font-size", "12px")
                .set("min-width", "18px")
                .set("height", "18px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("padding", "0 5px")
                .set("box-shadow", "0 1px 2px rgba(0,0,0,0.2)");
     //   notifications.getElement().appendChild(badge.getElement());

        // User menu
        Avatar avatar = new Avatar("Admin User");
        avatar.setHeight("32px");
        avatar.setWidth("32px");

        MenuBar userMenu = new MenuBar();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        var userItem = userMenu.addItem(avatar);

        SubMenu sub = userItem.getSubMenu();
        sub.addItem("Profile", e -> UI.getCurrent().navigate("profile"));
        sub.addItem("Settings", e -> UI.getCurrent().navigate("settings"));
        sub.addItem("Logout", e -> doLogout());

        // Logo + Version
        Image logo = new Image("images/dataport.png", "Dataport Image");
        logo.setHeight("60px");
        //logo.getStyle().set("opacity", "0.95");

        Span version = new Span("V1.01");
        version.getStyle()
                .set("opacity", "0.8")
                .set("font-size", "var(--lumo-font-size-s)");

        HorizontalLayout right = new HorizontalLayout(
                //notifications,
                userMenu,
                logo,
                version
        );
        right.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        right.setSpacing(true);

        HorizontalLayout topbar = new HorizontalLayout(left, right);
        topbar.setWidthFull();
        topbar.getStyle().set("flex", "0 0 auto");
        topbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        topbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Sticky classic admin header
        topbar.getStyle()
                .set("padding", "0 var(--lumo-space-xl)")
                .set("background", "var(--app-header-background)")
                .set("color", "white")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        // Mark for JS offset calc
        topbar.getElement().setAttribute("data-app-header", "true");

        return topbar;
    }

    private void doLogout() {
        // If Spring Security uses POST /logout, configure it accordingly or implement POST submit.
        UI.getCurrent().getPage().setLocation("logout");
    }

    // ---------------------------
    // Toggle: Desktop collapse / Mobile drawer
    // ---------------------------
    private Button buildToggleButton(SideNav nav, SplitLayout split) {
        Icon icon = VaadinIcon.MENU.create();
        icon.getStyle().set("width", "20px").set("height", "20px");

        Button toggle = new Button(icon);
        toggle.getElement().setAttribute("aria-label", "Menü ein-/ausblenden");
        toggle.getElement().setAttribute("title", "Menü ein-/ausblenden");

        toggle.addClickListener(e -> {
            split.getElement().executeJs("""
              const split = this;
              const nav = $0;
              const root = $1;
              const posKey = $2, posLastKey = $3, collapsedKey = $4;

              const mobile = window.matchMedia('(max-width: 900px)').matches;

              if (mobile && root.__openNavDrawer) {
                if (root.__isNavDrawerOpen && root.__isNavDrawerOpen()) root.__closeNavDrawer();
                else root.__openNavDrawer();
                return;
              }

              const isCollapsed = localStorage.getItem(collapsedKey) === "true";

              if (!isCollapsed) {
                localStorage.setItem(posLastKey, split.splitterPosition || "18%");
                localStorage.setItem(collapsedKey, "true");
                nav.style.display = "none";
                split.splitterPosition = "0%";
              } else {
                const last = localStorage.getItem(posLastKey) || localStorage.getItem(posKey) || "18%";
                localStorage.setItem(collapsedKey, "false");
                nav.style.display = "";
                split.splitterPosition = last;
              }
            """, nav.getElement(), getContent().getElement(), POS_KEY, POS_LAST_KEY, COLLAPSED_KEY);
        });

        return toggle;
    }

    // ---------------------------
    // Persist split + collapsed
    // ---------------------------
    private void initPersistence(SideNav nav, SplitLayout split) {
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

    // ---------------------------
    // Mobile overlay drawer
    // ---------------------------
    private void initResponsiveOverlay(SideNav nav, SplitLayout split) {
        getContent().getElement().executeJs("""
          const root = $0;
          const nav = $1;
          const split = $2;

          let drawer = root.querySelector('[data-nav-drawer]');
          if (!drawer) {
            drawer = document.createElement('div');
            drawer.setAttribute('data-nav-drawer', 'true');
            drawer.style.position = 'fixed';
            drawer.style.left = '0';
            drawer.style.bottom = '0';
            drawer.style.width = '280px';
            drawer.style.maxWidth = '85vw';
            drawer.style.background = 'var(--lumo-base-color)';
            drawer.style.borderRight = '1px solid var(--lumo-contrast-10pct)';
            drawer.style.boxShadow = 'var(--lumo-box-shadow-m)';
            drawer.style.transform = 'translateX(-110%)';
            drawer.style.transition = 'transform 160ms ease';
            drawer.style.zIndex = '1200';
            drawer.style.overflow = 'auto';

            const backdrop = document.createElement('div');
            backdrop.setAttribute('data-nav-backdrop', 'true');
            backdrop.style.position = 'fixed';
            backdrop.style.left = '0';
            backdrop.style.right = '0';
            backdrop.style.bottom = '0';
            backdrop.style.background = 'rgba(0,0,0,0.25)';
            backdrop.style.opacity = '0';
            backdrop.style.pointerEvents = 'none';
            backdrop.style.transition = 'opacity 160ms ease';
            backdrop.style.zIndex = '1190';

            backdrop.addEventListener('click', () => {
              drawer.style.transform = 'translateX(-110%)';
              backdrop.style.opacity = '0';
              backdrop.style.pointerEvents = 'none';
            });

            root.appendChild(backdrop);
            root.appendChild(drawer);
          }

          const backdrop = root.querySelector('[data-nav-backdrop]');

          function headerHeight() {
            const header = document.querySelector('[data-app-header="true"]');
            return header ? header.getBoundingClientRect().height : 80;
          }

          function applyTopOffset() {
            const top = headerHeight();
            drawer.style.top = top + 'px';
            backdrop.style.top = top + 'px';
          }

          function openDrawer() {
            applyTopOffset();
            drawer.style.transform = 'translateX(0)';
            backdrop.style.opacity = '1';
            backdrop.style.pointerEvents = 'auto';
          }

          function closeDrawer() {
            drawer.style.transform = 'translateX(-110%)';
            backdrop.style.opacity = '0';
            backdrop.style.pointerEvents = 'none';
          }

          root.__openNavDrawer = openDrawer;
          root.__closeNavDrawer = closeDrawer;
          root.__isNavDrawerOpen = () => drawer.style.transform === 'translateX(0)';

          function applyMode() {
            applyTopOffset();
            const mobile = window.matchMedia('(max-width: 900px)').matches;

            if (mobile) {
              if (nav.parentElement !== drawer) {
                nav.style.display = '';
                drawer.appendChild(nav);
                split.splitterPosition = '0%';
              }
            } else {
              if (nav.parentElement === drawer) {
                closeDrawer();
                split.firstElementChild.appendChild(nav);

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
        """, getContent().getElement(), nav.getElement(), split.getElement(),
                COLLAPSED_KEY, POS_LAST_KEY, POS_KEY);
    }

    // ---------------------------
    // RouterLayout content: wrap in "card"
    // ---------------------------
    @Override
    public void showRouterLayoutContent(HasElement contentElement) {
        content.removeAll();

        Div card = new Div();
        card.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "10px")
                .set("box-shadow", "0 1px 2px rgba(0,0,0,0.04)")
                .set("padding", "var(--lumo-space-l)")
                .set("min-height", "calc(100vh - 10px)"); // ~ header + margins

        card.getElement().appendChild(contentElement.getElement());
        content.add(card);
    }
}