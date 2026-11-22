package io.github.TorenDropProject.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.mgsx.gltf.loaders.glb.GLBAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import io.github.TorenDropProject.Main;
import io.github.TorenDropProject.menus.MenuAssets;
import io.github.TorenDropProject.characters.CharacterDefinition;
import io.github.TorenDropProject.world.WorldMap;
import io.github.TorenDropProject.world.WorldMapLoader;

public class SplashScreenAssetLoader {

    private final AssetManager assetManager;

    public SplashScreenAssetLoader(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public SplashScreenAssetLoader loadAssets() {
        InternalFileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(resolver));
        assetManager.setLoader(SceneAsset.class, ".glb", new GLBAssetLoader());
        assetManager.setLoader(WorldMap.class, new WorldMapLoader(resolver));
        assetManager.setLoader(FrontierCatalog.class, new FrontierCatalog.Loader(resolver));

        MenuAssets.queue(assetManager);
        assetManager.load(CharacterDefinition.PROTAGONIST.modelPath, SceneAsset.class);
        assetManager.load("grassland_tiles.png", Texture.class);
        // The catalog declares each destination WorldMap and its Tiled/texture dependencies.
        assetManager.load(FrontierCatalog.PATH, FrontierCatalog.class);

        return this;
    }

    public boolean preloaded(SpriteBatch spriteBatch) {
        if (assetManager.isLoaded(MenuAssets.SPLASH)) {
            Texture texture = assetManager.get(MenuAssets.SPLASH, Texture.class);
            spriteBatch.begin();
            spriteBatch.draw(texture, 0, 0, Main.worldWidth, Main.worldHeight);
            spriteBatch.end();
        }

        if (assetManager.update(5)) {
            System.out.println("*****************All your caravans are belongs to us now (loaded)*****************");
            assetManager.unload(MenuAssets.SPLASH);
            return true;
        }

        return false;
    }
}
