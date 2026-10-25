package io.github.TorenDropProject.menus;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/** Shared component sprites. Main's AssetManager owns the atlas and its texture. */
public final class CoffeeEnamelAssets {
    public static final String ATLAS = "ui-architecture/coffee-enamel/atlas/corovaner-ui.atlas";
    private final TextureAtlas atlas;

    public static void queue(AssetManager assets) {
        assets.load(ATLAS, TextureAtlas.class);
    }

    public CoffeeEnamelAssets(AssetManager assets) {
        atlas = assets.get(ATLAS, TextureAtlas.class);
    }

    public TextureAtlas.AtlasRegion region(String name) {
        TextureAtlas.AtlasRegion region = atlas.findRegion(name);
        if (region == null) throw new IllegalArgumentException("Missing coffee-enamel sprite: " + name);
        return region;
    }
}
