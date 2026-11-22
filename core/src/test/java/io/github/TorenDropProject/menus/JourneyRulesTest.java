package io.github.TorenDropProject.menus;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.github.TorenDropProject.assets.FrontierCatalog;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class JourneyRulesTest {
    private FrontierCatalog catalog;
    private CharacterRecord character;
    @Before public void setUp() {
        File file = new File("../assets/data/frontier.json");
        if (!file.exists()) file = new File("assets/data/frontier.json");
        catalog = new Json().fromJson(FrontierCatalog.class, new FileHandle(file));
        character = new CharacterRecord(); character.name = "Mara";
    }
    @Test public void pointBuyChargesHigherScoresAndRejectsOverspending() {
        character.standardArray();
        assertEquals(0, character.remainingPoints());
        assertFalse(character.adjust(5, 1));
        assertEquals(8, character.scores[5]);
        assertTrue(character.adjust(1, -1));
        assertEquals(2, character.remainingPoints());
        assertTrue(character.adjust(5, 1));
        assertEquals(1, character.remainingPoints());
        assertEquals(-1, character.modifier(5));
    }
    @Test public void incompatibleOrDamagedSavesAreRejected() {
        Map<String, String> data = new HashMap<>();
        JourneyStore store = new JourneyStore(memoryPreferences(data), catalog);
        Journey journey = new Journey(character);
        journey.location = "pass";
        journey.visited.clear();
        journey.visited.add("pass");
        journey.version = 99;
        assertThrows(IllegalArgumentException.class, () -> store.save(1, journey));
        assertFalse(store.exists(1));
        data.put("journey.1", "{version:99}");
        assertThrows(IllegalArgumentException.class, () -> store.load(1));
        journey.version = 1; journey.worldPosition = new float[] {Float.NaN, 0, 0};
        assertThrows(IllegalArgumentException.class, () -> store.save(1, journey));
    }
    @Test public void catalogRejectsInvalidRoutesBeforeMenusUseThem() {
        catalog.validate();
        catalog.routes[0].supplies = -1;
        assertThrows(IllegalArgumentException.class, catalog::validate);
    }
    @Test public void damagedAreaPositionsAreRejectedBeforeReplacingASave() {
        Journey journey = new Journey(character);
        journey.location = "pass";
        journey.visited.clear();
        journey.visited.add("pass");
        JourneyStore store = new JourneyStore(memoryPreferences(new HashMap<>()), catalog);
        journey.areaPositions.put("forest", new float[] {0, Float.NaN, 0});
        assertThrows(IllegalArgumentException.class, () -> store.save(1, journey));
        journey.areaPositions.clear();
        journey.areaPositions.put("unknown", new float[] {0, 0, 0});
        assertThrows(IllegalArgumentException.class, () -> store.save(1, journey));
        assertFalse(store.exists(1));
    }
    private Preferences memoryPreferences(Map<String, String> data) {
        return (Preferences)Proxy.newProxyInstance(Preferences.class.getClassLoader(), new Class<?>[] {Preferences.class},
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "contains": return data.containsKey(args[0]);
                    case "getString": return data.getOrDefault(args[0], "");
                    case "putString": data.put((String)args[0], (String)args[1]); return proxy;
                    case "flush": return null;
                    default: throw new UnsupportedOperationException(method.getName());
                }
            });
    }
}
