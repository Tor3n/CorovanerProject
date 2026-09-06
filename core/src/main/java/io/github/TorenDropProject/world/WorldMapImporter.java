package io.github.TorenDropProject.world;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.model.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** Converts authoring data to CPU mesh buffers off-thread, then uploads meshes on the GL thread. */
public final class WorldMapImporter {
    private static final int CHUNK_SIZE = 16;
    private static final float ROOT_TWO = (float)Math.sqrt(2);
    private static final float COS_ELEVATION = (float)Math.cos(Math.PI / 6);
    private static final float LAYER_OFFSET = 0.0005f;

    public PreparedMap prepare(TiledMap tiledMap, JsonValue config) {
        int width = tiledMap.getProperties().get("width", Integer.class);
        int height = tiledMap.getProperties().get("height", Integer.class);
        int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
        int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
        require("isometric".equals(tiledMap.getProperties().get("orientation")), "Expected isometric Tiled map");
        require(tileWidth == tileHeight * 2, "World camera requires 2:1 ground tiles");
        PreparedMap result = new PreparedMap(new WorldGrid(width, height, config.getFloat("cellSize", 1f)));
        ObjectMap<TiledMapTile, TileStyle> styles = new ObjectMap<>();
        for (TiledMapTileSet set : tiledMap.getTileSets()) {
            TileStyle style = new TileStyle(number(set.getProperties(), "worldAnchorX", tileWidth / 2f),
                number(set.getProperties(), "worldAnchorY", tileHeight / 2f),
                set.getProperties().get("worldSolid", false, Boolean.class));
            for (TiledMapTile tile : set) styles.put(tile, style);
        }
        // Terrain first: props use the final elevation field regardless of layer ordering.
        for (MapLayer layer : tiledMap.getLayers()) {
            if (!layer.isVisible()) continue;
            require(layer instanceof TiledMapTileLayer, "Unsupported map layer: " + layer.getName());
            require(layer.getOffsetX() == 0 && layer.getOffsetY() == 0 && layer.getOpacity() == 1f
                && layer.getParallaxX() == 1f && layer.getParallaxY() == 1f, "Unsupported layer offsets/opacity/parallax: " + layer.getName());
            String role = layer.getProperties().get("worldRole", String.class);
            require("ground".equals(role) || "cutout".equals(role), "Missing/unknown worldRole: " + layer.getName());
            if (!"ground".equals(role)) continue;
            TiledMapTileLayer tiles = (TiledMapTileLayer)layer;
            float elevation = number(layer.getProperties(), "worldElevation", 0);
            for (int row = 0; row < height; row++) for (int col = 0; col < width; col++) {
                if (tiles.getCell(col, row) != null) result.grid.setGround(col, row, elevation);
            }
        }
        int layerIndex = 0;
        for (MapLayer layer : tiledMap.getLayers()) {
            if (!layer.isVisible()) continue;
            TiledMapTileLayer tiles = (TiledMapTileLayer)layer;
            boolean ground = "ground".equals(layer.getProperties().get("worldRole", String.class));
            float elevation = number(layer.getProperties(), "worldElevation", 0);
            boolean fixedElevation = layer.getProperties().containsKey("worldElevation");
            boolean solid = layer.getProperties().get("worldSolid", false, Boolean.class);
            Color tint = Color.valueOf(layer.getProperties().get("worldTint", "ffffffff", String.class));
            float brightness = number(layer.getProperties(), "worldBrightness", 1);
            require(Float.isFinite(brightness) && brightness > 0, "Invalid layer brightness");
            tint.r *= brightness; tint.g *= brightness; tint.b *= brightness;
            for (int startRow = 0; startRow < height; startRow += CHUNK_SIZE) {
                for (int startCol = 0; startCol < width; startCol += CHUNK_SIZE) {
                    Map<Texture, Buffer> batches = new LinkedHashMap<>();
                    for (int row = startRow; row < Math.min(height, startRow + CHUNK_SIZE); row++) {
                        for (int col = startCol; col < Math.min(width, startCol + CHUNK_SIZE); col++) {
                            TiledMapTileLayer.Cell cell = tiles.getCell(col, row);
                            if (cell == null || cell.getTile() == null) continue;
                            TiledMapTile tile = cell.getTile();
                            require(!(tile instanceof com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile), "Animated tiles require a dynamic prop renderer");
                            require(tile.getOffsetX() == 0 && tile.getOffsetY() == 0, "Unsupported tile offset: " + tile.getId());
                            TextureRegion region = tile.getTextureRegion();
                            if (ground) require(region.getRegionWidth() == tileWidth && region.getRegionHeight() == tileHeight,
                                "Tall tile in ground layer: " + layer.getName());
                            Buffer buffer = batches.computeIfAbsent(region.getTexture(), texture -> new Buffer(texture, !ground));
                            buffer.tint.set(tint);
                            float x = result.grid.centerX(col), z = result.grid.centerZ(row);
                            if (ground) {
                                // Retain the base floor under raised platforms; it is visible from their edges.
                                ground(buffer, cell, x, elevation + layerIndex * LAYER_OFFSET, z, result.grid.cellSize);
                            } else {
                                TileStyle style = styles.get(tile);
                                float y = fixedElevation ? elevation : result.grid.elevation(col, row);
                                // The footprint must sit above every ground layer's anti-flicker offset.
                                y += (tiledMap.getLayers().getCount() + 1) * LAYER_OFFSET;
                                cutout(buffer, cell, style, x, y, z, tileWidth, result.grid.cellSize);
                                if (solid || style.solid) result.grid.block(col, row);
                            }
                        }
                    }
                    for (Buffer buffer : batches.values()) if (buffer.indices.size > 0) result.buffers.add(buffer);
                }
            }
            layerIndex++;
        }
        TextureRegion riserRegion = null;
        if (config.has("riserTexture")) {
            JsonValue patch = config.get("riserTexture");
            TiledMapTileSet set = tiledMap.getTileSets().getTileSet(patch.getString("tileset"));
            require(set != null && set.iterator().hasNext(), "Missing riser texture tileset");
            Texture texture = set.iterator().next().getTextureRegion().getTexture();
            int px = patch.getInt("x"), py = patch.getInt("y"), pw = patch.getInt("width"), ph = patch.getInt("height");
            require(px >= 0 && py >= 0 && pw > 0 && ph > 0 && px + pw <= texture.getWidth()
                && py + ph <= texture.getHeight(), "Invalid riser texture region");
            riserRegion = new TextureRegion(texture, px, py, pw, ph);
        }
        // Solid risers close elevation boundaries; cutout cliff art dresses these physical walls.
        for (int startRow = 0; startRow < height; startRow += CHUNK_SIZE) {
            for (int startCol = 0; startCol < width; startCol += CHUNK_SIZE) {
                Buffer risers = new Buffer(riserRegion == null ? null : riserRegion.getTexture(), false);
                for (int row = startRow; row < Math.min(height, startRow + CHUNK_SIZE); row++) {
                    for (int col = startCol; col < Math.min(width, startCol + CHUNK_SIZE); col++) {
                        float top = result.grid.elevation(col, row);
                        if (top <= 0) continue;
                        float size = result.grid.cellSize, x = col * size, z = -row * size;
                        int[][] adjacent = {{col-1,row},{col+1,row},{col,row-1},{col,row+1}};
                        float[][][] edges = {{{x,z-size},{x,z}},{{x+size,z},{x+size,z-size}},
                            {{x,z},{x+size,z}},{{x+size,z-size},{x,z-size}}};
                        for (int side = 0; side < 4; side++) {
                            int nc = adjacent[side][0], nr = adjacent[side][1];
                            float bottom = result.grid.contains(nc,nr) ? result.grid.elevation(nc,nr) : 0;
                            if (bottom >= top) continue;
                            float[] a = edges[side][0], b = edges[side][1];
                            int base = risers.vertices.size / 5;
                            float[][] points = {{a[0],bottom,a[1]},{b[0],bottom,b[1]},
                                {b[0],top,b[1]},{a[0],top,a[1]}};
                            for (int i = 0; i < 4; i++) {
                                float[] v = points[i];
                                risers.vertices.add(v[0],v[1],v[2]);
                                float u = riserRegion == null ? 0 : (i == 0 || i == 3 ? riserRegion.getU() : riserRegion.getU2());
                                float t = riserRegion == null ? 0 : (i < 2 ? riserRegion.getV2() : riserRegion.getV());
                                risers.vertices.add(u,t);
                            }
                            for (int i : new int[]{0,1,2,0,2,3}) risers.indices.add((short)(base+i));
                        }
                    }
                }
                risers.twoSided = true;
                if (risers.indices.size > 0) result.buffers.add(risers);
            }
        }
        result.structures.addAll(WorldStructures.prepare(config.get("structures"), result.grid));
        for (JsonValue spawn : config.get("spawns")) {
            // Descriptor rows follow Tiled's top-down authoring convention, not libGDX's flipped rows.
            int col = spawn.getInt("column"), row = height - 1 - spawn.getInt("row");
            Vector3 point = findSpawn(result.grid, col, row, result.spawns);
            result.spawns.add(point);
        }
        require(result.spawns.size > 0, "Map needs at least one spawn");
        return result;
    }

