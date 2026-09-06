package io.github.TorenDropProject.menus;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;

/** Three manual slots. Validate before replacing the running session. */
public final class JourneyStore {
    private final Preferences preferences;
    private final FrontierCatalog catalog;
    private final Json json = new Json();
    public JourneyStore(Preferences preferences, FrontierCatalog catalog) {
        this.preferences = preferences;
        this.catalog = catalog;
    }
    public boolean exists(int slot) { return preferences.contains(key(slot)); }
    public Journey load(int slot) {
        if (!exists(slot)) throw new IllegalArgumentException("This slot is empty.");
        Journey journey = json.fromJson(Journey.class, preferences.getString(key(slot)));
        if (journey == null) throw new IllegalArgumentException("This save is damaged.");
        journey.validate(catalog);
        return journey;
    }
    public void save(int slot, Journey journey) {
        journey.validate(catalog);
        preferences.putString(key(slot), json.toJson(journey));
        preferences.flush();
    }
    private String key(int slot) {
        if (slot < 1 || slot > 3) throw new IllegalArgumentException("Invalid save slot.");
        return "journey." + slot;
    }
}
