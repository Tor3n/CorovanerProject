package io.github.TorenDropProject.lwjgl3;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.TorenDropProject.characters.CharacterDefinition;
import io.github.TorenDropProject.characters.CharacterFactory;
import io.github.TorenDropProject.entities.components.AnimationComponent;
import io.github.TorenDropProject.entities.components.ModelComponent;
import io.github.TorenDropProject.world.WorldMap;
import io.github.TorenDropProject.world.WorldMapLoader;
import io.github.TorenDropProject.world.WorldScene;
import io.github.TorenDropProject.world.WorldSceneFactory;
import net.mgsx.gltf.loaders.glb.GLBAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

/** Real desktop GL smoke, with an invisible window and reproducible frame captures. */
public final class CharacterSmoke extends ApplicationAdapter {
    private static final String MAP = "maps/mountinPass.world.json";
    private final AssetManager assets = new AssetManager();
    private WorldScene scene;
    private int frame;
    private String currentClip;
    private Matrix4 firstSocket;

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Character animation smoke");
        config.setWindowedMode(1100, 800);
        config.setInitialVisible(false);
        config.disableAudio(true);
        config.useVsync(false);
        config.setForegroundFPS(0);
        new Lwjgl3Application(new CharacterSmoke(), config);
    }

    @Override public void create() {
        assets.setLoader(SceneAsset.class, ".glb", new GLBAssetLoader());
        assets.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assets.setLoader(WorldMap.class, new WorldMapLoader(new InternalFileHandleResolver()));
        assets.load(CharacterDefinition.PROTAGONIST.modelPath, SceneAsset.class);
        assets.load(MAP, WorldMap.class);
    }

    @Override public void render() {
        if (scene == null) {
            if (!assets.update(5)) return;
            SceneAsset asset = assets.get(CharacterDefinition.PROTAGONIST.modelPath, SceneAsset.class);
            scene = new WorldSceneFactory(assets, new CharacterFactory(CharacterDefinition.PROTAGONIST, asset)).create(MAP, false);
            scene.resize(1100, 800);
            scene.cameraRig.camera().zoom = 0.4f;
            boolean textured = false;
            for (com.badlogic.gdx.graphics.g3d.Material material : asset.scene.model.materials) {
                textured |= material.has(TextureAttribute.Diffuse);
            }
            require(textured, "GLB base color texture was not loaded");
            require(asset.maxBones > 0 && asset.maxBones <= 32, "Unexpected skin palette");
            System.out.println("Character smoke: " + asset.maxBones + " bones, " + asset.scene.model.animations.size + " clips");
        }
        String clip = frame < 36 ? "idle" : frame < 72 ? "walk" : "run";
        if (!clip.equals(currentClip)) {
            currentClip = clip;
            for (Entity actor : scene.session.engine.getEntitiesFor(Family.all(AnimationComponent.class).get())) {
                actor.getComponent(AnimationComponent.class).previewClip = clip;
            }
        }
        if (frame == 108) scene.cameraRig.camera().zoom = 1f;
        scene.render(1f / 30f, false);
        ModelComponent appearance = scene.session.followed().getComponent(ModelComponent.class);
        Matrix4 socket = appearance.attachmentTransform("socket_weapon_r", new Matrix4(), new Matrix4());
        for (float value : socket.val) require(Float.isFinite(value), "Nonfinite animated socket");
        if (frame == 0) firstSocket = new Matrix4(socket);
        if (frame == 35) capture("idle-close");
        if (frame == 71) {
            capture("walk-close");
            require(!firstSocket.equals(socket), "Socket did not animate");
        }
        if (frame == 107) capture("run-close");
        if (frame == 119) {
            capture("world");
            AnimationComponent animation = scene.session.followed().getComponent(AnimationComponent.class);
            float time = animation.controller.current.time;
            scene.render(0.2f, true);
            require(time == animation.controller.current.time, "Paused world advanced its animation");
            require(Gdx.gl.glGetError() == GL20.GL_NO_ERROR, "OpenGL error while rendering character");
            Gdx.files.absolute(System.getProperty("characterSmoke.output") + "/result.txt")
                .writeString("PASS: real GLB loading, textures, skin shaders, sockets, idle/walk/run, shared world and pause.\n", false);
            System.out.println("Character smoke PASS");
            Gdx.app.exit();
        }
        frame++;
    }

    private void capture(String name) {
        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(0, 0, 1100, 800);
        try {
            PixmapIO.writePNG(Gdx.files.absolute(System.getProperty("characterSmoke.output") + "/" + name + ".png"), pixels, -1, true);
        } finally { pixels.dispose(); }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }

    @Override public void dispose() {
        if (scene != null) scene.dispose();
        assets.dispose();
    }
}
