package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.TorenDropProject.menus.*;

public final class SavesPage extends MenuPage {
    public SavesPage(MenuController menus, MenuTheme theme) { super(menus, theme, "09 / ARCHIVE", "Journeys worth remembering"); }
    @Override protected void build(Table table) {
        table.add(label(menus.saveMode ? "SAVE JOURNEY / CHOOSE A SLOT" : "LOAD JOURNEY / CHOOSE A SLOT", "accent")).left().padBottom(22).row();
        for (int slot = 1; slot <= 3; slot++) {
            final int selectedSlot = slot;
            Table row = panel();
            row.add(label("0" + slot, "heading")).width(65).left();
            boolean exists = menus.saves.exists(slot), valid = false;
            String description = "Empty slot";
            if (exists) {
                try {
                    Journey saved = menus.saves.load(slot);
                    description = saved.character.name + " / " + menus.catalog.archetype(saved.character.archetype).name
                        + "\nDay " + saved.day() + " / " + menus.catalog.destination(saved.location).name;
                    valid = true;
                } catch (RuntimeException failure) { description = "Unreadable or incompatible save.\nYou may overwrite this slot."; }
            }
            row.add(prose(description)).growX().padRight(22);
            TextButton action = button(menus.saveMode ? "Save here" : "Load", () -> {
                Runnable operation = () -> {
                    if (menus.saveMode) { menus.save(selectedSlot); rebuild(); notice.setText("Journey saved in slot " + selectedSlot + "."); }
                    else menus.load(selectedSlot);
                };
                if (menus.saveMode && menus.saves.exists(selectedSlot)) confirm("Overwrite this journey?", "The previous save in slot " + selectedSlot + " will be replaced.", operation);
                else if (!menus.saveMode && menus.journey != null) confirm("Load this journey?", "Your current unsaved progress will be replaced.", operation);
                else operation.run();
            }, true);
            action.setDisabled(menus.saveMode ? menus.journey == null : !valid);
            row.add(action).width(150).height(48);
            table.add(row).growX().padBottom(16).row();
        }
        table.add(prose("Manual saves include your character, equipment, supplies, route history, and local position. Save before closing the game.")).growX().padTop(8);
    }
}
