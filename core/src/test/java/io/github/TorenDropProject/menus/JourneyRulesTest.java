package io.github.TorenDropProject.menus;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
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
    @Test public void invalidRouteAndInsufficientSuppliesDoNotMutateJourney() {
        Journey journey = new Journey(character);
        assertThrows(IllegalArgumentException.class, () -> journey.travel(catalog, "salt"));
        journey.supplies = 1;
        assertThrows(IllegalArgumentException.class, () -> journey.travel(catalog, "pass"));
        assertEquals("mercy", journey.location);
        assertEquals(0, journey.hours);
        assertEquals(1, journey.supplies);
        assertEquals(1, journey.log.size());
    }
    @Test public void travelChargesOnceAndTracksUniqueVisits() {
        Journey journey = new Journey(character);
        journey.travel(catalog, "pass");
        assertEquals(8, journey.hours);
        assertEquals(10, journey.supplies);
        journey.travel(catalog, "mercy");
        journey.travel(catalog, "pass");
        assertEquals(2, journey.visited.size());
        assertEquals(2, journey.day());
    }
    @Test public void strandedCaravanCanRecoverWithoutCurrency() {
        Journey journey = new Journey(character);
        journey.travel(catalog, "pass"); journey.supplies = 0; journey.currency = 0;
        journey.forage(); journey.travel(catalog, "mercy");
        assertEquals("mercy", journey.location);
        assertEquals(28, journey.hours);
    }
    @Test public void saveRoundTripPreservesCharacterEquipmentAndPosition() {
        Map<String, String> data = new HashMap<>();
        JourneyStore store = new JourneyStore(memoryPreferences(data), catalog);
        Journey journey = new Journey(character);
        character.standardArray(); character.armorEquipped = false;
        journey.travel(catalog, "pass"); journey.rememberPosition("pass", new float[] {2, 0, -4});
        store.save(1, journey);
        character.name = "Changed after saving";
        Journey restored = store.load(1);
        assertEquals("Mara", restored.character.name);
        assertEquals("pass", restored.location);
        assertFalse(restored.character.armorEquipped);
        assertArrayEquals(new float[] {2, 0, -4}, restored.positionFor("pass"), 0);
        assertEquals(journey.log, restored.log);
        assertFalse(store.exists(2));
    }
    @Test public void incompatibleOrDamagedSavesAreRejected() {
        Map<String, String> data = new HashMap<>();
        JourneyStore store = new JourneyStore(memoryPreferences(data), catalog);
        Journey journey = new Journey(character);
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
    @Test public void travelAndSaveKeepPositionsInTheirOwnAreas() {
        Journey journey = new Journey(character);
        journey.travel(catalog, "pass");
        float[] pass = {2, 0, -4};
        journey.rememberPosition("pass", pass);
        pass[0] = 999;
        journey.travel(catalog, "forest");
        assertNull(journey.positionFor("forest"));
        journey.rememberPosition("forest", new float[] {23.5f, 0, -6.5f});
        journey.travel(catalog, "pass");
        assertArrayEquals(new float[] {2, 0, -4}, journey.positionFor("pass"), 0);
        journey.positionFor("pass")[0] = 888;
        JourneyStore store = new JourneyStore(memoryPreferences(new HashMap<>()), catalog);
        store.save(1, journey);
        Journey restored = store.load(1);
        assertArrayEquals(new float[] {2, 0, -4}, restored.positionFor("pass"), 0);
        assertArrayEquals(new float[] {23.5f, 0, -6.5f}, restored.positionFor("forest"), 0);
        assertNull(new Journey(character).positionFor("pass"));
    }
    @Test public void legacyPositionBelongsToPassEvenWhenSavedAtAnOverlandWaypoint() {
        Journey legacy = new Journey(character);
        legacy.location = "relay";
        legacy.worldPosition = new float[] {2, 0, -4};
        Map<String, String> data = new HashMap<>();
        // Old saves have neither the new field nor an explicit default version.
        Json json = new Json();
        data.put("journey.1", json.toJson(legacy));
        Journey restored = new JourneyStore(memoryPreferences(data), catalog).load(1);
        assertArrayEquals(new float[] {2, 0, -4}, restored.positionFor("pass"), 0);
        assertNull(restored.positionFor("relay"));
        assertNull(restored.positionFor("forest"));
        assertNull(restored.worldPosition);
    }
    @Test public void damagedAreaPositionsAreRejectedBeforeReplacingASave() {
        Journey journey = new Journey(character);
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