    private Vector3 findSpawn(WorldGrid grid, int desiredCol, int desiredRow, Array<Vector3> used) {
        for (int radius = 0; radius < Math.max(grid.width, grid.height); radius++) {
            for (int row = desiredRow - radius; row <= desiredRow + radius; row++) {
                for (int col = desiredCol - radius; col <= desiredCol + radius; col++) {
                    if (Math.max(Math.abs(col - desiredCol), Math.abs(row - desiredRow)) != radius || !grid.walkable(col, row)) continue;
                    float x = grid.centerX(col), z = grid.centerZ(row), y = grid.elevation(col, row);
                    if (!grid.canStand(x, z, 0.2f, y)) continue;
                    boolean occupied = false;
                    for (Vector3 other : used) if (other.dst2(x, y, z) < 1f) occupied = true;
                    if (!occupied) return new Vector3(x, y, z);
                }
            }
        }
        throw new GdxRuntimeException("No walkable spawn near " + desiredCol + "," + desiredRow);
    }

    private void ground(Buffer buffer, TiledMapTileLayer.Cell cell, float x, float y, float z, float size) {
        float h = size / 2;
        float[][] points = {{x-h,y,z+h},{x+h,y,z+h},{x+h,y,z-h},{x-h,y,z-h}};
        // Diamond corners: left, bottom, right, top, not the corners of the enclosing sprite rectangle.
        float[][] uv = {{0,0.5f},{0.5f,1},{1,0.5f},{0.5f,0}};
        quad(buffer, cell, points, uv, true);
    }

