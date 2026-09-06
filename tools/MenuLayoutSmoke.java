package io.github.TorenDropProject;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import io.github.TorenDropProject.menus.*;
import java.util.ArrayList;
import java.util.List;

/** Desktop regression: bounded pages, long names/history, large text, focus and hover help. */
public class MenuLayoutSmoke extends Main {
    private MenuController controller;
    private final List<Runnable> steps = new ArrayList<>();
    private int frame;
    public MenuLayoutSmoke() { super(1300, 800); }
    private Stage stage() { return (Stage)Gdx.input.getInputProcessor(); }
    private Actor find(Group group, String text) {
        for (Actor actor : group.getChildren()) {
            if (actor instanceof TextButton && ((TextButton)actor).getText().toString().equals(text)) return actor;
            if (actor instanceof Group) { Actor found = find((Group)actor, text); if (found != null) return found; }
        }
        return null;
    }
    private void click(String text) {
        Actor button = find(stage().getRoot(), text);
        if (button == null || ((TextButton)button).isDisabled()) throw new AssertionError("Unavailable control: " + text);
        button.fire(new ChangeEvent());
    }
    private void check() {
        Actor content = stage().getRoot().findActor("bounded-content");
        if (content == null) throw new AssertionError("Expected a bounded page");
        Vector2 origin = content.localToStageCoordinates(new Vector2());
        inspect(content, origin.x, origin.y, content.getWidth(), content.getHeight());
    }
    private void inspect(Actor actor, float x, float y, float width, float height) {
        Vector2 p = actor.localToStageCoordinates(new Vector2());
        if (p.x < x - 1 || p.y < y - 1 || p.x + actor.getWidth() > x + width + 1 || p.y + actor.getHeight() > y + height + 1) {
            throw new AssertionError("Overflow: " + actor + " at " + p + " size " + actor.getWidth() + "x" + actor.getHeight()
                + " outside " + x + "," + y + " " + width + "x" + height);
        }
        if (actor instanceof Label && ((Label)actor).getPrefHeight() > actor.getHeight() + 1) {
            throw new AssertionError("Clipped text: " + ((Label)actor).getText());
        }
        if (actor instanceof Group) for (Actor child : ((Group)actor).getChildren()) inspect(child, x, y, width, height);
    }
    private void snapshot(String id) {
        Pixmap p = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        PixmapIO.writePNG(new FileHandle("../docs/menus/" + id + ".png"), p, -1, true); p.dispose();
        System.out.println("Checked " + id);
    }
    private void pages(MenuId id, String... titles) {
        steps.add(() -> controller.root(id));
        for (String title : titles) {
            steps.add(() -> click(title));
            steps.add(this::check);
        }
    }
    private void addChecks(boolean large) {
        steps.add(() -> {
            controller.settings.apply(false, true, large, true);
            Gdx.graphics.setWindowedMode(large ? 900 : 1300, large ? 600 : 800);
        });
        pages(MenuId.CHARACTER, "Overview", "Abilities", "Training", "Background");
        steps.add(() -> click("Overview"));
        steps.add(() -> click("Next >"));
        steps.add(() -> {
            if (!((TextButton)find(stage().getRoot(), "Abilities")).isChecked()) throw new AssertionError("Next page failed");
        });
        steps.add(() -> { check(); snapshot(large ? "character-abilities-small" : "character-abilities"); });
        steps.add(() -> {
            Actor stat = find(stage().getRoot(), "Strength   12   [?]");
            stage().setKeyboardFocus(stat);
        });
        steps.add(() -> {
            Actor help = stage().getRoot().findActor("stat-tooltip");
            if (help == null) throw new AssertionError("Missing keyboard tooltip");
            inspect(help, 0, 0, stage().getWidth(), stage().getHeight());
            snapshot(large ? "stat-tooltip-small" : "stat-tooltip");
            stage().keyDown(Input.Keys.ESCAPE);
            if (stage().getRoot().findActor("stat-tooltip") != null) throw new AssertionError("Escape did not dismiss tooltip");
        });
        steps.add(() -> {
            stage().setKeyboardFocus(null);
            Actor stat = find(stage().getRoot(), "Charisma   8   [?]");
            InputEvent hover = new InputEvent(); hover.setType(InputEvent.Type.enter); hover.setPointer(-1);
            stat.fire(hover);
            Actor help = stage().getRoot().findActor("stat-tooltip");
            if (help == null) throw new AssertionError("Missing hover tooltip");
            inspect(help, 0, 0, stage().getWidth(), stage().getHeight());
            InputEvent exit = new InputEvent(); exit.setType(InputEvent.Type.exit); exit.setPointer(-1); stat.fire(exit);
            if (stage().getRoot().findActor("stat-tooltip") != null) throw new AssertionError("Hover tooltip did not close");
        });
        pages(MenuId.INVENTORY, "Equipment", "Supplies & belongings");
        pages(MenuId.SETTINGS, "Display", "Interface", "Controls", "Audio");
        pages(MenuId.JOURNAL, "Objectives", "Travel ledger", "Frontier lore");
        steps.add(() -> click("Travel ledger"));
        steps.add(() -> click("Older entry >"));
        steps.add(() -> { check(); snapshot(large ? "journal-ledger-small" : "journal-ledger"); });
        steps.add(() -> controller.root(MenuId.CREATE));
        steps.add(() -> {
            TextField field = findField(stage().getRoot());
            stage().setKeyboardFocus(field);
            for (char ch : "Mara Voss".toCharArray()) stage().keyTyped(ch);
        });
        for (int i = 0; i < 4; i++) {
            steps.add(this::check);
            steps.add(() -> click("Next   >"));
        }
        steps.add(this::check);
        steps.add(() -> click("Sign the ledger   >"));
    }
    private TextField findField(Group group) {
        for (Actor actor : group.getChildren()) {
            if (actor instanceof TextField) return (TextField)actor;
            if (actor instanceof Group) { TextField found = findField((Group)actor); if (found != null) return found; }
        }
        return null;
    }
    @Override public void render() {
        super.render();
        try {
            java.lang.reflect.Field loaded = Main.class.getDeclaredField("postLoadedComplete"); loaded.setAccessible(true);
            if (!loaded.getBoolean(this)) return;
            if (controller == null) {
                java.lang.reflect.Field menus = Main.class.getDeclaredField("menus"); menus.setAccessible(true);
                controller = (MenuController)menus.get(this);
                for (boolean large : new boolean[] {false, true}) {
                    steps.add(() -> {
                        CharacterRecord c = new CharacterRecord(); c.name = "WWWWWWWWWWWWWWWWWWWWWWWWWWWW"; c.standardArray();
                        controller.begin(c);
                        controller.journey.log.clear();
                        for (int i = 0; i < 100; i++) controller.journey.log.add("Day " + i + " - " + "The road remembers. ".repeat(22));
                    });
                    addChecks(large);
                }
            }
            if (++frame % 6 != 0) return;
            int index = frame / 6 - 1;
            if (index == steps.size()) { System.out.println("MENU_LAYOUT_SMOKE_PASSED"); Gdx.app.exit(); }
            else steps.get(index).run();
        } catch (Throwable failure) { failure.printStackTrace(); Gdx.app.exit(); System.exit(1); }
    }
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Corovaner menu layout checks"); config.setWindowedMode(1300, 800); config.disableAudio(true);
        config.setPreferencesConfig(System.getProperty("java.io.tmpdir") + "/corovaner-layout-" + System.currentTimeMillis(), Files.FileType.Absolute);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new MenuLayoutSmoke(), config);
    }
}
