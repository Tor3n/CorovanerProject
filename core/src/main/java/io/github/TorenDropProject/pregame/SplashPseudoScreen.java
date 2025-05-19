package io.github.TorenDropProject.pregame;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SplashPseudoScreen {

    AssetManager assetManager;
    Texture texture;

    public SplashPseudoScreen(AssetManager assetManager){
        this.assetManager = assetManager;
    }

    public SplashPseudoScreen loadSplash(){
        assetManager.load("Splash.png", Texture.class);
        return this;
    }


    public boolean preloaded(SpriteBatch spriteBatch){
        //17 = 60 fps during loading
        if(assetManager.update(5)){
            if (assetManager.isLoaded("Splash.png")) {
                texture = assetManager.get("Splash.png", Texture.class);
                spriteBatch.begin();
                spriteBatch.draw(texture,0,0, 2,2);
                spriteBatch.end();
            }

            if(assetManager.isFinished()){
                assetManager.unload("Splash.png");
                return true;
            }
        }

        float progress = assetManager.getProgress();
        System.out.println("progress: "+progress);
        return false;
    }
}
