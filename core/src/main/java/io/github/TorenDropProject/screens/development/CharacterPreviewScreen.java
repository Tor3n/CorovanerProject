package io.github.TorenDropProject.screens.development;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.TorenDropProject.screens.GameScreen;
import io.github.TorenDropProject.world.WorldScene;
import io.github.TorenDropProject.entities.components.AnimationComponent;
import io.github.TorenDropProject.entities.components.ModelComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;

/** Development controls around the exact same WorldScene used by BattleScreen. */
public final class CharacterPreviewScreen implements GameScreen {
    private final WorldScene scene;
    private final ScreenViewport uiViewport = new ScreenViewport();
    private final SpriteBatch uiBatch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private boolean turntable;
    public CharacterPreviewScreen(WorldScene scene) {
        this.scene = scene;
        for (Entity actor : scene.session.engine.getEntitiesFor(Family.all(AnimationComponent.class, ModelComponent.class).get())) {
            AnimationComponent animation = actor.getComponent(AnimationComponent.class);
            animation.previewClip = animation.clip = "neutral_idle";
            animation.controller.setAnimation(animation.clip, -1);
            animation.controller.update(0);
            hideEquipment(actor.getComponent(ModelComponent.class).instance.getNode("Equipment_Rifle", true));
        }
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    private static void hideEquipment(Node node) {
        if (node == null) return;
        for (NodePart part : node.parts) part.enabled = false;
        for (Node child : node.getChildren()) hideEquipment(child);
    }
    @Override public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) turntable = !turntable;
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) scene.session.followNext();
        AnimationComponent animation = scene.session.followed().getComponent(AnimationComponent.class);
        if (animation != null) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) animation.previewClip = "neutral_idle";
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) animation.previewClip = "neutral_walk";
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) animation.previewClip = "neutral_run";
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) animation.paused = !animation.paused;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) scene.cameraRig.zoom(-delta * 0.5f);
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) scene.cameraRig.zoom(delta * 0.5f);
        if (turntable) scene.session.rotatePreview(delta * 35f);
        scene.render(delta, false);
        uiViewport.apply();
        uiBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        uiBatch.begin();
        font.draw(uiBatch, "Shared world preview | Space: rotate | Tab: follow next | +/-: zoom", 16, uiViewport.getWorldHeight() - 18);
        if (animation != null) font.draw(uiBatch, "Unarmed preview | I: neutral idle | W: walk | R: run | P: pause | "
            + animation.clip + (animation.paused ? " (paused)" : ""), 16, uiViewport.getWorldHeight() - 40);
        uiBatch.end();
    }
    @Override public void resize(int width, int height) { scene.resize(width, height); uiViewport.update(width, Math.max(1, height), true); }
    @Override public void show() { }
    @Override public void hide() { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void dispose() { scene.dispose(); uiBatch.dispose(); font.dispose(); }
}
