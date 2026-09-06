package io.github.TorenDropProject.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/** One campaign snapshot. World coordinates are supplied by the gameplay screen. */
public final class Journey {
    public int version = 1;
    public CharacterRecord character;
    public String location = "mercy";
    public int supplies = 12;
    public int hours = 0;
    public int currency = 30;
    /** Legacy version-1 saves only had the Blackwire Pass scene. Migrated during validation. */
    public float[] worldPosition;
    public Map<String, float[]> areaPositions = new HashMap<>();
    public List<String> visited = new ArrayList<>();
    public List<String> log = new ArrayList<>();

    public Journey() { }
    public Journey(CharacterRecord character) {
        this.character = character;
        visited.add(location);
        log.add("Day 1 - Signed on at Mercy Crossing. Follow the blackwire road.");
    }
    public void travel(FrontierCatalog catalog, String target) {
        catalog.destination(target);
        FrontierCatalog.Route route = catalog.route(location, target);
        if (route == null) throw new IllegalArgumentException("Choose a connected destination.");
        if (supplies < route.supplies) throw new IllegalArgumentException("Not enough supplies for this route.");
        supplies -= route.supplies;
        hours += route.hours;
        location = target;
        if (!visited.contains(target)) visited.add(target);
        log.add("Day " + day() + " - Reached " + catalog.destination(target).name + ".");
        if (log.size() > 100) log.remove(0);
    }
    public void rememberPosition(String area, float[] position) {
        validatePosition(position);
        areaPositions.put(area, position.clone());
    }
    public float[] positionFor(String area) {
        float[] position = areaPositions.get(area);
        return position == null ? null : position.clone();
    }
    public int day() { return 1 + hours / 24; }
    public void forage() {
        hours += 12;
        supplies += 2;
        log.add("Day " + day() + " - Made camp and gathered 2 supplies.");
        if (log.size() > 100) log.remove(0);
    }
    public void buySupplies() {
        if (!"mercy".equals(location)) throw new IllegalArgumentException("Supplies are sold at Mercy Crossing.");
        if (currency < 5) throw new IllegalArgumentException("You need 5 brass to trade.");
        currency -= 5;
        supplies += 4;
    }
    public void validate(FrontierCatalog catalog) {
        if (version != 1) throw new IllegalArgumentException("This save version is not supported.");
        if (character == null) throw new IllegalArgumentException("The save has no character.");
        character.validate(catalog);
        catalog.destination(location);
        if (supplies < 0 || supplies > 100000 || hours < 0 || hours > 1000000 || currency < 0 || currency > 100000) {
            throw new IllegalArgumentException("The save contains invalid resources.");
        }
        if (visited == null || log == null || log.size() > 100) throw new IllegalArgumentException("The journey history is damaged.");
        for (String id : visited) catalog.destination(id);
        for (String entry : log) if (entry == null || entry.length() > 500) throw new IllegalArgumentException("Invalid journal entry.");
        if (worldPosition != null) {
            validatePosition(worldPosition);
        }
        if (areaPositions == null) throw new IllegalArgumentException("Invalid area positions.");
        for (Map.Entry<String, float[]> entry : areaPositions.entrySet()) {
            if (!catalog.destination(entry.getKey()).hasLocalArea()) throw new IllegalArgumentException("Invalid saved area.");
            validatePosition(entry.getValue());
        }
        if (worldPosition != null) {
            areaPositions.putIfAbsent("pass", worldPosition.clone());
            worldPosition = null;
        }
    }
    private static void validatePosition(float[] position) {
        if (position == null || position.length != 3) throw new IllegalArgumentException("Invalid world position.");
        for (float coordinate : position) {
            if (!Float.isFinite(coordinate)) throw new IllegalArgumentException("Invalid world position.");
        }
    }
}
