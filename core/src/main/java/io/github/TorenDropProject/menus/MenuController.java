package io.github.TorenDropProject.menus;

import io.github.TorenDropProject.screens.GameScreen;
import io.github.TorenDropProject.screens.ScreenManager;

/** Injected navigation and session boundary shared by the menu pages and battle HUD. */
public final class MenuController {
    public final ScreenManager screens;
    public final FrontierCatalog catalog;
    public final JourneyStore saves;
    public final GameSettings settings;
    public Journey journey;
    public boolean saveMode;

    public MenuController(ScreenManager screens, FrontierCatalog catalog, JourneyStore saves, GameSettings settings) {
        this.screens = screens;
        this.catalog = catalog;
        this.saves = saves;
        this.settings = settings;
    }
    public void open(MenuId id) { screens.pushScreen(screens.getGameScreen(id.route)); }
    public void back() { screens.popScreen(); }
    public void root(MenuId id) { screens.setScreen(screens.getGameScreen(id.route)); }
    public void begin(CharacterRecord character) {
        character.name = character.name.trim();
        character.validate(catalog);
        journey = new Journey(character);
        root(MenuId.TRAVEL);
    }
    public void enterWorld() {
        if (journey == null || !catalog.destination(journey.location).hasLocalArea()) return;
        GameScreen battle = screens.getGameScreen("BattleScreen");
        if (battle == null) throw new IllegalStateException("The local area is unavailable.");
        screens.setScreen(battle);
    }
    public void pause() {
        open(MenuId.PAUSE);
    }
    public void slots(boolean saveMode) { this.saveMode = saveMode; open(MenuId.SAVES); }
    public void load(int slot) {
        Journey restored = saves.load(slot);
        journey = restored;
        root(MenuId.TRAVEL);
    }
    public void save(int slot) {
        if (journey == null) throw new IllegalStateException("There is no journey to save.");
        saves.save(slot, journey);
    }
}