    private void cutout(Buffer buffer, TiledMapTileLayer.Cell cell, TileStyle style, float x, float y, float z,
                        int tileWidth, float cellSize) {
        TextureRegion region = cell.getTile().getTextureRegion();
        float[][] points = cutoutPoints(region.getRegionWidth(), region.getRegionHeight(), style.anchorX,
            style.anchorY, x, y, z, tileWidth, cellSize);
        float seam = 1f - style.anchorY / region.getRegionHeight();
        if (style.anchorY > 0) {
            quad(buffer, cell, new float[][]{points[0], points[1], points[3], points[2]},
                new float[][]{{0,1},{1,1},{1,seam},{0,seam}}, false);
        }
        if (style.anchorY < region.getRegionHeight()) {
            quad(buffer, cell, new float[][]{points[2], points[3], points[5], points[4]},
                new float[][]{{0,seam},{1,seam},{1,0},{0,0}}, false);
        }
    }

    /** Bottom, anchor and top pairs. Fold the footprint forward without changing its screen projection. */
    static float[][] cutoutPoints(float width, float height, float anchorX, float anchorY,
                                  float x, float y, float z, int tileWidth, float cellSize) {
        require(anchorY >= 0 && anchorY <= height, "Cutout anchor outside image");
        float pixelsPerUnit = tileWidth / (ROOT_TWO * cellSize);
        float left = -anchorX / pixelsPerUnit / ROOT_TWO;
        float right = (width - anchorX) / pixelsPerUnit / ROOT_TWO;
        float top = (height - anchorY) / pixelsPerUnit / COS_ELEVATION;
        // At 30 degrees, moving toward the camera on the ground projects downward by sin(30).
        // Previously these pixels extended below Y=ground and were cut off by the depth buffer.
        float forward = anchorY / pixelsPerUnit / 0.5f / ROOT_TWO;
        return new float[][]{{x+left+forward,y,z-left+forward},{x+right+forward,y,z-right+forward},
            {x+left,y,z-left},{x+right,y,z-right},
            {x+left,y+top,z-left},{x+right,y+top,z-right}};
    }

