package io.github.TorenDropProject.pregame;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SplashScreenAssetLoader {

    AssetManager assetManager;
    Texture texture;

    public SplashScreenAssetLoader(AssetManager assetManager){
        this.assetManager = assetManager;
    }

    public SplashScreenAssetLoader loadAssets(){
        assetManager.load("Splash.png", Texture.class);
        assetManager.load("grassland_tiles.png", Texture.class);
        assetManager.load("MainModal.png", Texture.class);
        assetManager.load("bucket.png",Texture.class);
        assetManager.load("./output/leatherJacket.atlas", TextureAtlas.class);
        return this;
    }


    public boolean preloaded(SpriteBatch spriteBatch){
        //17 = 60 fps during loading
        if(assetManager.update(5)){
            if (assetManager.isLoaded("Splash.png")) {
                texture = assetManager.get("Splash.png", Texture.class);
                spriteBatch.begin();
                spriteBatch.draw(texture,0,0, 3,3);
                spriteBatch.end();
            }

            if(assetManager.isFinished()){
                System.out.println("*****************All your caravans are loaded*****************");
                assetManager.unload("Splash.png");
                return true;
            }
        }

        float progress = assetManager.getProgress();
        System.out.println("progress: "+progress);
        return false;
    }
}
