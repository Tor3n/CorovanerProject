package io.github.TorenDropProject.screens;

import io.github.TorenDropProject.menus.MenuController;
import io.github.TorenDropProject.menus.MenuTheme;
import io.github.TorenDropProject.screens.GUIs.MainScreenGUI;

public final class MainMenuScreen extends MenuScreen {
    public MainMenuScreen(MenuController menus, MenuTheme theme) {
        super(new MainScreenGUI(menus, theme), theme, menus.settings, true);
    }
}
