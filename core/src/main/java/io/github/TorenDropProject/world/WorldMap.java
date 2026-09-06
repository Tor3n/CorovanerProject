package io.github.TorenDropProject.world;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/** Shared, immutable map asset. Owns generated meshes; textures belong to its TiledMap dependency. */
public final class WorldMap implements Disposable {
    public final WorldGrid grid;
    public final Array<Vector3> spawns;
    final Array<Chunk> chunks = new Array<>();

    public WorldMap(WorldGrid grid, Array<Vector3> spawns) { this.grid = grid; this.spawns = spawns; }
    public void add(Model model) { chunks.add(new Chunk(model)); }
    public int chunkCount() { return chunks.size; }
    @Override public void dispose() {
        for (Chunk chunk : chunks) chunk.model.dispose();
        chunks.clear();
    }
    static final class Chunk {
        final Model model;
        final ModelInstance instance;
        final BoundingBox bounds = new BoundingBox();
        Chunk(Model model) {
            this.model = model;
            instance = new ModelInstance(model);
            instance.calculateBoundingBox(bounds);
        }
    }
}
