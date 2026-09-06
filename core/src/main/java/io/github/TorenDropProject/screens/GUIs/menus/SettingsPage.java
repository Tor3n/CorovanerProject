package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.TorenDropProject.menus.*;

public final class SettingsPage extends TabbedMenuPage {
    private boolean fullscreen, vsync, large, decorations;
    public SettingsPage(MenuController menus, MenuTheme theme) { super(menus, theme, "08 / CONFIGURATION", "Tune your terminal"); }
    @Override protected String[] tabs() { return new String[] {"Display", "Interface", "Controls", "Audio"}; }
    @Override public void show() {
        fullscreen = menus.settings.fullscreen; vsync = menus.settings.vsync;
        large = menus.settings.largerText; decorations = menus.settings.decorations;
        super.show();
    }
    @Override protected void buildTab(Table table) {
        Table options = panel();
        if (tab == 0) {
            options.add(label("DISPLAY", "accent")).left().padBottom(20).row();
            toggle(options, "Fullscreen", fullscreen, () -> fullscreen = !fullscreen);
            toggle(options, "Vertical sync", vsync, () -> vsync = !vsync);
            options.add(prose("Vertical sync matches drawing to your display's refresh cycle.\n\nChanges take effect when applied.")).growX().padTop(20);
        } else if (tab == 1) {
            options.add(label("READABILITY", "accent")).left().padBottom(20).row();
            toggle(options, "Larger body text", large, () -> large = !large);
            toggle(options, "Decorative background", decorations, () -> decorations = !decorations);
            options.add(prose("Stat buttons marked [?] explain their values on hover or keyboard focus.\n\nTab selects a control. Escape dismisses stat help before returning to the previous screen."))
                .growX().padTop(20);
        } else if (tab == 2) {
            controls(options, "IN MENUS", new String[][] {
                {"Tab / Shift+Tab", "Next / previous control"}, {"Enter / Space", "Activate focused button"},
                {"Escape", "Dismiss help / back"}, {"[?] stat", "Hover or focus for details"}
            });
            Table world = panel();
            controls(world, "IN THE FIELD", new String[][] {
                {"Escape", "Pause"}, {"C / I", "Character / inventory"}, {"J / M", "Journal / travel"},
                {"Mouse", "Select travelers and issue orders"}
            });
            table.add(options).grow().uniform(true).padRight(18);
            table.add(world).grow().uniform(true);
            return;
        } else {
            options.add(label("AUDIO CHANNELS", "accent")).left().padBottom(24).row();
            options.add(label("The frontier is quiet.", "heading")).left().padBottom(20).row();
            options.add(prose("No music or sound tracks are installed yet.\n\nAudio controls will appear when those channels are available.")).growX();
        }
        table.add(options).grow();
    }
    private void controls(Table table, String title, String[][] bindings) {
        table.add(label(title, "accent")).colspan(2).left().padBottom(24).row();
        for (String[] binding : bindings) {
            table.add(label(binding[0], "accent")).left().width(190).padBottom(22);
            table.add(prose(binding[1])).growX().padBottom(22).row();
        }
    }
    @Override protected void footerActions(Table footer) {
        pageNavigation(footer);
        footer.add(button("Defaults", () -> { fullscreen = false; vsync = true; large = false; decorations = true; rebuild(); })).height(38).padLeft(18);
        footer.add().expandX();
        footer.add(button("Cancel", menus::back)).height(38).padLeft(10).padRight(10);
        footer.add(button("Apply", () -> {
            menus.settings.apply(fullscreen, vsync, large, decorations);
            theme.apply(menus.settings); rebuild(); notice.setText("Configuration saved.");
        }, true)).height(38).width(140);
    }
    private void toggle(Table table, String name, boolean enabled, Runnable change) {
        table.add(button(name + "   /   " + (enabled ? "ON" : "OFF"), () -> { change.run(); rebuild(); })).growX().height(52).padBottom(12).row();
    }
    @Override public void back() {
        if (fullscreen != menus.settings.fullscreen || vsync != menus.settings.vsync || large != menus.settings.largerText || decorations != menus.settings.decorations) {
            confirm("Discard setting changes?", "Return without applying these changes?", menus::back);
        } else menus.back();
    }
}
