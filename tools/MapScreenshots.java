package io.github.TorenDropProject;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.github.TorenDropProject.characters.*;
import io.github.TorenDropProject.world.*;
import java.util.Arrays;

/** Reproducible full-map captures through the game's world renderer. Run from assets/. */
public class MapScreenshots extends ApplicationAdapter {
    private static final int WIDTH = 2400, HEIGHT = 1600;
    private final AssetManager assets = new AssetManager();
    private FileHandle[] maps;
    private int next;
    private FrameBuffer framebuffer;
    private SpriteBatch labels;
    private BitmapFont font;
    @Override public void create() {
        InternalFileHandleResolver resolver = new InternalFileHandleResolver();
        assets.setLoader(TiledMap.class, new TmxMapLoader(resolver));
        assets.setLoader(WorldMap.class, new WorldMapLoader(resolver));
        assets.setLoader(Model.class, ".obj", new ObjLoader(resolver));
        maps = Gdx.files.local("maps").list(".world.json");
        Arrays.sort(maps, (a, b) -> a.name().compareTo(b.name()));
        if (maps.length != Gdx.files.local("maps").list(".tmx").length) throw new AssertionError("A map lacks a world descriptor");
        for (FileHandle map : maps) assets.load(map.path(), WorldMap.class);
        assets.load(CharacterDefinition.VAULT_DWELLER.modelPath, Model.class);
        framebuffer = new FrameBuffer(Pixmap.Format.RGBA8888, WIDTH, HEIGHT, true);
        labels = new SpriteBatch(); font = new BitmapFont(); font.getData().setScale(2);
    }
    @Override public void render() {
        if (!assets.update(8)) return;
        if (next == maps.length) { System.out.println("MAP_SCREENSHOTS_PASSED: " + next + " map overviews plus Mercy detail"); Gdx.app.exit(); return; }
        FileHandle descriptor = maps[next++];
        WorldMap map = assets.get(descriptor.path(), WorldMap.class);
        for (Vector3 spawn : map.spawns) {
            if (!map.grid.canStand(spawn.x, spawn.z, .2f, spawn.y)) throw new AssertionError("Blocked spawn: " + descriptor);
        }
        CharacterFactory factory = new CharacterFactory(CharacterDefinition.VAULT_DWELLER,
            assets.get(CharacterDefinition.VAULT_DWELLER.modelPath, Model.class));
        BattleSession session = new BattleSession(map, factory, false);
        WorldCameraRig rig = new WorldCameraRig(); rig.resize(WIDTH, HEIGHT);
        WorldRenderer renderer = new WorldRenderer(rig, session);
        String stem = descriptor.name().replace(".world.json", "");
        try {
            fitMap(rig, map.grid);
            capture(renderer, rig, map, "maps/" + stem + ".png", stem + " / WORLD OVERVIEW");
            if (stem.equals("mercyCrossing")) {
                rig.camera().zoom = 1.6f;
                rig.follow(32.5f, 0, -29.5f);
                capture(renderer, rig, map, "maps/mercyCrossing-detail.png", "MERCY CROSSING / WASTELAND TILESET");
                // Main roads and the trading court remain reachable after the artwork replacement.
                Vector3 spawn = map.spawns.first();
                for (Vector3 point : new Vector3[] {new Vector3(32.5f, 0, -29.5f), new Vector3(32.5f, 0, -.5f),
                    new Vector3(.5f, 0, -27.5f), new Vector3(63.5f, 0, -31.5f)}) {
                    Array<Vector3> path = new GridPathfinder(map.grid).find(spawn.x, spawn.y, spawn.z, point.x, point.z, .18f);
                    if (path.isEmpty()) throw new AssertionError("Mercy route lost: " + point);
                }
            }
        } finally { renderer.dispose(); session.clear(); }
    }
    private void fitMap(WorldCameraRig rig, WorldGrid grid) {
        float width = grid.width * grid.cellSize, depth = grid.height * grid.cellSize;
        rig.follow(width / 2, 4.7f, -depth / 2);
        OrthographicCamera camera = rig.camera();
        Vector3 center = new Vector3(width / 2, 5.5f, -depth / 2);
        // Pull the same orthographic projection back for overview near-plane clearance.
        camera.position.set(center).mulAdd(camera.direction, -180);
        camera.far = 400; camera.zoom = 1; camera.update();
        float extentX = 0, extentY = 0;
        for (float x : new float[] {-3, width + 3}) for (float y : new float[] {-1, 12})
            for (float z : new float[] {-depth - 3, 3}) {
                Vector3 point = camera.project(new Vector3(x, y, z), 0, 0, WIDTH, HEIGHT);
                extentX = Math.max(extentX, Math.abs(point.x - WIDTH / 2f));
                extentY = Math.max(extentY, Math.abs(point.y - HEIGHT / 2f));
            }
        camera.zoom = Math.max(extentX / (WIDTH * .47f), extentY / (HEIGHT * .45f));
        camera.update();
    }
    private void capture(WorldRenderer renderer, WorldCameraRig rig, WorldMap map, String path, String title) {
        framebuffer.begin();
        renderer.render(map);
        labels.getProjectionMatrix().setToOrtho2D(0, 0, WIDTH, HEIGHT);
        labels.begin(); font.setColor(.85f, .8f, .66f, 1); font.draw(labels, title, 32, HEIGHT - 28); labels.end();
        Pixmap pixels = Pixmap.createFromFrameBuffer(0, 0, WIDTH, HEIGHT);
        framebuffer.end();
        PixmapIO.writePNG(Gdx.files.local(path), pixels, -1, true); pixels.dispose();
        System.out.println("Captured " + path);
    }
    @Override public void dispose() {
        labels.dispose(); font.dispose(); framebuffer.dispose(); assets.dispose();
    }
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("World map screenshots"); config.setWindowedMode(1000, 700); config.disableAudio(true);
        new Lwjgl3Application(new MapScreenshots(), config);
    }
}
