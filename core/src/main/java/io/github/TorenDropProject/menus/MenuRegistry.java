package io.github.TorenDropProject.menus;

import io.github.TorenDropProject.screens.MainMenuScreen;
import io.github.TorenDropProject.screens.MenuScreen;
import io.github.TorenDropProject.screens.GUIs.menus.*;

/** Explicit feature registration keeps page construction out of navigation and render loops. */
public final class MenuRegistry {
    private MenuRegistry() { }
    public static void register(MenuController menus, MenuTheme theme) {
        menus.screens.addGameScreen(MenuId.MAIN.route, new MainMenuScreen(menus, theme));
        add(menus, theme, MenuId.CREATE, new CreationPage(menus, theme));
        add(menus, theme, MenuId.CHARACTER, new CharacterPage(menus, theme));
        add(menus, theme, MenuId.INVENTORY, new InventoryPage(menus, theme));
        add(menus, theme, MenuId.JOURNAL, new JournalPage(menus, theme));
        add(menus, theme, MenuId.TRAVEL, new TravelPage(menus, theme));
        add(menus, theme, MenuId.PAUSE, new PausePage(menus, theme));
        add(menus, theme, MenuId.SETTINGS, new SettingsPage(menus, theme));
        add(menus, theme, MenuId.SAVES, new SavesPage(menus, theme));
        add(menus, theme, MenuId.CREDITS, new CreditsPage(menus, theme));
    }
    private static void add(MenuController menus, MenuTheme theme, MenuId id, MenuPage page) {
        menus.screens.addGameScreen(id.route, new MenuScreen(page, theme, menus.settings, false));
    }
}
