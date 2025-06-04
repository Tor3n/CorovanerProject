package io.github.TorenDropProject.entities.textureLoaders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class MsDOSFalloutLeatherJacketLoader {

    public ArrayList<Array<TextureRegion>> testWalk;

    TextureAtlas atlas;
    public MsDOSFalloutLeatherJacketLoader(AssetManager assetManager) {
        atlas = assetManager.get("./output/leatherJacket.atlas", TextureAtlas.class);

        TextureRegion idleRegion = atlas.findRegion("idle");
        TextureRegion walkingRegion = atlas.findRegion("walking");

        int idleColums = 12, idleRows = 6;
        TextureRegion[][] idleFramesArray = idleRegion.split(34, 70);

        Array<TextureRegion> idleFrames = new Array<>();
        for (TextureRegion[] row : idleFramesArray) {
            idleFrames.addAll(row);
        }

        int walkColumns = 6, walkRows = 8;
        TextureRegion[][] walkFramesArray = walkingRegion.split(44, 69); // Width=59, Height=52

        ArrayList<Array<TextureRegion>> result = new ArrayList<>();


        for (TextureRegion[] row : walkFramesArray) {
            Array<TextureRegion> walkFramesOneDirection = new Array<>();
            walkFramesOneDirection.addAll(row);
            result.add(walkFramesOneDirection);
        }


        testWalk = result;
    }

}
