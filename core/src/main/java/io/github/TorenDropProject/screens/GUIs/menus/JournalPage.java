package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.TorenDropProject.menus.*;

public final class JournalPage extends TabbedMenuPage {
    private int entry;
    public JournalPage(MenuController menus, MenuTheme theme) { super(menus, theme, "04 / FIELD NOTES", "Debts & destinations"); }
    @Override protected String[] tabs() { return new String[] {"Objectives", "Travel ledger", "Frontier lore"}; }
    @Override protected void buildTab(Table table) {
        if (menus.journey == null) { table.add(prose("No journey recorded.")); return; }
        Journey j = menus.journey;
        Table page = panel();
        if (tab == 0) {
            boolean arrived = j.visited.contains("pass");
            page.add(label(arrived ? "COMPLETED / FIRST ROAD" : "ACTIVE / FIRST ROAD", "accent")).left().padBottom(20).row();
            page.add(label("Follow the blackwire", "heading")).left().padBottom(20).row();
            page.add(prose("The caravan needs a road through the hills. Reach Blackwire Pass and find the old relay trail."))
                .growX().padBottom(24).row();
            page.add(prose(arrived ? "[x] Reach Blackwire Pass\nThe pass is now in your travel record."
                : "[ ] Reach Blackwire Pass\nChoose the pass on your route map.")).growX();
        } else if (tab == 1) {
            entry = Math.min(entry, Math.max(0, j.log.size() - 1));
            page.add(label("TRAVEL LEDGER / NEWEST FIRST", "accent")).left().padBottom(20).row();
            page.add(prose(j.log.isEmpty() ? "No entries recorded." : j.log.get(j.log.size() - 1 - entry))).growX().top().expandY().row();
            Table navigation = new Table();
            TextButton newer = button("< Newer entry", () -> { entry--; rebuild(); });
            newer.setDisabled(entry == 0);
            TextButton older = button("Older entry >", () -> { entry++; rebuild(); });
            older.setDisabled(entry >= j.log.size() - 1);
            navigation.add(newer).height(44);
            navigation.add(label(j.log.isEmpty() ? "0 / 0" : (entry + 1) + " / " + j.log.size(), "accent")).expandX();
            navigation.add(older).height(44);
            page.add(navigation).growX().padTop(18);
        } else {
            page.add(label("THE OLD WIRE", "accent")).left().padBottom(24).row();
            page.add(label("When the wire hums, keep walking.", "heading")).left().padBottom(24).row();
            page.add(prose("The blackwire once carried voices across the continent. These days, travelers follow its poles from one settlement to the next.\n\nIt is a road, a landmark, and a reminder of a world that never quite learned to be silent."))
                .growX();
        }
        table.add(page).grow();
    }
}
