package io.github.TorenDropProject;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.TorenDropProject.menus.CharacterRecord;
import io.github.TorenDropProject.menus.Journey;
import io.github.TorenDropProject.menus.MenuId;
import io.github.TorenDropProject.menus.TerminalMenuAssets;
import io.github.TorenDropProject.screens.GUIs.menus.TerminalButton;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Configuration;

/** Actual startup, GL rendering and input at several sizes, with isolated preferences. */
public final class TerminalMenuSmoke extends Main {
    private final long started = TimeUtils.nanoTime();
    private int frames;
    private int step;
    private Journey originalJourney;

    private TerminalMenuSmoke() { super(1920, 1080); }

    public static void main(String[] args) throws Exception {
        if (Boolean.getBoolean("terminalSmoke.headless")) {
            // GLFW's null platform uses a real OSMesa context without a display server.
            GLFW.glfwInitHint(GLFW.GLFW_PLATFORM, GLFW.GLFW_PLATFORM_NULL);
            Configuration.OPENGL_EXPLICIT_INIT.set(true);
            GL.create(GLFW::glfwGetProcAddress);
        }
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Corovaner terminal smoke");
        config.setWindowedMode(1920, 1080);
        config.setInitialVisible(false);
        config.setWindowPosition(0, 0);
        config.disableAudio(true);
        config.useVsync(false);
        config.setForegroundFPS(60);
        config.setPreferencesConfig(java.nio.file.Files.createTempDirectory("corovaner-terminal-").toString(), Files.FileType.Absolute);
        new Lwjgl3Application(new TerminalMenuSmoke(), config);
    }

    @Override public void render() {
        require(TimeUtils.timeSinceNanos(started) < 180_000_000_000L, "Terminal smoke timed out at step " + step);
        super.render();
        if (!postLoadedComplete || ++frames % 30 != 0) return;
        switch (step++) {
            case 0:
                require(button("Continue journey").isDisabled(), "Fresh game must disable Continue");
                pointer("Continue journey", true);
                require(mainVisible(), "Disabled Continue navigated");
                pointer("New journey", false);
                stage().setKeyboardFocus(button("Settings"));
                require(litLamps() == 1 && button("New journey").isLampVisible(), "Hover must take precedence over another focused button");
                capture("terminal-1920.png");
                pointer("Credits", true);
                break;
            case 1:
                require(!mainVisible(), "Credits did not open");
                stage().keyDown(Input.Keys.ESCAPE);
                break;
            case 2:
                require(mainVisible(), "Escape did not return to the terminal");
                clearPointer();
                stage().setKeyboardFocus(null);
                stage().keyDown(Input.Keys.TAB);
                require(stage().getKeyboardFocus() == button("New journey"), "Tab must skip disabled Continue");
                require(button("New journey").isLampVisible() && litLamps() == 1, "Keyboard focus lamp missing");
                stage().keyDown(Input.Keys.ENTER);
                break;
            case 3:
                require(!mainVisible(), "Keyboard New journey did not open creation");
                menus.back();
                pointer("Load journey", true);
                break;
            case 4:
                require(!mainVisible() && !menus.saveMode, "Load journey did not open load slots");
                menus.back();
                pointer("Settings", true);
                break;
            case 5:
                require(!mainVisible(), "Settings did not open");
                CharacterRecord character = new CharacterRecord();
                character.name = "Terminal test";
                character.standardArray();
                originalJourney = new Journey(character);
                originalJourney.validate(menus.catalog);
                menus.journey = originalJourney;
                menus.settings.largerText = true;
                menus.root(MenuId.MAIN);
                resizeWindow(1280, 720);
                break;
            case 6:
                require(!button("Continue journey").isDisabled(), "Returning journey must enable Continue");
                pointer("Continue journey", false);
                capture("terminal-1280-large-text.png");
                pointer("Continue journey", true);
                break;
            case 7:
                require(!mainVisible() && menus.journey == originalJourney, "Continue did not resume the journey");
                menus.settings.largerText = false;
                menus.root(MenuId.MAIN);
                pointer("New journey", true);
                require(dialog() != null && menus.journey == originalJourney, "New journey must confirm before replacing an existing one");
                break;
            case 8:
                stage().keyDown(Input.Keys.ESCAPE);
                break;
            case 9:
                require(dialog() == null && menus.journey == originalJourney, "Cancel must preserve the journey");
                resizeWindow(2560, 1080);
                break;
            case 10:
                pointer("Settings", false);
                capture("terminal-2560.png");
                require(Math.abs(composition().getX() - 320) < 1, "Expected 320-unit side margins");
                stage().touchDown(100, 500, 0, Input.Buttons.LEFT);
                stage().touchUp(100, 500, 0, Input.Buttons.LEFT);
                require(mainVisible(), "Decorative margin activated a control");
                pointer("Settings", true);
                break;
            case 11:
                require(!mainVisible(), "Ultrawide click coordinates did not reach Settings");
                menus.back();
                resizeWindow(900, 600);
                break;
            case 12:
                checkLayout();
                pointer("Quit to desktop", true);
                require(dialog() != null, "Quit must show confirmation");
                capture("terminal-quit-dialog.png");
                break;
            case 13:
                stage().keyDown(Input.Keys.ESCAPE);
                break;
            case 14:
                require(dialog() == null && mainVisible(), "Quit cancellation failed");
                resizeWindow(2560, 1440);
                break;
            case 15:
                checkLayout();
                resizeWindow(3840, 2160);
                break;
            case 16:
                checkLayout();
                resizeWindow(1920, 1080);
                break;
            case 17:
                checkLayout();
                menus.settings.decorations = false;
                resizeWindow(2560, 1080);
                break;
            case 18:
                checkLayout();
                menus.root(MenuId.SETTINGS);
                menus.root(MenuId.MAIN);
                require(mainVisible(), "Reused terminal did not restore");
                require(assetManager.isLoaded(TerminalMenuAssets.PANEL), "A shared terminal asset was disposed during navigation");
                require(Gdx.gl.glGetError() == GL20.GL_NO_ERROR, "OpenGL error");
                Gdx.files.absolute(output() + "/result.txt").writeString("PASS: real Main startup; independent artwork; disabled/enabled Continue; six routes; hover/focus precedence; keyboard activation; confirmations/cancel; resize and click mapping at 1920x1080, 1280x720, 2560x1080, 900x600, 2560x1440, 3840x2160; larger text; hidden decorations; shared asset lifetime.\n", false);
                System.out.println("TERMINAL_MENU_SMOKE_PASSED");
                Gdx.app.exit();
                break;
            default: break;
        }
    }

