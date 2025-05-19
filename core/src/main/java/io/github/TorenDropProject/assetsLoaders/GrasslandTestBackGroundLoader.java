package io.github.TorenDropProject.assetsLoaders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.TorenDropProject.Main;

public class GrasslandTestBackGroundLoader {
    Texture texture;
    float tileHeight = 0.5f;
    float tileWidth = 1f;

    TextureRegion TESTregion;
    TextureRegion[] grassTiles;
    TextureRegion[][] testStaticMap;

    public GrasslandTestBackGroundLoader(AssetManager assetManager){
        texture = new Texture("grassland_tiles.png");
        assetManager.load("grassland_tiles.png", Texture.class);

        TESTregion = new TextureRegion(texture, 0,0,63,31);

        grassTiles = new TextureRegion[16];

        for (int i = 0; i < 15; i++) {
            grassTiles[i] = new TextureRegion(texture, 64 * i,0,63,31);
        }

        //(int) (Math.random() * 15)
        testStaticMap = new TextureRegion[Main.worldWidth+1][Main.worldHeight*2+1];
        for (int i = 0; i < Main.worldWidth+1; i++) {
            for (int j = 0; j < Main.worldHeight*2+1; j++) {
                testStaticMap[i][j]=grassTiles[(int) (Math.random() * 15)];
            }
        }

    }

    public void drawBackGround(SpriteBatch spriteBatch){
        //bottom
        for (int i = 0; i < Main.worldWidth+1; i++) {
            spriteBatch.draw(testStaticMap[i][1], i-tileWidth/2 , (-1)*0.25f,1f,0.5f);
        }


        for (int i = 0; i < Main.worldWidth+1; i++) { //x
            for (int j = 0; j < Main.worldHeight*2+1; j++) { //y
                //testStaticMap[i][j] current tile on a map
                spriteBatch.draw(testStaticMap[i][j], i-tileWidth/2, (j)*tileHeight+(tileHeight/2f), tileWidth, tileHeight);
                spriteBatch.draw(testStaticMap[i][j], i , j*0.5f,1f,0.5f);
            }

        }

        //spriteBatch.draw(TESTregion, 0,0,1f,0.5f);
        //spriteBatch.draw(TESTregion, 1,0,1f,0.5f);
    }

}
