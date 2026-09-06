package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.TorenDropProject.menus.*;

public final class CreditsPage extends MenuPage {
    public CreditsPage(MenuController menus, MenuTheme theme) { super(menus, theme, "10 / CREDITS", "Built for the long road"); }
    @Override protected void build(Table table) {
        Table credits = panel();
        credits.add(label("COROVANER", "title")).left().padBottom(24).row();
        credits.add(prose("A frontier roleplaying game by the Corovaner project contributors.\n\n"
            + "Built with Java, libGDX, Ashley, and LWJGL.\n"
            + "Typography: Liberation Serif, Sans, and Mono, licensed under SIL Open Font License 1.1.\n\n"
            + "Menu linework is drawn in-engine. Frontier names, archetypes, and travel routes are original project content.\n\n"
            + "Character and world asset notes are maintained in the project's documentation."))
            .growX().padBottom(30).row();
        credits.add(label("KEEP A LIGHT FOR THE ONES BEHIND YOU.", "accent")).left();
        table.add(credits).grow();
    }
}