    private Stage stage() { return (Stage)Gdx.input.getInputProcessor(); }
    private Table composition() { return stage().getRoot().findActor("terminal-composition"); }
    private boolean mainVisible() { return composition() != null; }
    private TerminalButton button(String name) {
        TerminalButton button = stage().getRoot().findActor(name);
        require(button != null, "Missing terminal button " + name);
        return button;
    }
    private Dialog dialog() {
        for (Actor actor : stage().getActors()) if (actor instanceof Dialog) return (Dialog)actor;
        return null;
    }
    private int litLamps() {
        int count = 0;
        for (Actor actor : composition().getChildren()) if (actor instanceof TerminalButton
            && ((TerminalButton)actor).isLampVisible()) count++;
        return count;
    }
    private void clearPointer() {
        stage().mouseMoved(Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 50);
        stage().act(0);
    }
    private void pointer(String name, boolean click) {
        TerminalButton button = button(name);
        composition().validate();
        Vector2 p = button.localToStageCoordinates(new Vector2(button.getWidth() / 2, button.getHeight() / 2));
        stage().stageToScreenCoordinates(p);
        Stage target = stage();
        target.mouseMoved(Math.round(p.x), Math.round(p.y));
        target.act(0);
        if (click) {
            target.touchDown(Math.round(p.x), Math.round(p.y), 0, Input.Buttons.LEFT);
            target.touchUp(Math.round(p.x), Math.round(p.y), 0, Input.Buttons.LEFT);
        }
    }
    private void checkLayout() {
        Table composition = composition();
        require(composition != null, "Terminal is not visible");
        composition.validate();
        require(composition.getWidth() == 1920 && composition.getHeight() == 1080, "Composition was stretched");
        Vector2 origin = composition.localToStageCoordinates(new Vector2());
        require(Math.abs(origin.x * 2 + 1920 - stage().getWidth()) < 2, "Horizontal centering failed");
        require(Math.abs(origin.y * 2 + 1080 - stage().getHeight()) < 2, "Vertical centering failed");
        int count = 0;
        for (Actor actor : composition.getChildren()) if (actor instanceof TerminalButton) {
            TerminalButton button = (TerminalButton)actor;
            button.validate();
            require(button.getLabel().getPrefWidth() <= button.getLabel().getWidth() + 1, "Clipped label: " + button.getText());
            Vector2 p = button.localToStageCoordinates(new Vector2(240, 50));
            if (dialog() == null) {
                Actor hit = stage().hit(p.x, p.y, true);
                require(hit != null && (hit == button || hit.isDescendantOf(button)), "Painting intercepted input for " + button.getText());
            }
            count++;
        }
        require(count == 6, "Expected six menu actions");
    }
    private void resizeWindow(int width, int height) {
        require(Gdx.graphics.setWindowedMode(width, height), "Resize failed");
    }
    private void capture(String file) {
        checkLayout();
        stage().getViewport().apply();
        stage().draw();
        Pixmap pixels = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        try { PixmapIO.writePNG(Gdx.files.absolute(output() + "/" + file), pixels, -1, true); }
        finally { pixels.dispose(); }
    }
    private static String output() { return System.getProperty("terminalSmoke.output"); }
    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
