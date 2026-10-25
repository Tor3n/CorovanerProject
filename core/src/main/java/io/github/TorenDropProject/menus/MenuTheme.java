package io.github.TorenDropProject.menus;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

/** Owns skin textures. Fonts are queued and owned by the central AssetManager. */
public final class MenuTheme implements Disposable {
    public static final Color INK = Color.valueOf("101715");
    public static final Color PANEL = Color.valueOf("19211d");
    public static final Color AMBER = Color.valueOf("d5a451");
    public static final Color ESPRESSO = Color.valueOf("21160b");
    public static final Color GREEN = Color.valueOf("8eb49a");
    public static final Color LINE = Color.valueOf("455044");
    public final Skin skin = new Skin();
    public final TextureRegion pixel;
    public final CoffeeEnamelAssets components;
    public final TerminalMenuAssets terminal;
    private final BitmapFont body, mono;

    public static void queue(AssetManager assets) {
        InternalFileHandleResolver resolver = new InternalFileHandleResolver();
        assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        font(assets, "body", "manrope/Manrope-Regular", 19);
        // Retain the compact label style ID; all menu typography now uses Manrope.
        font(assets, "mono", "manrope/Manrope-Regular", 14);
        font(assets, "heading", "manrope/Manrope-SemiBold", 34);
        font(assets, "title", "manrope/Manrope-SemiBold", 66);
        font(assets, "terminal-button", "manrope/Manrope-SemiBold", 68);
        CoffeeEnamelAssets.queue(assets);
        TerminalMenuAssets.queue(assets);
        assets.setLoader(FrontierCatalog.class, new FrontierCatalog.Loader(resolver));
        assets.load(FrontierCatalog.PATH, FrontierCatalog.class);
    }
    private static void font(AssetManager assets, String id, String file, int size) {
        FreetypeFontLoader.FreeTypeFontLoaderParameter parameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        parameter.fontFileName = "ui/fonts/" + file + ".ttf";
        parameter.fontParameters.size = size;
        parameter.fontParameters.magFilter = Texture.TextureFilter.Linear;
        parameter.fontParameters.minFilter = Texture.TextureFilter.Linear;
        assets.load("ui/" + id + ".ttf", BitmapFont.class, parameter);
    }
    public MenuTheme(AssetManager assets) {
        body = assets.get("ui/body.ttf", BitmapFont.class);
        mono = assets.get("ui/mono.ttf", BitmapFont.class);
        components = new CoffeeEnamelAssets(assets);
        terminal = new TerminalMenuAssets(assets, components);
        Pixmap image = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        image.setColor(Color.WHITE); image.fill();
        Texture texture = new Texture(image); image.dispose();
        skin.add("pixel-texture", texture);
        pixel = new TextureRegion(texture);
        skin.add("default", new Label.LabelStyle(body, AMBER));
        skin.add("muted", new Label.LabelStyle(body, AMBER));
        skin.add("mono", new Label.LabelStyle(mono, AMBER));
        skin.add("accent", new Label.LabelStyle(mono, AMBER));
        skin.add("heading", new Label.LabelStyle(assets.get("ui/heading.ttf", BitmapFont.class), AMBER));
        skin.add("title", new Label.LabelStyle(assets.get("ui/title.ttf", BitmapFont.class), AMBER));
        TextButton.TextButtonStyle button = new TextButton.TextButtonStyle(panel(PANEL, LINE),
            panel(Color.valueOf("302b21"), AMBER), panel(Color.valueOf("354334"), AMBER), body);
        button.over = panel(Color.valueOf("2b362b"), AMBER);
        button.focused = button.over;
        button.fontColor = AMBER;
        button.disabled = panel(INK, LINE);
        button.disabledFontColor = Color.valueOf("71766b");
        skin.add("default", button);
        TextButton.TextButtonStyle primary = new TextButton.TextButtonStyle(button);
        primary.up = panel(INK, AMBER);
        skin.add("primary", primary);
        TextField.TextFieldStyle field = new TextField.TextFieldStyle(body, AMBER,
            fill(AMBER), fill(LINE), panel(INK, LINE));
        field.focusedBackground = panel(INK, AMBER);
        field.messageFontColor = AMBER;
        skin.add("default", field);
        ScrollPane.ScrollPaneStyle scroll = new ScrollPane.ScrollPaneStyle();
        scroll.vScroll = fill(INK); scroll.vScrollKnob = panel(LINE, LINE);
        skin.add("default", scroll);
        List.ListStyle list = new List.ListStyle(body, AMBER, AMBER, fill(Color.valueOf("2b362b")));
        list.background = panel(INK, LINE);
        skin.add("default", list);
        SelectBox.SelectBoxStyle select = new SelectBox.SelectBoxStyle(body, AMBER, panel(INK, LINE), scroll, list);
        select.backgroundOver = panel(PANEL, AMBER);
        select.backgroundOpen = select.backgroundOver;
        skin.add("default", select);
        skin.add("default", new Window.WindowStyle(assets.get("ui/heading.ttf", BitmapFont.class), AMBER, panel(PANEL, AMBER)));
        skin.get(Window.WindowStyle.class).stageBackground = fill(new Color(0, 0, 0, 0.8f));
    }
    public void apply(GameSettings settings) {
        body.getData().setScale(settings.largerText ? 1.15f : 1f);
        mono.getData().setScale(settings.largerText ? 1.1f : 1f);
        terminal.font.getData().setScale(settings.largerText ? 0.575f : 0.5f);
    }
    public Drawable fill(Color color) { return new TextureRegionDrawable(pixel).tint(color); }
    public Drawable panel(Color background, Color border) {
        Pixmap image = new Pixmap(12, 12, Pixmap.Format.RGBA8888);
        image.setColor(border); image.fill();
        image.setColor(background); image.fillRectangle(1, 1, 10, 10);
        Texture texture = new Texture(image); image.dispose();
        skin.add("panel-" + skin.getAll(Texture.class).size, texture);
        NinePatchDrawable drawable = new NinePatchDrawable(new NinePatch(texture, 3, 3, 3, 3));
        drawable.setPadding(10, 10, 14, 14);
        return drawable;
    }
    @Override public void dispose() { skin.dispose(); }
}
