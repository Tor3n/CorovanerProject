package io.github.TorenDropProject.entities.textureLoaders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class MsDOSFalloutLeatherJacketLoader {

    public Array<TextureRegion> testWalk;

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
        TextureRegion[][] walkFramesArray = walkingRegion.split(42, 70); // Width=59, Height=52

        Array<TextureRegion> walkFrames = new Array<>();
        for (TextureRegion[] row : walkFramesArray) {
            walkFrames.addAll(row);
        }

        testWalk = walkFrames;
    }

}
