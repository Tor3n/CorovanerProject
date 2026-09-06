package io.github.TorenDropProject;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import io.github.TorenDropProject.entities.components.*;
import io.github.TorenDropProject.menus.*;
import io.github.TorenDropProject.screens.BattleScreen;
import io.github.TorenDropProject.world.*;

/** Exercises world input through the installed UI/world multiplexer with isolated preferences. */
public class BattleInputSmoke extends Main {
    private MenuController menus;
    private BattleScreen battle;
    private WorldScene scene;
    private int frame;
    private final String[] areas = {"mercy", "relay", "salt"};
    private Vector3 destination;
    private Entity commanded;
    private float otherX, otherZ;
    public BattleInputSmoke() { super(1300, 800); }
    private static Object field(Object object, Class<?> type, String name) throws Exception {
        java.lang.reflect.Field field = type.getDeclaredField(name); field.setAccessible(true); return field.get(object);
    }
    private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    private Vector3 screen(Vector3 point) {
        Vector3 p = scene.cameraRig.camera().project(new Vector3(point));
        p.y = Gdx.graphics.getHeight() - p.y; return p;
    }
    private void click(Vector3 world) {
        Vector3 p = screen(world);
        Gdx.input.getInputProcessor().touchDown(Math.round(p.x), Math.round(p.y), 0, Input.Buttons.LEFT);
        Gdx.input.getInputProcessor().touchUp(Math.round(p.x), Math.round(p.y), 0, Input.Buttons.LEFT);
    }
    private void capture(String name) {
        Pixmap image = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        PixmapIO.writePNG(new FileHandle("../docs/maps/" + name + ".png"), image, -1, true); image.dispose();
    }
    @Override public void render() {
        super.render();
        try {
            if (!(Boolean)field(this, Main.class, "postLoadedComplete")) return;
            if (menus == null) {
                menus = (MenuController)field(this, Main.class, "menus");
                CharacterRecord character = new CharacterRecord(); character.name = "Wasteland Scout"; menus.begin(character);
                battle = (BattleScreen)menus.screens.getGameScreen("BattleScreen");
            }
            int areaIndex = frame / 160, step = frame % 160;
            if (areaIndex == areas.length) { System.out.println("BATTLE_INPUT_SMOKE_PASSED"); Gdx.app.exit(); return; }
            String area = areas[areaIndex];
            if (step == 0) {
                if (areaIndex > 0) {
                    menus.open(MenuId.TRAVEL);
                    if (area.equals("relay")) { menus.journey.travel(menus.catalog, "pass"); menus.journey.travel(menus.catalog, "relay"); }
                    else menus.journey.travel(menus.catalog, "salt");
                }
                menus.enterWorld(); scene = (WorldScene)field(battle, BattleScreen.class, "scene");
                check(scene.session.map == assetManager.get(menus.catalog.destination(area).worldMap, WorldMap.class), "Wrong destination map");
            }
            if (step == 5) {
                commanded = scene.session.engine.getEntities().get(1);
                click(commanded.getComponent(ModelComponent.class).worldBounds.getCenter(new Vector3()));
                check(scene.session.selected() == commanded, "Mouse selection failed");
            }
            if (step == 10) {
                WorldTransformComponent p = commanded.getComponent(WorldTransformComponent.class);
                WorldTransformComponent other = scene.session.engine.getEntities().first().getComponent(WorldTransformComponent.class);
                otherX = other.x; otherZ = other.z;
                destination = null;
                for (int[] direction : new int[][] {{2, 0}, {0, -2}, {-2, 0}, {0, 2}}) {
                    Vector3 target = new Vector3(p.x + direction[0], p.y, p.z + direction[1]);
                    if (new GridPathfinder(scene.session.map.grid).find(p.x, p.y, p.z, target.x, target.z, .18f).notEmpty()) {
                        click(target);
                        if (commanded.getComponent(NavigationComponent.class).active()) { destination = target; break; }
                    }
                }
                check(destination != null, "Ground click did not start movement");
                // Top HUD background must swallow world commands even when no button handles it.
                int count = commanded.getComponent(NavigationComponent.class).waypoints.size;
                Gdx.input.getInputProcessor().touchDown(800, 40, 0, Input.Buttons.LEFT);
                Gdx.input.getInputProcessor().touchUp(800, 40, 0, Input.Buttons.LEFT);
                check(commanded.getComponent(NavigationComponent.class).waypoints.size == count, "HUD click replaced route");
                capture("mouse-route-" + area);
            }
            if (step == 100) {
                WorldTransformComponent p = commanded.getComponent(WorldTransformComponent.class);
                check(new Vector3(p.x, p.y, p.z).dst(destination) < .03f, "Click movement did not arrive");
                WorldTransformComponent other = scene.session.engine.getEntities().first().getComponent(WorldTransformComponent.class);
                check(other.x == otherX && other.z == otherZ, "Unselected actor moved");
                // Move to the central landmark for a review capture using the same path command.
                Vector3 center = area.equals("mercy") ? new Vector3(32.5f, 0, -29.5f)
                    : area.equals("relay") ? new Vector3(30.5f, 0, -30.5f) : new Vector3(31.5f, 0, -34.5f);
                check(scene.session.moveSelected(center), "Landmark is unreachable");
                for (int i = 0; i < 800; i++) scene.session.update(.1f);
                scene.cameraRig.zoom(.35f);
            }
            if (step == 110) {
                capture("in-game-" + area);
                WorldTransformComponent p = commanded.getComponent(WorldTransformComponent.class);
                check(scene.session.moveSelected(new Vector3(p.x + 1, p.y, p.z)), "Pause test route unavailable");
                menus.pause();
                check(!commanded.getComponent(NavigationComponent.class).active(), "Menu did not cancel movement");
                menus.back();
                Gdx.graphics.setWindowedMode(900, 600);
            }
            if (step == 125) {
                click(commanded.getComponent(ModelComponent.class).worldBounds.getCenter(new Vector3()));
                check(scene.session.selected() == commanded, "Selection failed after resize/zoom");
                capture("in-game-" + area + "-small");
            }
            if (step == 145) Gdx.graphics.setWindowedMode(1300, 800);
            frame++;
        } catch (Throwable failure) { failure.printStackTrace(); Gdx.app.exit(); System.exit(1); }
    }
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Battle map input smoke"); config.setWindowedMode(1300, 800); config.disableAudio(true);
        config.setForegroundFPS(60);
        config.setPreferencesConfig(System.getProperty("java.io.tmpdir") + "/corovaner-input-smoke-" + System.currentTimeMillis(), Files.FileType.Absolute);
        new Lwjgl3Application(new BattleInputSmoke(), config);
    }
}
