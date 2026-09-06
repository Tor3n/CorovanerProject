package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.TorenDropProject.menus.*;

public final class InventoryPage extends TabbedMenuPage {
    public InventoryPage(MenuController menus, MenuTheme theme) { super(menus, theme, "03 / QUARTERMASTER", "What you carry"); }
    @Override protected String[] tabs() { return new String[] {"Equipment", "Supplies & belongings"}; }
    @Override protected void buildTab(Table table) {
        if (menus.journey == null) { table.add(prose("No traveler registered.")); return; }
        Journey j = menus.journey;
        CharacterRecord c = j.character;
        if (tab == 0) {
            Table weapon = panel();
            item(weapon, menus.catalog.archetype(c.archetype).weapon, "WEAPON / 3 kg", c.weaponEquipped,
                () -> { c.weaponEquipped = !c.weaponEquipped; rebuild(); });
            weapon.add(prose("Your calling's starting weapon. Stowing it keeps it in your personal pack.")).growX();
            Table armor = panel();
            item(armor, "Dust coat", "ARMOR / 4 kg", c.armorEquipped,
                () -> { c.armorEquipped = !c.armorEquipped; rebuild(); });
            armor.add(stat("Defense", Integer.toString(c.defense()), CharacterStats.defenseHelp(c))).growX().height(58);
            table.add(weapon).grow().uniform(true).padRight(18);
            table.add(armor).grow().uniform(true);
        } else {
            Table pack = panel();
            pack.add(label("CARAVAN STORES", "accent")).left().padBottom(18).row();
            pack.add(stat("Supplies", Integer.toString(j.supplies), "Shared trail provisions. Each road consumes the amount shown on the travel map.\n\nForage for 12 hours to gain 2 supplies, or buy 4 supplies for 5 brass at Mercy Crossing."))
                .growX().height(62).padBottom(10).row();
            pack.add(stat("Brass", Integer.toString(j.currency), "The caravan's trading currency. Supplies cost 5 brass for a bundle of 4 at Mercy Crossing."))
                .growX().height(62).padBottom(20).row();
            TextButton trade = button("Buy supplies / 5 brass", () -> { j.buySupplies(); rebuild(); });
            trade.setDisabled(!"mercy".equals(j.location) || j.currency < 5);
            pack.add(trade).growX().height(48).row();
            pack.add(prose("Trading post: Mercy Crossing")).growX().padTop(12);
            Table belongings = panel();
            belongings.add(label("PERSONAL BELONGINGS", "accent")).left().padBottom(18).row();
            belongings.add(label("Field journal", "heading")).left().padBottom(14).row();
            belongings.add(prose("A record of roads taken and debts outstanding.\n\nPersonal equipment: 7.5 kg.\nTrail supplies ride with the caravan."))
                .growX().padBottom(24).row();
            belongings.add(button("Read journal", () -> menus.open(MenuId.JOURNAL))).left().height(44);
            table.add(pack).grow().uniform(true).padRight(18);
            table.add(belongings).grow().uniform(true);
        }
    }
    private void item(Table table, String name, String detail, boolean equipped, Runnable toggle) {
        table.add(label(name, "heading")).left().padBottom(18).row();
        table.add(label(detail, "accent")).left().padBottom(24).row();
        table.add(button(equipped ? "Unequip" : "Equip", toggle)).left().height(44).padBottom(24).row();
        table.add(rule()).height(1).growX().padBottom(24).row();
    }
}
