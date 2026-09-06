package io.github.TorenDropProject;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import io.github.TorenDropProject.menus.*;

public class MenuSmoke extends Main {
    private int frame;
    private MenuController controller;
    private float[] pausedPosition;
    public MenuSmoke() { super(1300, 800); }
    private Stage stage() { return (Stage)Gdx.input.getInputProcessor(); }
    private Actor find(Group group, String text) {
        for (Actor actor : group.getChildren()) {
            if (actor instanceof TextButton && ((TextButton)actor).getText().toString().equals(text)) return actor;
            if (actor instanceof Group) { Actor found = find((Group)actor, text); if (found != null) return found; }
        }
        return null;
    }
    private TextField field(Group group) {
        for (Actor actor : group.getChildren()) {
            if (actor instanceof TextField) return (TextField)actor;
            if (actor instanceof Group) { TextField result = field((Group)actor); if (result != null) return result; }
        }
        return null;
    }
    private void click(String text) {
        Actor button = find(stage().getRoot(), text);
        if (button == null) throw new IllegalStateException("Missing button: " + text);
        if (((TextButton)button).isDisabled()) throw new IllegalStateException("Disabled button: " + text);
        button.fire(new ChangeEvent());
    }
    private void snapshot(String id) {
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        PixmapIO.writePNG(new FileHandle("../docs/menus/" + id + ".png"), pixmap, -1, true);
        pixmap.dispose();
        System.out.println("Captured " + id);
    }
    @Override public void render() {
        super.render();
        try {
            java.lang.reflect.Field loaded = Main.class.getDeclaredField("postLoadedComplete"); loaded.setAccessible(true);
            if (!loaded.getBoolean(this)) return;
            java.lang.reflect.Field controllerField = Main.class.getDeclaredField("menus"); controllerField.setAccessible(true);
            controller = (MenuController)controllerField.get(this);
        } catch (Exception failure) { throw new RuntimeException(failure); }
        try {
            switch (++frame) {
                case 20: snapshot("main-menu"); click("New journey"); break;
                case 25: stage().setKeyboardFocus(field(stage().getRoot())); for (char ch : "Mara Voss".toCharArray()) stage().keyTyped(ch); click("Next   >"); break;
                case 30: click("Tinker   /   d8"); click("Next   >"); break;
                case 35: click("Use standard array"); break;
                case 40: snapshot("character-creation"); click("Next   >"); break;
                case 43: click("Next   >"); break;
                case 45: snapshot("character-review"); click("Sign the ledger   >"); break;
                case 55: click("Blackwire Pass"); break;
                case 60: snapshot("travel"); click("Travel this road   >"); break;
                case 65:
                    Actor confirm = find(stage().getRoot(), "Confirm"); stage().setKeyboardFocus(confirm);
                    stage().keyDown(Input.Keys.ENTER); stage().keyUp(Input.Keys.ENTER); break;
                case 80:
                    if (!"pass".equals(controller.journey.location) || controller.journey.supplies != 10) throw new AssertionError("Travel failed");
                    click("Blackwire Pass"); break;
                case 85: click("Enter local area   >"); break;
                case 110: snapshot("battle-hud"); controller.pause(); pausedPosition = controller.journey.positionFor("pass"); break;
                case 130:
                    if (!java.util.Arrays.equals(pausedPosition, controller.journey.positionFor("pass"))) throw new AssertionError("Paused position changed");
                    snapshot("pause"); click("Character sheet"); break;
                case 140: snapshot("character-sheet"); click("Equipment"); break;
                case 145: click("Unequip"); break;
                case 150: snapshot("inventory"); controller.back(); controller.back(); click("Settings"); break;
                case 160: snapshot("settings"); click("Interface"); click("Larger body text   /   OFF"); click("Apply"); break;
                case 165: snapshot("settings-large-text"); click("Larger body text   /   ON"); click("Apply"); controller.back(); click("Save journey"); break;
                case 170: click("Save here"); break;
                case 180: snapshot("saves"); controller.back(); click("Load journey"); break;
                case 185: click("Load"); break;
                case 190: click("Confirm"); break;
                case 205:
                    if (!"Mara Voss".equals(controller.journey.character.name)) throw new AssertionError("Load failed");
                    click("Journal"); break;
                case 215: snapshot("journal"); Gdx.graphics.setWindowedMode(900, 600); break;
                case 225: snapshot("journal-small-window"); controller.back(); break;
                case 235: snapshot("travel-small-window"); controller.root(MenuId.MAIN); break;
                case 240: controller.open(MenuId.SETTINGS); break;
                case 245: stage().keyDown(Input.Keys.ESCAPE); stage().keyUp(Input.Keys.ESCAPE); break;
                case 250:
                    if (find(stage().getRoot(), "New journey") == null) throw new AssertionError("Escape did not return to title");
                    Gdx.app.exit(); System.out.println("MENU_SMOKE_PASSED"); break;
            }
        } catch (Throwable failure) { failure.printStackTrace(); Gdx.app.exit(); System.exit(1); }
    }
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Corovaner menu smoke"); config.setWindowedMode(1300,800); config.disableAudio(true);
        config.setPreferencesConfig(System.getProperty("java.io.tmpdir") + "/corovaner-menu-smoke-" + System.currentTimeMillis(), Files.FileType.Absolute);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new MenuSmoke(), config);
    }
}
