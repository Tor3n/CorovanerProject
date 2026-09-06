package io.github.TorenDropProject.menus;

public enum MenuId {
    MAIN("MainMenu"), CREATE("CharacterCreation"), CHARACTER("CharacterSheet"),
    INVENTORY("Inventory"), JOURNAL("Journal"), TRAVEL("Travel"), PAUSE("Pause"),
    SETTINGS("Settings"), SAVES("Saves"), CREDITS("Credits");

    public final String route;
    MenuId(String route) { this.route = route; }
}
