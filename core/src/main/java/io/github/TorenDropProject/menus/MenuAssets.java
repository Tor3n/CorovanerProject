package io.github.TorenDropProject.menus;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

/** Startup UI assets, all relative to one folder and owned by Main's AssetManager. */
public final class MenuAssets {
    public static final String ROOT = "ui/Menus/";
    public static final String SPLASH = ROOT + "Splash.png";
    public static final String CURSOR = ROOT + "arrowCursor3.png";
    public static final String MODAL = ROOT + "MainModal.png";

    private MenuAssets() { }

    public static void queue(AssetManager assets) {
        assets.load(SPLASH, Texture.class);
        assets.load(CURSOR, Pixmap.class);
        CoffeeEnamelAssets.queue(assets);
        TerminalMenuAssets.queue(assets);

        InternalFileHandleResolver resolver = new InternalFileHandleResolver();
        assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        queueFont(assets, "body", "Manrope-Regular.ttf", 19);
        // Retain the compact label style ID; all menu typography uses Manrope.
        queueFont(assets, "mono", "Manrope-Regular.ttf", 14);
        queueFont(assets, "heading", "Manrope-SemiBold.ttf", 34);
        queueFont(assets, "title", "Manrope-SemiBold.ttf", 66);
        queueFont(assets, "terminal-button", "Manrope-SemiBold.ttf", 68);
    }

    /** AssetManager key for a generated size, distinct from the source TTF. */
    public static String font(String id) {
        return ROOT + id + ".ttf";
    }

    private static void queueFont(AssetManager assets, String id, String source, int size) {
        FreetypeFontLoader.FreeTypeFontLoaderParameter parameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        parameter.fontFileName = ROOT + source;
        parameter.fontParameters.size = size;
        parameter.fontParameters.magFilter = Texture.TextureFilter.Linear;
        parameter.fontParameters.minFilter = Texture.TextureFilter.Linear;
        assets.load(font(id), BitmapFont.class, parameter);
    }
}
