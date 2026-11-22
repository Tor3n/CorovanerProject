package io.github.TorenDropProject.menus;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import io.github.TorenDropProject.assets.FrontierCatalog;
import io.github.TorenDropProject.world.WorldMap;
import org.junit.Test;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

public class FrontierMapsTest {
    @Test public void allPlayableAreasAreStartupDependenciesWithExistingMapSources() {
        FileHandle assets = new FileHandle(new File("../assets").exists() ? "../assets" : "assets");
        FileHandle file = assets.child(FrontierCatalog.PATH);
        FrontierCatalog catalog = new Json().fromJson(FrontierCatalog.class, file);
        FrontierCatalog.Loader loader = new FrontierCatalog.Loader(assets::child);
        Array<AssetDescriptor> dependencies = loader.getDependencies(FrontierCatalog.PATH, file, null);
        Set<String> paths = new HashSet<>();
        for (AssetDescriptor dependency : dependencies) {
            assertEquals(WorldMap.class, dependency.type);
            assertTrue(paths.add(dependency.fileName));
            FileHandle descriptor = assets.child(dependency.fileName);
            assertTrue(descriptor.exists());
            assertTrue(descriptor.parent().child(new JsonReader().parse(descriptor).getString("map")).exists());
        }
        assertEquals(6, paths.size());
        for (FrontierCatalog.Destination destination : catalog.destinations) {
            assertEquals(destination.hasLocalArea(), paths.contains(destination.worldMap));
        }
        assertEquals("maps/mountinPass.world.json", catalog.destination("pass").worldMap);
        assertEquals("maps/forestCrossroads.world.json", catalog.destination("forest").worldMap);
        assertEquals("maps/abandonedQuarry.world.json", catalog.destination("quarry").worldMap);
        assertEquals("maps/forgottenGraveyard.world.json", catalog.destination("graveyard").worldMap);
    }
}
