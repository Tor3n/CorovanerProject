package io.github.TorenDropProject.world;

import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.assets.loaders.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.*;

/** AssetManager resolves the TiledMap/textures first, prepares CPU chunks asynchronously, then uploads. */
public final class WorldMapLoader extends AsynchronousAssetLoader<WorldMap, AssetLoaderParameters<WorldMap>> {
    private final WorldMapImporter importer = new WorldMapImporter();
    private WorldMapImporter.PreparedMap prepared;
    public WorldMapLoader(FileHandleResolver resolver) { super(resolver); }
    private String mapPath(FileHandle file) { return file.parent().child(new JsonReader().parse(file).getString("map")).path(); }
    @Override public Array<AssetDescriptor> getDependencies(String name, FileHandle file, AssetLoaderParameters<WorldMap> p) {
        Array<AssetDescriptor> dependencies = new Array<>();
        dependencies.add(new AssetDescriptor<>(mapPath(file), TiledMap.class));
        return dependencies;
    }
    @Override public void loadAsync(AssetManager manager, String name, FileHandle file, AssetLoaderParameters<WorldMap> p) {
        prepared = importer.prepare(manager.get(mapPath(file), TiledMap.class), new JsonReader().parse(file));
    }
    @Override public WorldMap loadSync(AssetManager manager, String name, FileHandle file, AssetLoaderParameters<WorldMap> p) {
        try { return importer.upload(prepared); }
        finally { prepared = null; }
    }
}
