package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import io.github.TorenDropProject.menus.*;

public final class TravelPage extends MenuPage {
    private String selected;
    public TravelPage(MenuController menus, MenuTheme theme) { super(menus, theme, "06 / OVERLAND", "The blackwire road"); }
    @Override public void show() {
        selected = menus.journey == null ? "pass" : menus.journey.location;
        super.show();
    }
    @Override protected void build(Table table) {
        if (menus.journey == null) { table.add(prose("Create or load a journey to chart your route.")); return; }
        Journey journey = menus.journey;
        Table mapPanel = panel();
        Table tools = new Table();
        tools.add(label("BLACKWIRE TERRITORY  /  ROUTE SURVEY", "accent")).expandX().left();
        tools.add(label("N ^", "accent")).right();
        mapPanel.add(tools).growX().padBottom(18).row();
        mapPanel.add(new TravelMap(theme, menus.catalog, journey.location, selected, id -> { selected = id; rebuild(); }))
            .growX().height(350).row();
        mapPanel.add(label("GREEN: CARAVAN    /    AMBER: SELECTED ROAD", "mono")).left().padTop(16).row();
        table.add(mapPanel).grow().padRight(22);
        Table detail = panel();
        FrontierCatalog.Destination destination = menus.catalog.destination(selected);
        detail.add(label(destination.id.equals(journey.location) ? "CURRENT LOCATION" : "DESTINATION", "accent")).left().padBottom(16).row();
        Label areaName = label(destination.name, "heading");
        areaName.setWrap(true);
        detail.add(areaName).width(278).growX().left().padBottom(12).row();
        detail.add(prose(destination.description)).width(278).padBottom(20).row();
        detail.add(label("EXPOSURE / " + destination.danger.toUpperCase(), "mono")).left().padBottom(20).row();
        FrontierCatalog.Route route = menus.catalog.route(journey.location, selected);
        boolean here = selected.equals(journey.location);
        if (here) {
            TextButton enter = button(destination.hasLocalArea() ? "Enter local area   >" : "Local area unavailable", menus::enterWorld, true);
            enter.setDisabled(!destination.hasLocalArea());
            detail.add(enter).growX().height(52).padBottom(16).row();
        } else {
            detail.add(prose(route == null ? "No direct road. Travel via a connected waypoint."
                : route.hours + " hours on the road\n" + route.supplies + " supplies required")).growX().padBottom(14).row();
            TextButton travel = button("Travel this road   >", () -> confirm("Break camp?",
                "Travel to " + destination.name + "? This consumes " + route.supplies + " supplies and advances the journey by " + route.hours + " hours.",
                () -> { journey.travel(menus.catalog, selected); menus.root(MenuId.TRAVEL); rebuild(); }), true);
            travel.setDisabled(route == null || journey.supplies < route.supplies);
            detail.add(travel).growX().height(52).padBottom(16).row();
            if (route != null && journey.supplies < route.supplies) detail.add(prose("Low supplies. Make camp to forage.")).growX().padBottom(12).row();
        }
        detail.add(rule()).growX().height(1).padBottom(18).row();
        detail.add(label(journey.supplies + " SUPPLIES   /   " + journey.currency + " BRASS", "accent")).left().padBottom(14).row();
        detail.add(button("Forage / 12 hours", () -> confirm("Make camp?", "Spend 12 hours gathering 2 supplies. The caravan will remain here.", () -> {
            journey.forage(); rebuild();
        }))).growX().height(46).row();
        table.add(detail).width(330).growY();
    }
    @Override protected void footerActions(Table footer) {
        footer.add(button("Character", () -> menus.open(MenuId.CHARACTER))).height(38).padLeft(10);
        footer.add(button("Inventory", () -> menus.open(MenuId.INVENTORY))).height(38).padLeft(8);
        footer.add(button("Journal", () -> menus.open(MenuId.JOURNAL))).height(38).padLeft(8);
        footer.add(button("Save", () -> menus.slots(true))).height(38).padLeft(8);
        footer.add(button("Settings", () -> menus.open(MenuId.SETTINGS))).height(38).padLeft(8);
        footer.add(label("CHART YOUR COURSE.", "mono")).expandX().right();
    }
    @Override public void back() {
        if (menus.screens.canGoBack()) menus.back(); else menus.root(MenuId.MAIN);
    }
}
