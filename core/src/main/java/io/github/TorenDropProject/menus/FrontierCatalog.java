package io.github.TorenDropProject.menus;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import io.github.TorenDropProject.world.WorldMap;

/** Editable content, independent of actors, save data and world rendering. */
public final class FrontierCatalog {
    public static final String PATH = "data/frontier.json";
    public Origin[] origins;
    public Archetype[] archetypes;
    public Destination[] destinations;
    public Route[] routes;

    public static class Origin {
        public String id, name, description, skill;
        @Override public String toString() { return name; }
    }
    public static class Archetype {
        public String id, name, description, skill, weapon;
        public int hitDie;
        @Override public String toString() { return name; }
    }
    public static class Destination {
        public String id, name, description, danger;
        public float x, y;
        public String worldMap;
        public boolean hasLocalArea() { return worldMap != null && !worldMap.isEmpty(); }
    }
    public static class Route {
        public String from, to;
        public int hours, supplies;
        public boolean connects(String a, String b) {
            return (from.equals(a) && to.equals(b)) || (from.equals(b) && to.equals(a));
        }
    }
    public Origin origin(String id) {
        for (Origin value : origins) if (value.id.equals(id)) return value;
        throw new IllegalArgumentException("Unknown origin: " + id);
    }
    public Archetype archetype(String id) {
        for (Archetype value : archetypes) if (value.id.equals(id)) return value;
        throw new IllegalArgumentException("Unknown archetype: " + id);
    }
    public Destination destination(String id) {
        for (Destination value : destinations) if (value.id.equals(id)) return value;
        throw new IllegalArgumentException("Unknown destination: " + id);
    }
    public Route route(String from, String to) {
        for (Route value : routes) if (value.connects(from, to)) return value;
        return null;
    }

    public void validate() {
        if (origins == null || origins.length == 0 || archetypes == null || archetypes.length == 0
            || destinations == null || destinations.length == 0 || routes == null) {
            throw new IllegalArgumentException("The frontier catalog is incomplete.");
        }
        java.util.Set<String> ids = new java.util.HashSet<>();
        for (Origin value : origins) {
            requireId(ids, value.id);
            if (value.name == null || value.description == null || value.skill == null) throw new IllegalArgumentException("Incomplete origin.");
        }
        ids.clear();
        for (Archetype value : archetypes) {
            requireId(ids, value.id);
            if (value.name == null || value.description == null || value.skill == null || value.weapon == null || value.hitDie < 4) {
                throw new IllegalArgumentException("Incomplete archetype.");
            }
        }
        ids.clear();
        for (Destination value : destinations) {
            requireId(ids, value.id);
            if (value.name == null || value.description == null || value.danger == null
                || !Float.isFinite(value.x) || !Float.isFinite(value.y) || value.x < 0 || value.x > 1 || value.y < 0 || value.y > 1) {
                throw new IllegalArgumentException("Invalid destination.");
            }
        }
        ids.clear();
        for (Route value : routes) {
            destination(value.from); destination(value.to);
            String key = value.from.compareTo(value.to) < 0 ? value.from + ":" + value.to : value.to + ":" + value.from;
            if (!ids.add(key) || value.from.equals(value.to) || value.hours <= 0 || value.supplies <= 0) {
                throw new IllegalArgumentException("Invalid or duplicate road.");
            }
        }
        origin("caravan"); archetype("gunslinger"); destination("mercy"); destination("pass");
    }
    private void requireId(java.util.Set<String> ids, String id) {
        if (id == null || id.isEmpty() || !ids.add(id)) throw new IllegalArgumentException("Missing or duplicate catalog ID.");
    }

    public static final class Loader extends AsynchronousAssetLoader<FrontierCatalog, AssetLoaderParameters<FrontierCatalog>> {
        private FrontierCatalog loaded;
        public Loader(FileHandleResolver resolver) { super(resolver); }
        @Override public void loadAsync(AssetManager manager, String name, FileHandle file,
                                        AssetLoaderParameters<FrontierCatalog> parameters) {
            loaded = new Json().fromJson(FrontierCatalog.class, file);
            loaded.validate();
        }
        @Override public FrontierCatalog loadSync(AssetManager manager, String name, FileHandle file,
                                                  AssetLoaderParameters<FrontierCatalog> parameters) {
            FrontierCatalog result = loaded;
            loaded = null;
            return result;
        }
        @Override public Array<AssetDescriptor> getDependencies(String name, FileHandle file,
                                                                 AssetLoaderParameters<FrontierCatalog> parameters) {
            FrontierCatalog catalog = new Json().fromJson(FrontierCatalog.class, file);
            Array<AssetDescriptor> dependencies = new Array<>();
            for (Destination destination : catalog.destinations) {
                if (destination.hasLocalArea()) {
                    dependencies.add(new AssetDescriptor<>(destination.worldMap, WorldMap.class));
                }
            }
            return dependencies;
        }
    }
}
