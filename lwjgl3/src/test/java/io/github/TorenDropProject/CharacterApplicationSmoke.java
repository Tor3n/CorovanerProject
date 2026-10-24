package io.github.TorenDropProject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import io.github.TorenDropProject.characters.CharacterDefinition;
import io.github.TorenDropProject.screens.development.CharacterPreviewScreen;
import io.github.TorenDropProject.entities.components.AnimationComponent;
import io.github.TorenDropProject.entities.components.ModelComponent;
import io.github.TorenDropProject.world.WorldScene;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

/** Exercises Main's real startup queue and preview screen without introducing production test hooks. */
public final class CharacterApplicationSmoke extends Main {
    private final long started = TimeUtils.nanoTime();
    private int previewFrames;

    public CharacterApplicationSmoke() { super(1100, 800); }

    public static void main(String[] args) {
        System.setProperty("corovaner.characterPreview", "true");
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Character application smoke");
        config.setWindowedMode(1100, 800);
        config.setInitialVisible(false);
        config.disableAudio(true);
        config.useVsync(false);
        config.setForegroundFPS(60);
        new Lwjgl3Application(new CharacterApplicationSmoke(), config);
    }

    @Override public void render() {
        require(TimeUtils.timeSinceNanos(started) < 60_000_000_000L, "Main startup/preview timed out");
        super.render();
        if (!postLoadedComplete) return;
        require(assetManager.isLoaded(CharacterDefinition.PROTAGONIST.modelPath, SceneAsset.class), "Main did not load the GLB SceneAsset");
        require(screenManager.getGameScreen("CharacterPreview") instanceof CharacterPreviewScreen, "Main did not register the preview screen");
        if (previewFrames == 0) verifyNeutralPreview();
        if (++previewFrames == 20) {
            // Exercise the reusable screen's hide/show and application lifecycle without replacing it.
            screenManager.setScreen(null);
            screenManager.setScreen(screenManager.getGameScreen("CharacterPreview"));
            super.pause();
            super.resume();
            super.resize(1100, 800);
        }
        if (previewFrames == 60) {
            require(Gdx.gl.glGetError() == GL20.GL_NO_ERROR, "OpenGL error in Main/CharacterPreviewScreen");
            Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 1100, 800);
            try {
                PixmapIO.writePNG(Gdx.files.absolute(System.getProperty("characterSmoke.output") + "/application-preview.png"), pixels, -1, true);
            } finally { pixels.dispose(); }
            Gdx.files.absolute(System.getProperty("characterSmoke.output") + "/application-result.txt").writeString(
                "PASS: Main startup, neutral unarmed preview on all actors, instance-only equipment visibility, 60 rendered frames, hide/show, pause/resume and resize.\n", false);
            System.out.println("Character application smoke PASS");
            Gdx.app.exit();
        }
    }

    private void verifyNeutralPreview() {
        try {
            java.lang.reflect.Field sceneField = CharacterPreviewScreen.class.getDeclaredField("scene");
            sceneField.setAccessible(true);
            WorldScene scene = (WorldScene)sceneField.get(screenManager.getGameScreen("CharacterPreview"));
            for (Entity actor : scene.session.engine.getEntities()) {
                AnimationComponent animation = actor.getComponent(AnimationComponent.class);
                require("neutral_idle".equals(animation.clip), "Preview actor is not in neutral idle");
                ModelComponent model = actor.getComponent(ModelComponent.class);
                for (NodePart part : model.instance.getNode("Equipment_Rifle", true).parts) {
                    require(!part.enabled, "Preview rifle is still visible");
                }
                for (NodePart part : model.instance.model.getNode("Equipment_Rifle", true).parts) {
                    require(part.enabled, "Preview modified shared gameplay equipment visibility");
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot inspect preview actors", e);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
