package io.github.TorenDropProject.pregame;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import io.github.TorenDropProject.Main;

public class SplashScreenAssetLoader {

    AssetManager assetManager;
    Texture texture;

    public SplashScreenAssetLoader(AssetManager assetManager){
        this.assetManager = assetManager;
    }

    public SplashScreenAssetLoader loadAssets(){

        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("Splash.png", Texture.class);
        assetManager.load("grassland_tiles.png", Texture.class);
        assetManager.load("MainModal.png", Texture.class);
        assetManager.load("char.png",Texture.class);
        assetManager.load("MainMenu.png",Texture.class);
        assetManager.load("cursors/arrowCursor3.png", Pixmap.class);
        assetManager.load("maps/mountinPass.tmx", TiledMap.class);

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
