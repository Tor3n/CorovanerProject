package io.github.TorenDropProject.pregame;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import io.github.TorenDropProject.Main;
import io.github.TorenDropProject.characters.CharacterDefinition;
import io.github.TorenDropProject.world.WorldMap;
import io.github.TorenDropProject.world.WorldMapLoader;

public class SplashScreenAssetLoader {

    AssetManager assetManager;
    Texture texture;

    public SplashScreenAssetLoader(AssetManager assetManager){
        this.assetManager = assetManager;
    }

    public SplashScreenAssetLoader loadAssets(){

        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.setLoader(Model.class, ".obj", new ObjLoader(new InternalFileHandleResolver()));
        assetManager.setLoader(WorldMap.class, new WorldMapLoader(new InternalFileHandleResolver()));
        assetManager.load(CharacterDefinition.VAULT_DWELLER.modelPath, Model.class);
        assetManager.load("Splash.png", Texture.class);
        assetManager.load("grassland_tiles.png", Texture.class);
        assetManager.load("cursors/arrowCursor3.png", Pixmap.class);
        // The catalog declares each destination WorldMap and its Tiled/texture dependencies.
        io.github.TorenDropProject.menus.MenuTheme.queue(assetManager);

        return this;
    }


    public boolean preloaded(SpriteBatch spriteBatch){
        //17 = 60 fps during loading

        if (assetManager.isLoaded("Splash.png")) {
            texture = assetManager.get("Splash.png", Texture.class);
            spriteBatch.begin();
            spriteBatch.draw(texture,0,0, Main.worldWidth,Main.worldHeight);
            spriteBatch.end();
        }

        if(assetManager.update(5)){
            if(assetManager.isFinished()){

                System.out.println("*****************All your caravans are belongs to us now (loaded)*****************");
                assetManager.unload("Splash.png");
                return true;
            }
        }

        return false;
    }
}
