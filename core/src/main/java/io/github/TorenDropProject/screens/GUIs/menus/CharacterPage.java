package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.TorenDropProject.menus.*;

public final class CharacterPage extends TabbedMenuPage {
    public CharacterPage(MenuController menus, MenuTheme theme) { super(menus, theme, "02 / DOSSIER", "Character record"); }
    @Override protected String[] tabs() { return new String[] {"Overview", "Abilities", "Training", "Background"}; }
    @Override protected void buildTab(Table table) {
        if (menus.journey == null) { table.add(prose("Create or load a traveler to open their record.")); return; }
        CharacterRecord c = menus.journey.character;
        if (tab == 0) overview(table, c);
        else if (tab == 1) abilities(table, c);
        else if (tab == 2) training(table, c);
        else background(table, c);
    }
    private void overview(Table table, CharacterRecord c) {
        Table identity = panel();
        identity.add(label("REGISTERED TRAVELER", "accent")).left().padBottom(18).row();
        Label name = label(c.name, "heading"); name.setWrap(true);
        identity.add(name).width(285).left().padBottom(18).row();
        identity.add(prose(menus.catalog.archetype(c.archetype).name + " / Level " + c.level
            + "\n" + menus.catalog.origin(c.origin).name)).width(285).padBottom(24).row();
        identity.add(prose("A name in the ledger.\nA place on the caravan.")).width(285).row();
        table.add(identity).width(335).growY().padRight(18);
        Table stats = panel();
        stats.add(label("VITAL STATISTICS", "accent")).colspan(2).left().padBottom(18).row();
        stats.add(stat("Starting health", Integer.toString(c.maxHealth(menus.catalog)), CharacterStats.healthHelp(c, menus.catalog)))
            .growX().height(92).padRight(10).padBottom(10);
        stats.add(stat("Defense", Integer.toString(c.defense()), CharacterStats.defenseHelp(c))).growX().height(92).padBottom(10).row();
        stats.add(stat("Initiative", signed(c.modifier(1)), "Initiative orders turns in an encounter.\n\nYour base bonus is your Dexterity modifier: " + signed(c.modifier(1)) + "."))
            .growX().height(92).padRight(10).padBottom(10);
        stats.add(stat("Proficiency", signed(CharacterStats.proficiency(c)), "A bonus for trained skills. At level 1 it is +2.\n\nIt applies once per check, even when both background and calling grant the same training."))
            .growX().height(92).padBottom(10).row();
        stats.add(stat("Hit die", "d" + menus.catalog.archetype(c.archetype).hitDie,
            "Your calling's health die. Starting health uses its maximum face value, then adds your Constitution modifier."))
            .colspan(2).growX().height(58);
        table.add(stats).grow();
    }
    private void abilities(Table table, CharacterRecord c) {
        for (int i = 0; i < CharacterRecord.ABILITIES.length; i++) {
            Table card = panel(); card.pad(18);
            card.add(stat(CharacterRecord.ABILITIES[i], Integer.toString(c.scores[i]), CharacterStats.abilityHelp(c, i)))
                .growX().height(64).padBottom(12).row();
            card.add(label("MOD " + signed(c.modifier(i)) + "    SAVE " + signed(c.modifier(i)), "accent")).left().padBottom(12).row();
            card.add(prose(CharacterStats.abilitySummary(i))).growX();
            table.add(card).grow().uniform(true).padRight(i % 3 == 2 ? 0 : 12).padBottom(i < 3 ? 12 : 0);
            if (i % 3 == 2) table.row();
        }
    }
    private void training(Table table, CharacterRecord c) {
        Table skills = panel();
        skills.add(label("SKILL CHECK BONUSES", "accent")).colspan(2).left().padBottom(12).row();
        for (CharacterStats.Skill skill : CharacterStats.Skill.values()) {
            skills.add(stat(skill.title, signed(CharacterStats.skillBonus(c, menus.catalog, skill)), CharacterStats.skillHelp(c, menus.catalog, skill)))
                .growX().height(44).padBottom(5);
            skills.add(label(CharacterStats.trained(c, menus.catalog, skill) ? "TRAINED" : "BASE", "accent")).width(100).padLeft(18).row();
        }
        table.add(skills).grow().padRight(18);
        Table notes = panel();
        notes.add(label("YOUR TRAINING", "accent")).left().padBottom(18).row();
        notes.add(prose("Background\n" + menus.catalog.origin(c.origin).skill + "\n\nCalling\n"
            + menus.catalog.archetype(c.archetype).skill + "\n\nMatching training does not stack.\n\nFocus a skill to see its check formula."))
            .width(275);
        table.add(notes).width(325).growY();
    }
    private void background(Table table, CharacterRecord c) {
        Table origin = panel();
        origin.add(label("WHERE YOU CAME FROM", "accent")).left().padBottom(18).row();
        origin.add(label(menus.catalog.origin(c.origin).name, "heading")).left().padBottom(18).row();
        origin.add(prose(menus.catalog.origin(c.origin).description)).growX().padBottom(24).row();
        origin.add(label("TRAINING / " + menus.catalog.origin(c.origin).skill.toUpperCase(), "accent")).left();
        table.add(origin).grow().uniform(true).padRight(18);
        Table calling = panel();
        calling.add(label("WHAT YOU BECAME", "accent")).left().padBottom(18).row();
        calling.add(label(menus.catalog.archetype(c.archetype).name, "heading")).left().padBottom(18).row();
        calling.add(prose(menus.catalog.archetype(c.archetype).description)).growX().padBottom(24).row();
        calling.add(prose("Starting weapon\n" + menus.catalog.archetype(c.archetype).weapon
            + "\n\n" + (c.weaponEquipped ? "Weapon equipped" : "Weapon stowed")
            + " / " + (c.armorEquipped ? "Dust coat worn" : "Unarmored"))).growX();
        table.add(calling).grow().uniform(true);
    }
    @Override protected void footerActions(Table footer) {
        pageNavigation(footer);
        footer.add(label("HOVER / FOCUS [?]", "mono")).expandX().right().padRight(18);
        footer.add(button("Equipment", () -> menus.open(MenuId.INVENTORY))).height(38);
    }
}
