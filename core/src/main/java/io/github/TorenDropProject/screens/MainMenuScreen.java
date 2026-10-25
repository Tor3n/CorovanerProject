package io.github.TorenDropProject.screens;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.TorenDropProject.menus.MenuController;
import io.github.TorenDropProject.menus.MenuTheme;
import io.github.TorenDropProject.menus.TerminalMenuAssets;
import io.github.TorenDropProject.screens.GUIs.MainScreenGUI;
import io.github.TorenDropProject.screens.GUIs.menus.TerminalBackdrop;

public final class MainMenuScreen extends MenuScreen {
    public MainMenuScreen(MenuController menus, MenuTheme theme) {
        super(new MainScreenGUI(menus, theme), theme, menus.settings,
            new ExtendViewport(TerminalMenuAssets.WIDTH, TerminalMenuAssets.HEIGHT),
            new TerminalBackdrop(theme, menus.settings));
    }
}
