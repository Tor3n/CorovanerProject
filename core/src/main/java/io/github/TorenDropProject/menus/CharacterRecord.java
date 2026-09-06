package io.github.TorenDropProject.menus;

import java.util.Arrays;

/** Serializable character rules; no dependency on an actor or a render model. */
public final class CharacterRecord {
    public static final String[] ABILITIES = {"Strength", "Dexterity", "Constitution", "Intelligence", "Wisdom", "Charisma"};
    private static final int[] COSTS = {0, 1, 2, 3, 4, 5, 7, 9};
    public String name = "";
    public String origin = "caravan";
    public String archetype = "gunslinger";
    public int[] scores = {8, 8, 8, 8, 8, 8};
    public int level = 1;
    public boolean weaponEquipped = true;
    public boolean armorEquipped = true;

    public int remainingPoints() {
        int points = 27;
        for (int score : scores) points -= COSTS[score - 8];
        return points;
    }
    public boolean adjust(int ability, int amount) {
        int old = scores[ability];
        int next = old + amount;
        if (next < 8 || next > 15) return false;
        scores[ability] = next;
        if (remainingPoints() < 0) { scores[ability] = old; return false; }
        return true;
    }
    public int modifier(int ability) { return Math.floorDiv(scores[ability] - 10, 2); }
    public int maxHealth(FrontierCatalog catalog) { return catalog.archetype(archetype).hitDie + modifier(2); }
    public int defense() { return (armorEquipped ? 12 : 10) + modifier(1); }
    public void standardArray() { scores = new int[] {12, 15, 13, 10, 14, 8}; }
    public void validate(FrontierCatalog catalog) {
        if (name == null || name.trim().isEmpty() || name.length() > 28) throw new IllegalArgumentException("Enter a name (1-28 characters).");
        if (name.chars().anyMatch(Character::isISOControl)) throw new IllegalArgumentException("The name contains invalid characters.");
        catalog.origin(origin);
        catalog.archetype(archetype);
        if (scores == null || scores.length != 6 || Arrays.stream(scores).anyMatch(s -> s < 8 || s > 15)) {
            throw new IllegalArgumentException("Attributes must be between 8 and 15.");
        }
        if (remainingPoints() < 0 || level != 1) throw new IllegalArgumentException("Invalid character rules.");
    }
}
