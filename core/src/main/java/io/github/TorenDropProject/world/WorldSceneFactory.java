package io.github.TorenDropProject.world;

import com.badlogic.gdx.assets.AssetManager;
import io.github.TorenDropProject.characters.CharacterFactory;

/** Creates independent sessions from shared assets already loaded during startup. */
public final class WorldSceneFactory {
    private final AssetManager assets;
    private final CharacterFactory characters;

    public WorldSceneFactory(AssetManager assets, CharacterFactory characters) {
        this.assets = assets;
        this.characters = characters;
    }

    public WorldScene create(String mapPath, boolean playerInput) {
        return new WorldScene(new BattleSession(assets.get(mapPath, WorldMap.class), characters, playerInput));
    }
}
