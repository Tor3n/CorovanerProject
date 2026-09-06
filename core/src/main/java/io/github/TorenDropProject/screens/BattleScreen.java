package io.github.TorenDropProject.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import io.github.TorenDropProject.entities.components.CollisionComponent;
import io.github.TorenDropProject.entities.components.PlayerControlledComponent;
import io.github.TorenDropProject.entities.components.WorldTransformComponent;
import io.github.TorenDropProject.entities.systems.ModelTransformSystem;
import io.github.TorenDropProject.menus.*;
import io.github.TorenDropProject.screens.GUIs.BattleScreenGUI;
import io.github.TorenDropProject.world.WorldScene;
import io.github.TorenDropProject.world.WorldInputController;
import io.github.TorenDropProject.world.WorldSceneFactory;

/** Coordinates a world scene and screen-space UI. Menus suspend the scene through screen navigation. */
public final class BattleScreen implements GameScreen {
    private final WorldSceneFactory scenes;
    private WorldScene scene;
    private Journey sceneJourney;
    private String sceneLocation;
    private final MenuController menus;
    private final BattleScreenGUI gui;
    private boolean applicationPaused;

    public BattleScreen(WorldSceneFactory scenes, MenuController menus, MenuTheme theme) {
        this.scenes = scenes; this.menus = menus;
        gui = new BattleScreenGUI(menus, theme);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    @Override public void show() {
        Journey journey = menus.journey;
        if (journey == null || !menus.catalog.destination(journey.location).hasLocalArea()) {
            throw new IllegalStateException("There is no local area to enter.");
        }
        if (sceneJourney != journey || !journey.location.equals(sceneLocation)) {
            WorldScene next = scenes.create(menus.catalog.destination(journey.location).worldMap, true);
            if (scene != null) scene.dispose();
            scene = next;
            sceneJourney = journey;
            sceneLocation = journey.location;
            restorePosition(journey.positionFor(sceneLocation));
        }
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gui.setWorldMessage("Click a scout to select; click the ground to move.");
        gui.show(new WorldInputController(scene, () -> !applicationPaused && !gui.hasKeyboardFocus(),
            gui::containsScreenPoint, gui::setWorldMessage));
    }
    @Override public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { menus.pause(); return; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) { menus.open(MenuId.CHARACTER); return; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) { menus.open(MenuId.INVENTORY); return; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.J)) { menus.open(MenuId.JOURNAL); return; }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) { menus.open(MenuId.TRAVEL); return; }
        boolean paused = applicationPaused || gui.hasKeyboardFocus();
        if (!paused) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) scene.session.selectNext();
            if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) scene.cameraRig.zoom(-delta * 0.5f);
            if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) scene.cameraRig.zoom(delta * 0.5f);
        }
        scene.render(delta, paused);
        gui.draw(delta);
    }
    private Entity player() {
        return scene.session.engine.getEntitiesFor(Family.all(PlayerControlledComponent.class,
            WorldTransformComponent.class, CollisionComponent.class).get()).first();
    }
    public float[] capturePosition() {
        WorldTransformComponent position = player().getComponent(WorldTransformComponent.class);
        return new float[] {position.x, position.y, position.z};
    }
    private void restorePosition(float[] saved) {
        Vector3 spawn = scene.session.map.spawns.first();
        float x = saved == null ? spawn.x : saved[0];
        float y = saved == null ? spawn.y : saved[1];
        float z = saved == null ? spawn.z : saved[2];
        Entity player = player();
        float radius = player.getComponent(CollisionComponent.class).radius;
        if (!scene.session.map.grid.canStand(x, z, radius, y)) {
            // Authored collision may change between saves; return to this area's safe entrance.
            x = spawn.x; y = spawn.y; z = spawn.z;
        }
        scene.session.stopActors();
        WorldTransformComponent position = player.getComponent(WorldTransformComponent.class);
        position.x = x; position.y = y; position.z = z; position.yawDegrees = 0;
        scene.session.engine.getSystem(ModelTransformSystem.class).update(0);
    }
    @Override public void resize(int width, int height) {
        if (scene != null) scene.resize(width, height);
        gui.resize(width, height);
    }
    @Override public void hide() {
        if (scene != null) {
            scene.session.stopActors();
            // Capture into the journey that owns this scene, even if a load replaced menus.journey.
            sceneJourney.rememberPosition(sceneLocation, capturePosition());
        }
        gui.hide();
    }
    @Override public void pause() { applicationPaused = true; if (scene != null) scene.session.stopActors(); }
    @Override public void resume() { applicationPaused = false; }
    @Override public void dispose() { gui.dispose(); if (scene != null) scene.dispose(); }
}
