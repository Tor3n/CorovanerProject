package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.TorenDropProject.menus.*;

public final class CreationPage extends MenuPage {
    private CharacterRecord draft;
    private int step;
    private static final String[] STEPS = {"Identity", "Calling", "Physical", "Mental", "Review"};
    public CreationPage(MenuController menus, MenuTheme theme) { super(menus, theme, "01 / ENLISTMENT", "Write your name in the dust."); }
    @Override protected boolean scrollContent() { return false; }
    @Override public void show() {
        if (draft == null) { draft = new CharacterRecord(); step = 0; }
        super.show();
    }
    @Override protected void build(Table table) {
        Table rail = panel();
        for (int i = 0; i < STEPS.length; i++) {
            rail.add(label("0" + (i + 1) + "   " + STEPS[i].toUpperCase(), i == step ? "accent" : "mono"))
                .left().padBottom(22).row();
        }
        rail.add(prose("Nobody arrives here clean.\nDecide what you carry.")).width(200).padTop(18).row();
        table.add(rail).width(250).growY().padRight(24);
        Table form = panel();
        form.add(label(STEPS[step], "heading")).left().padBottom(18).row();
        if (step == 0) identity(form);
        else if (step == 1) calling(form);
        else if (step == 2 || step == 3) attributes(form);
        else review(form);
        table.add(form).grow();
    }
    @Override protected void footerActions(Table footer) {
        footer.add(label("STEP " + (step + 1) + " / " + STEPS.length, "accent")).expandX().left().padLeft(24);
        footer.add(button(step == 4 ? "Sign the ledger   >" : "Next   >", () -> {
            if (step == 0 && draft.name.trim().isEmpty()) throw new IllegalArgumentException("Give your traveler a name before continuing.");
            if (step < 4) { step++; rebuild(); }
            else { menus.begin(draft); draft = null; }
        }, true)).height(42).width(240);
    }
    private void identity(Table form) {
        form.add(label("TRAVELER'S NAME", "accent")).left().padBottom(8).row();
        TextField name = new TextField(draft.name, theme.skin);
        name.setMessageText("Name or callsign"); name.setMaxLength(28);
        name.setFocusTraversal(false);
        name.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { draft.name = name.getText(); }
        });
        form.add(name).growX().height(48).padBottom(22).row();
        form.add(label("WHERE YOU CAME FROM", "accent")).left().padBottom(10).row();
        for (FrontierCatalog.Origin origin : menus.catalog.origins) {
            TextButton choice = button(origin.name + "   /   " + origin.skill, () -> { draft.origin = origin.id; rebuild(); });
            choice.setChecked(origin.id.equals(draft.origin));
            form.add(choice).growX().height(42).padBottom(6).row();
        }
        form.add(prose(menus.catalog.origin(draft.origin).description)).growX().padTop(14).row();
    }
    private void calling(Table form) {
        form.add(prose("Choose a trade. Each calling grants a trained skill, a starting weapon, and a health die.")).growX().padBottom(18).row();
        for (FrontierCatalog.Archetype archetype : menus.catalog.archetypes) {
            TextButton choice = button(archetype.name + "   /   d" + archetype.hitDie, () -> { draft.archetype = archetype.id; rebuild(); });
            choice.setChecked(archetype.id.equals(draft.archetype));
            form.add(choice).growX().height(42).padBottom(6).row();
        }
        FrontierCatalog.Archetype selected = menus.catalog.archetype(draft.archetype);
        form.add(prose(selected.description + "\nTrained: " + selected.skill + "  /  Kit: " + selected.weapon)).growX().padTop(16).row();
    }
    private void attributes(Table form) {
        form.add(stat("Point buy", draft.remainingPoints() + " / 27", "All six attributes share a 27-point budget.\n\nScores start at 8. Increases through 13 cost 1 point each; 14 and 15 cost 2 each. You may keep points unspent. Lowering a score refunds its cost."))
            .growX().height(46).padBottom(16).row();
        Table scores = new Table();
        int start = step == 2 ? 0 : 3;
        for (int i = start; i < start + 3; i++) {
            final int ability = i;
            scores.add(stat(CharacterRecord.ABILITIES[i], Integer.toString(draft.scores[i]), CharacterStats.abilityHelp(draft, i)))
                .growX().height(56).padRight(16).padBottom(8);
            TextButton minus = button("-", () -> { draft.adjust(ability, -1); rebuild(); });
            minus.setName("decrease-" + ability);
            minus.setDisabled(draft.scores[i] <= 8);
            scores.add(minus).size(42).padRight(8).padBottom(8);
            TextButton plus = button("+", () -> { draft.adjust(ability, 1); rebuild(); });
            plus.setName("increase-" + ability);
            int price = draft.scores[i] >= 13 ? 2 : 1;
            plus.setDisabled(draft.scores[i] >= 15 || draft.remainingPoints() < price);
            scores.add(plus).size(42).padRight(16).padBottom(8);
            scores.add(label("MOD " + signed(draft.modifier(i)), "accent")).width(82).padBottom(8).row();
        }
        form.add(scores).growX().row();
        form.add(hint(button("Use standard array", () -> { draft.standardArray(); rebuild(); }), "Standard array",
            "Sets all six scores to Strength 12, Dexterity 15, Constitution 13, Intelligence 10, Wisdom 14, Charisma 8. Spends all 27 points."))
            .left().height(42).padTop(12).row();
        form.add(prose(step == 2 ? "Physical attributes: power, reflexes and endurance. Mental attributes follow on the next page."
            : "Mental attributes: reason, awareness and presence. Review your traveler on the next page."))
            .growX().padTop(14).row();
    }
    private void review(Table form) {
        FrontierCatalog.Archetype archetype = menus.catalog.archetype(draft.archetype);
        Label name = label(draft.name, "heading"); name.setWrap(true);
        form.add(name).growX().left().padBottom(8).row();
        form.add(prose(menus.catalog.origin(draft.origin).name + " / " + archetype.name + " / Level 1")).growX().padBottom(18).row();
        Table vitals = new Table();
        vitals.add(stat("Health", Integer.toString(draft.maxHealth(menus.catalog)), CharacterStats.healthHelp(draft, menus.catalog))).growX().height(56).padRight(10);
        vitals.add(stat("Defense", Integer.toString(draft.defense()), CharacterStats.defenseHelp(draft))).growX().height(56);
        form.add(vitals).growX().padBottom(20).row();
        form.add(prose("Starting kit\n" + archetype.weapon + ", dust coat, field journal\n12 supplies / 30 brass\n\nYour journey begins at Mercy Crossing. Follow the road to Blackwire Pass.\n\n" + draft.remainingPoints() + " attribute points left unspent.")).growX().row();
    }
    @Override public void back() {
        if (step > 0) { step--; rebuild(); }
        else confirm("Discard this traveler?", "This unfinished character will be discarded. Your existing journey will remain available.", () -> { draft = null; menus.back(); });
    }
}
