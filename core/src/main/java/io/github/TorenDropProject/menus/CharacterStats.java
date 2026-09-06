package io.github.TorenDropProject.menus;

/** Shared explanations and derived sheet values; pages do not duplicate rule arithmetic. */
public final class CharacterStats {
    private CharacterStats() { }
    private static final String[] ABILITY_HELP = {
        "Physical power: lifting, climbing and forcing a path. Governs Athletics.",
        "Reflexes and coordination. Contributes to defense and initiative.",
        "Endurance and resilience. Adds to your starting health.",
        "Reasoning and technical knowledge. Governs Investigation and Technology.",
        "Awareness and judgment. Governs Perception and Survival.",
        "Presence and influence. Governs Persuasion."
    };
    public enum Skill {
        ATHLETICS("Athletics", 0, "Climb, jump, swim and overcome physical obstacles."),
        INVESTIGATION("Investigation", 3, "Search for clues and work out how things fit together."),
        TECHNOLOGY("Technology", 3, "Understand, diagnose and repair old-world machinery."),
        PERCEPTION("Perception", 4, "Notice movement, hidden threats and changes around you."),
        SURVIVAL("Survival", 4, "Read tracks, navigate wilderness and find shelter."),
        PERSUASION("Persuasion", 5, "Negotiate and influence others through conversation.");
        public final String title, description;
        public final int ability;
        Skill(String title, int ability, String description) { this.title = title; this.ability = ability; this.description = description; }
    }
    public static int proficiency(CharacterRecord c) { return 2 + (c.level - 1) / 4; }
    public static boolean trained(CharacterRecord c, FrontierCatalog catalog, Skill skill) {
        return skill.title.equals(catalog.origin(c.origin).skill) || skill.title.equals(catalog.archetype(c.archetype).skill);
    }
    public static int skillBonus(CharacterRecord c, FrontierCatalog catalog, Skill skill) {
        return c.modifier(skill.ability) + (trained(c, catalog, skill) ? proficiency(c) : 0);
    }
    public static String signed(int value) { return (value >= 0 ? "+" : "") + value; }
    public static String abilitySummary(int ability) { return ABILITY_HELP[ability]; }
    public static String abilityHelp(CharacterRecord c, int ability) {
        return ABILITY_HELP[ability] + "\n\nScore: " + c.scores[ability] + ". Modifier: " + signed(c.modifier(ability))
            + ".\nModifier = floor((score - 10) / 2).\nThe base saving throw uses this same modifier; no save proficiency is assigned yet.";
    }
    public static String healthHelp(CharacterRecord c, FrontierCatalog catalog) {
        return "Starting health is your calling's maximum hit die plus your Constitution modifier.\n\n"
            + catalog.archetype(c.archetype).hitDie + " + (" + signed(c.modifier(2)) + ") = " + c.maxHealth(catalog)
            + ".\nThis record shows starting health; combat damage is not tracked here yet.";
    }
    public static String defenseHelp(CharacterRecord c) {
        return "The target number an attack must meet.\n\n" + (c.armorEquipped ? "Dust coat: 12" : "Unarmored: 10")
            + " + Dexterity (" + signed(c.modifier(1)) + ") = " + c.defense() + ".\nEquip or remove armor in Inventory to change the base value.";
    }
    public static String skillHelp(CharacterRecord c, FrontierCatalog catalog, Skill skill) {
        return skill.description + "\n\n" + CharacterRecord.ABILITIES[skill.ability] + " " + signed(c.modifier(skill.ability))
            + (trained(c, catalog, skill) ? " + proficiency " + signed(proficiency(c)) : " + no proficiency")
            + " = " + signed(skillBonus(c, catalog, skill)) + ".\nAdd this bonus to a d20 skill check. Training from both background and calling applies only once.";
    }
}