    private void quad(Buffer buffer, TiledMapTileLayer.Cell cell, float[][] points, float[][] uv, boolean inset) {
        TextureRegion region = cell.getTile().getTextureRegion();
        int base = buffer.vertices.size / 5;
        for (int i = 0; i < 4; i++) {
            float u = uv[i][0], v = uv[i][1];
            if (cell.getFlipHorizontally()) u = 1-u;
            if (cell.getFlipVertically()) v = 1-v;
            for (int turn = 0; turn < cell.getRotation(); turn++) { float old = u; u = v; v = 1-old; }
            float marginU = (inset ? 0.75f : 0.05f) / region.getRegionWidth();
            float marginV = (inset ? 0.75f : 0.05f) / region.getRegionHeight();
            u = marginU + u * (1-2*marginU);
            v = marginV + v * (1-2*marginV);
            buffer.vertices.add(points[i][0], points[i][1], points[i][2]);
            buffer.vertices.add(region.getU() + u*(region.getU2()-region.getU()), region.getV() + v*(region.getV2()-region.getV()));
        }
        for (int index : new int[]{0,1,2,0,2,3}) buffer.indices.add((short)(base+index));
    }

    /** GL-thread upload. Model owns only its Mesh, never dependency textures. */
    public WorldMap upload(PreparedMap prepared) {
        WorldMap world = new WorldMap(prepared.grid, prepared.spawns);
        try {
            for (Buffer buffer : prepared.buffers) {
                Mesh mesh = new Mesh(true, buffer.vertices.size / 5, buffer.indices.size,
                    VertexAttribute.Position(), VertexAttribute.TexCoords(0));
                Model model = new Model();
                model.manageDisposable(mesh);
                try {
                    mesh.setVertices(buffer.vertices.toArray());
                    mesh.setIndices(buffer.indices.toArray());
                    Material material = buffer.texture == null
                        ? new Material(ColorAttribute.createDiffuse(0.24f, 0.205f, 0.17f, 1f), IntAttribute.createCullFace(GL20.GL_NONE))
                        : new Material(TextureAttribute.createDiffuse(buffer.texture));
                    if (buffer.texture != null) material.set(ColorAttribute.createDiffuse(buffer.tint));
                    if (buffer.twoSided) material.set(IntAttribute.createCullFace(GL20.GL_NONE));
                    if (buffer.cutout) {
                        // DefaultShader enables alpha discard only with BlendingAttribute. ONE/ZERO replaces
                        // opaque pixels, while AlphaTest discards transparent pixels before they write depth.
                        material.set(new BlendingAttribute(GL20.GL_ONE, GL20.GL_ZERO),
                            FloatAttribute.createAlphaTest(0.4f), new DepthTestAttribute(GL20.GL_LEQUAL, true),
                            IntAttribute.createCullFace(GL20.GL_NONE));
                    }
                    MeshPart part = new MeshPart("surface", mesh, 0, buffer.indices.size, GL20.GL_TRIANGLES);
                    Node node = new Node();
                    node.id = "surface";
                    node.parts.add(new NodePart(part, material));
                    model.nodes.add(node);
                    model.meshes.add(mesh);
                    model.meshParts.add(part);
                    model.materials.add(material);
                    model.calculateTransforms();
                    world.add(model);
                } catch (RuntimeException failure) {
                    model.dispose();
                    throw failure;
                }
            }
            for (WorldStructures.Structure structure : prepared.structures) world.add(WorldStructures.upload(structure));
            return world;
        } catch (RuntimeException failure) {
            world.dispose();
            throw failure;
        }
    }
    private static float number(MapProperties p, String name, float fallback) {
        Object value = p.get(name);
        return value instanceof Number ? ((Number)value).floatValue() : fallback;
    }
    private static void require(boolean condition, String message) { if (!condition) throw new GdxRuntimeException(message); }
    public static final class PreparedMap {
        final WorldGrid grid;
        final Array<Vector3> spawns = new Array<>();
        final Array<Buffer> buffers = new Array<>();
        final Array<WorldStructures.Structure> structures = new Array<>();
        PreparedMap(WorldGrid grid) { this.grid = grid; }
    }
    private static final class TileStyle {
        final float anchorX, anchorY;
        final boolean solid;
        TileStyle(float x, float y, boolean solid) { anchorX = x; anchorY = y; this.solid = solid; }
    }
    private static final class Buffer {
        final Texture texture;
        final boolean cutout;
        final Color tint = new Color(Color.WHITE);
        boolean twoSided;
        final FloatArray vertices = new FloatArray();
        final ShortArray indices = new ShortArray();
        Buffer(Texture texture, boolean cutout) { this.texture = texture; this.cutout = cutout; }
    }
}
