package io.github.TorenDropProject;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import io.github.TorenDropProject.entities.components.*;
import io.github.TorenDropProject.entities.systems.InputSystem;
import io.github.TorenDropProject.menus.*;
import io.github.TorenDropProject.screens.BattleScreen;
import io.github.TorenDropProject.world.*;
import java.util.Arrays;

/** Desktop integration check; run from assets after building the runnable JAR. */
public class MapSmoke extends Main {
    private final String[] areas = {"forest", "quarry", "graveyard"};
    private int frame;
    private BattleScreen battle;
    private MenuController menus;
    private WorldScene previous;
    private float[] movedPosition;

    public MapSmoke() { super(1300, 800); }

    private WorldScene scene() throws Exception {
        java.lang.reflect.Field field = BattleScreen.class.getDeclaredField("scene");
        field.setAccessible(true);
        return (WorldScene)field.get(battle);
    }
    private Actor find(Group group, String text) {
        for (Actor actor : group.getChildren()) {
            if (actor instanceof TextButton && ((TextButton)actor).getText().toString().equals(text)) return actor;
            if (actor instanceof Group) {
                Actor result = find((Group)actor, text);
                if (result != null) return result;
            }
        }
        return null;
    }
    private void click(String text) {
        TextButton button = (TextButton)find(((Stage)Gdx.input.getInputProcessor()).getRoot(), text);
        check(button != null && !button.isDisabled(), "Unavailable control: " + text);
        button.fire(new ChangeEvent());
    }
    private void travel(String area) {
        click(menus.catalog.destination(area).name);
        click("Travel this road   >");
        click("Confirm");
        check(area.equals(menus.journey.location), "Travel did not arrive at " + area);
    }
    private void enter(String area) throws Exception {
        click("Enter local area   >");
        WorldScene scene = scene();
        check(scene.session.map == assetManager.get(menus.catalog.destination(area).worldMap, WorldMap.class), "Wrong map");
        check(scene.session.engine.getEntities().size() == 3, "Wrong actor count");
        check(scene.session.map.chunkCount() > 0, "Missing terrain");
        if (previous != null && previous != scene) check(previous.session.engine.getEntities().size() == 0, "Old session leaked");
        previous = scene;
    }
    private void move() throws Exception {
        WorldScene scene = scene();
        scene.session.engine.getSystem(InputSystem.class).setProcessing(false);
        WorldTransformComponent position = scene.session.engine.getEntities().first().getComponent(WorldTransformComponent.class);
        MovementIntentComponent intent = scene.session.engine.getEntities().first().getComponent(MovementIntentComponent.class);
        WorldTransformComponent npc = scene.session.engine.getEntities().get(1).getComponent(WorldTransformComponent.class);
        float npcX = npc.x, npcZ = npc.z;
        float[] start = battle.capturePosition();
        for (int[] direction : new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
            intent.x = direction[0]; intent.z = direction[1];
            scene.session.update(0.1f);
            if (position.x != start[0] || position.z != start[2]) break;
        }
        intent.x = intent.z = 0;
        movedPosition = battle.capturePosition();
        check(!Arrays.equals(start, movedPosition), "Player cannot move from spawn");
        check(npc.x == npcX && npc.z == npcZ, "NPC moved with player");
    }
    private void capture(String name) {
        Pixmap pixels = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        PixmapIO.writePNG(new FileHandle("../docs/maps/" + name + ".png"), pixels, -1, true);
        pixels.dispose();
    }
    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    @Override public void render() {
        super.render();
        try {
            java.lang.reflect.Field loaded = Main.class.getDeclaredField("postLoadedComplete");
            loaded.setAccessible(true);
            if (!loaded.getBoolean(this)) return;
            if (menus == null) {
                java.lang.reflect.Field controller = Main.class.getDeclaredField("menus");
                controller.setAccessible(true);
                menus = (MenuController)controller.get(this);
            }
            if (frame == 0) {
                CharacterRecord character = new CharacterRecord(); character.name = "Map Scout";
                menus.begin(character);
                battle = (BattleScreen)menus.screens.getGameScreen("BattleScreen");
                travel("pass"); enter("pass");
                menus.open(MenuId.TRAVEL);
            }
            if (frame >= 10 && frame < 100) {
                int index = (frame - 10) / 30, step = (frame - 10) % 30;
                String area = areas[index];
                if (step == 0) travel(area);
                if (step == 4) { capture("travel-" + area); enter(area); }
                if (step == 8) {
                    Vector3 spawn = scene().session.map.spawns.first();
                    check(Arrays.equals(new float[] {spawn.x, spawn.y, spawn.z}, battle.capturePosition()), "Cross-map position contamination");
                    move();
                }
                if (step == 14) capture("in-game-" + area);
                if (step == 18) {
                    menus.pause();
                    check(Arrays.equals(movedPosition, menus.journey.positionFor(area)), "Position not captured on pause");
                    menus.save(index + 1);
                    menus.back();
                    check(Arrays.equals(movedPosition, battle.capturePosition()), "Resume changed position");
                    menus.open(MenuId.TRAVEL);
                }
                if (step == 22) { travel("pass"); enter("pass"); menus.open(MenuId.TRAVEL); }
            }
            if (frame == 102) {
                menus.load(3); enter("graveyard");
                check(Arrays.equals(movedPosition, battle.capturePosition()), "Load restored wrong area's position");
                menus.open(MenuId.TRAVEL);
                menus.journey.supplies = 20;
                travel("pass"); enter("pass"); menus.open(MenuId.TRAVEL);
                travel("graveyard"); enter("graveyard");
                check(Arrays.equals(movedPosition, battle.capturePosition()), "Revisit lost position");
                Gdx.graphics.setWindowedMode(900, 600);
            }
            if (frame == 112) {
                capture("in-game-graveyard-resized");
                // Replace a live journey; hide must capture into the old owner, never the new save.
                CharacterRecord character = new CharacterRecord(); character.name = "Fresh Scout";
                menus.begin(character);
                check(menus.journey.areaPositions.isEmpty(), "New journey inherited old positions");
                travel("pass"); travel("graveyard"); enter("graveyard");
                Vector3 spawn = scene().session.map.spawns.first();
                check(Arrays.equals(new float[] {spawn.x, spawn.y, spawn.z}, battle.capturePosition()), "New journey did not reset scene");
                menus.open(MenuId.TRAVEL);
                capture("travel-resized");
            }
            if (frame == 120) {
                capture("travel-resized");
                // A valid old save can become unwalkable after a map edit.
                menus.journey.rememberPosition("graveyard", new float[] {-100, 0, -100});
                menus.save(3); menus.load(3); enter("graveyard");
                Vector3 spawn = scene().session.map.spawns.first();
                check(Arrays.equals(new float[] {spawn.x, spawn.y, spawn.z}, battle.capturePosition()), "Invalid saved position did not fall back to spawn");
                System.out.println("MAP_SMOKE_PASSED: all maps, UI travel, movement, pause, save/load, revisits, resize, session disposal, new journey and spawn fallback");
                Gdx.app.exit();
            }
            frame++;
        } catch (Throwable failure) {
            failure.printStackTrace();
            Gdx.app.exit();
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Corovaner map integration smoke"); config.setWindowedMode(1300, 800); config.disableAudio(true);
        config.setPreferencesConfig(System.getProperty("java.io.tmpdir") + "/corovaner-map-smoke-" + System.currentTimeMillis(), Files.FileType.Absolute);
        config.setForegroundFPS(30);
        new Lwjgl3Application(new MapSmoke(), config);
    }
}
