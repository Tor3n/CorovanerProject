package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.TorenDropProject.menus.*;

public final class PausePage extends MenuPage {
    public PausePage(MenuController menus, MenuTheme theme) { super(menus, theme, "07 / PAUSED", "Take a breath, traveler."); }
    @Override protected void build(Table table) {
        Table actions = panel();
        actions.add(button("Resume   >", menus::back, true)).growX().height(52).padBottom(10).row();
        nav(actions, "Character sheet", MenuId.CHARACTER);
        nav(actions, "Inventory & equipment", MenuId.INVENTORY);
        nav(actions, "Journal", MenuId.JOURNAL);
        nav(actions, "Travel map", MenuId.TRAVEL);
        nav(actions, "Settings", MenuId.SETTINGS);
        table.add(actions).width(360).growY().padRight(26);
        Table camp = panel();
        camp.add(label("THE WORLD IS PAUSED", "accent")).left().padBottom(18).row();
        camp.add(prose("Check your gear. Mark your route.\nThe road can wait a moment.")).growX().padBottom(35).row();
        camp.add(button("Save journey", () -> menus.slots(true))).growX().height(50).padBottom(10).row();
        camp.add(button("Load journey", () -> menus.slots(false))).growX().height(50).padBottom(10).row();
        camp.add(button("Return to title", () -> menus.root(MenuId.MAIN))).growX().height(50).padBottom(10).row();
        camp.add(button("Quit to desktop", () -> confirm("Leave the frontier?", "Unsaved journey progress will be lost.", () -> Gdx.app.exit())))
            .growX().height(50).row();
        table.add(camp).grow();
    }
}
