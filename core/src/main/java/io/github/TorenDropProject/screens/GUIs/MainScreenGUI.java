package io.github.TorenDropProject.screens.GUIs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.TorenDropProject.menus.MenuController;
import io.github.TorenDropProject.menus.MenuId;
import io.github.TorenDropProject.menus.MenuTheme;
import io.github.TorenDropProject.menus.TerminalMenuAssets;
import io.github.TorenDropProject.screens.GUIs.menus.MenuPage;
import io.github.TorenDropProject.screens.GUIs.menus.TerminalButton;

/** Fixed 1920 x 1080 terminal composition, centered inside the menu's full-window stage. */
public final class MainScreenGUI extends MenuPage {
    public MainScreenGUI(MenuController menus, MenuTheme theme) {
        super(menus, theme, "", "");
    }

    @Override protected void buildLayout() {
        root.pad(0).center();
        content = new Table();
        content.setName("terminal-composition");
        root.add(content).size(TerminalMenuAssets.WIDTH, TerminalMenuAssets.HEIGHT);
        build(content);
        root.getStage().setScrollFocus(null);
    }

    @Override protected void build(Table table) {
        addPainting(table, "terminal-painting", theme.terminal.painting);
        addPainting(table, "terminal-panel", theme.terminal.panel);
        addPainting(table, "terminal-rim", theme.terminal.rim);

        TerminalButton resume = action(table, 0, "Continue journey", () -> menus.root(MenuId.TRAVEL));
        resume.setDisabled(menus.journey == null);
        action(table, 1, "New journey", () -> {
            if (menus.journey == null) menus.open(MenuId.CREATE);
            else confirm("Start a new journey?", "Your current journey will be replaced when you finish creating a new character. Save it first if you want to return.", () -> menus.open(MenuId.CREATE));
        });
        action(table, 2, "Load journey", () -> menus.slots(false));
        action(table, 3, "Settings", () -> menus.open(MenuId.SETTINGS));
        action(table, 4, "Credits", () -> menus.open(MenuId.CREDITS));
        action(table, 5, "Quit to desktop", this::quit);

        // Normally empty; custom title/nameplate labels remain blank as requested.
        notice = label("", "accent");
        notice.setWrap(true);
        notice.setTouchable(Touchable.disabled);
        notice.setBounds(840, 30, 1000, 70);
        table.addActor(notice);
    }

    private void addPainting(Table table, String name, TextureRegion region) {
        Image image = new Image(region);
        image.setName(name);
        image.setTouchable(Touchable.disabled);
        image.setBounds(0, 0, TerminalMenuAssets.WIDTH, TerminalMenuAssets.HEIGHT);
        table.addActor(image);
    }

    private TerminalButton action(Table table, int row, String text, Runnable command) {
        TerminalButton button = new TerminalButton(text, theme.terminal.buttonStyle(),
            theme.terminal.lampOn, theme.terminal.lampOff);
        button.setName(text);
        // The art manifest uses top-left coordinates; Scene2D uses bottom-left.
        button.setBounds(144, TerminalMenuAssets.HEIGHT - (300 + row * 94) - 100, 480, 100);
        button.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (!button.isDisabled()) attempt(command);
            }
        });
        table.addActor(button);
        return button;
    }

    private void quit() {
        confirm("Leave the frontier?", "Unsaved journey progress will be lost.", () -> Gdx.app.exit());
    }
    @Override protected float dialogScale() { return 1.5f; }
    @Override public void back() { quit(); }
}
