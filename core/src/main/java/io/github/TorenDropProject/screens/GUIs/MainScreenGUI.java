package io.github.TorenDropProject.screens.GUIs;

import io.github.TorenDropProject.screens.GUIs.menus.MenuPage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.TorenDropProject.menus.*;

public final class MainScreenGUI extends MenuPage {
    public MainScreenGUI(MenuController menus, MenuTheme theme) { super(menus, theme, "00 / HOME", "Beyond the last safe mile."); }
    @Override protected void build(Table table) {
        Table actions = panel();
        actions.add(label("CARAVAN REGISTRY  /  01", "accent")).left().padBottom(18).row();
        TextButton continueButton = button("Continue journey   >", () -> menus.root(MenuId.TRAVEL), true);
        continueButton.setDisabled(menus.journey == null);
        actions.add(continueButton).growX().height(44).padBottom(8).row();
        actions.add(button("New journey", () -> {
            if (menus.journey == null) menus.open(MenuId.CREATE);
            else confirm("Start a new journey?", "Your current journey will be replaced when you finish creating a new character. Save it first if you want to return.", () -> menus.open(MenuId.CREATE));
        })).growX().height(44).padBottom(8).row();
        actions.add(button("Load journey", () -> menus.slots(false))).growX().height(44).padBottom(8).row();
        actions.add(button("Settings", () -> menus.open(MenuId.SETTINGS))).growX().height(44).padBottom(8).row();
        actions.add(button("Credits", () -> menus.open(MenuId.CREDITS))).growX().height(44).padBottom(8).row();
        actions.add(button("Quit to desktop", this::quit)).growX().height(44).row();

        table.add(actions).width(330).growY().padRight(42);
        Table hero = new Table(); hero.top().left();
        hero.add(label("A DUSTBOUND ROLEPLAYING FRONTIER", "accent")).left().padTop(30).row();
        hero.add(label("COROVANER", "title")).left().padTop(10).padBottom(12).row();
        hero.add(prose("Iron on your hip. Static in your prayers.\nThe old world is dead. Its debts are not.")).growX().left().row();
        hero.add().expandY().row();
        hero.add(label("BLACKWIRE TERRITORY", "accent")).left().padBottom(8).row();
        hero.add(prose("Keep the wheels turning.\nKeep a light for the ones behind you.")).growX().padBottom(35);
        table.add(hero).grow();
    }
    private void quit() { confirm("Leave the frontier?", "Unsaved journey progress will be lost.", () -> Gdx.app.exit()); }
    @Override public void back() { quit(); }
}
