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
    private static final String ELEMENTS = "ui-architecture/element-set/";
    private static final String SCREEN_FRAME_TEXTURE = ELEMENTS + "screen-frame.png";
    private static final String PANEL_TEXTURE = ELEMENTS + "panel-dark.png";
    private static final String DARK_CONTROL_TEXTURE = ELEMENTS + "control-dark.png";
    private static final String ENAMEL_CONTROL_TEXTURE = ELEMENTS + "control-enamel.png";
    public static final Color INK = Color.valueOf("101715");
    public static final Color PANEL = Color.valueOf("19211d");
    public static final Color PAPER = Color.valueOf("e2d4b5");
    public static final Color MUTED = Color.valueOf("a7ad98");
    public static final Color AMBER = Color.valueOf("d5a451");
    public static final Color GREEN = Color.valueOf("8eb49a");
    public static final Color LINE = Color.valueOf("455044");
    public final Skin skin = new Skin();
    public final TextureRegion pixel;
    private final BitmapFont body, mono;
    private final NinePatchDrawable screenFrame;
    private final NinePatchDrawable panelFrame;
    private final NinePatchDrawable darkControl;
    private final NinePatchDrawable enamelControl;

    public static void queue(AssetManager assets) {
        InternalFileHandleResolver resolver = new InternalFileHandleResolver();
        assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        font(assets, "body", "frontier-body", 19);
        font(assets, "mono", "frontier-mono", 14);
        font(assets, "heading", "frontier-title", 34);
        font(assets, "title", "frontier-title", 66);
        assets.load(SCREEN_FRAME_TEXTURE, Texture.class);
        assets.load(PANEL_TEXTURE, Texture.class);
        assets.load(DARK_CONTROL_TEXTURE, Texture.class);
        assets.load(ENAMEL_CONTROL_TEXTURE, Texture.class);
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
        screenFrame = patch(assets.get(SCREEN_FRAME_TEXTURE, Texture.class), 22, 22, 22, 22, 22, 22, 22, 22);
        panelFrame = patch(assets.get(PANEL_TEXTURE, Texture.class), 22, 22, 22, 22, 22, 22, 22, 22);
        darkControl = patch(assets.get(DARK_CONTROL_TEXTURE, Texture.class), 18, 18, 18, 18, 16, 16, 10, 10);
        enamelControl = patch(assets.get(ENAMEL_CONTROL_TEXTURE, Texture.class), 18, 18, 18, 18, 16, 16, 10, 10);
        Pixmap image = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        image.setColor(Color.WHITE); image.fill();
        Texture texture = new Texture(image); image.dispose();
        skin.add("pixel-texture", texture);
        pixel = new TextureRegion(texture);
        skin.add("default", new Label.LabelStyle(body, PAPER));
        skin.add("muted", new Label.LabelStyle(body, MUTED));
        skin.add("mono", new Label.LabelStyle(mono, MUTED));
        skin.add("accent", new Label.LabelStyle(mono, AMBER));
        skin.add("heading", new Label.LabelStyle(assets.get("ui/heading.ttf", BitmapFont.class), PAPER));
        skin.add("title", new Label.LabelStyle(assets.get("ui/title.ttf", BitmapFont.class), PAPER));
        TextButton.TextButtonStyle button = new TextButton.TextButtonStyle(darkControl.tint(Color.WHITE),
            darkControl.tint(Color.valueOf("b58b4f")), darkControl.tint(Color.valueOf("d0a45e")), body);
        button.over = darkControl.tint(Color.valueOf("d8c49d"));
        button.focused = button.over;
        button.fontColor = PAPER;
        button.overFontColor = Color.WHITE;
        button.downFontColor = Color.WHITE;
        button.checked = enamelControl.tint(Color.valueOf("d8b970"));
        button.checkedOver = enamelControl.tint(Color.valueOf("f0d592"));
        button.checkedFontColor = INK;
        button.checkedOverFontColor = INK;
        button.disabled = darkControl.tint(Color.valueOf("696969"));
        button.disabledFontColor = Color.valueOf("71766b");
        skin.add("default", button);
        TextButton.TextButtonStyle primary = new TextButton.TextButtonStyle(button);
        primary.up = enamelControl.tint(Color.valueOf("d8b970"));
        primary.over = enamelControl.tint(Color.valueOf("f0d592"));
        primary.down = enamelControl.tint(Color.valueOf("b99451"));
        primary.fontColor = INK;
        primary.overFontColor = INK;
        primary.downFontColor = INK;
        skin.add("primary", primary);
        TextField.TextFieldStyle field = new TextField.TextFieldStyle(body, PAPER,
            fill(AMBER), fill(LINE), darkControl.tint(Color.WHITE));
        field.focusedBackground = darkControl.tint(Color.valueOf("d8c49d"));
        field.messageFontColor = MUTED;
        skin.add("default", field);
        ScrollPane.ScrollPaneStyle scroll = new ScrollPane.ScrollPaneStyle();
        scroll.vScroll = fill(INK); scroll.vScrollKnob = darkControl.tint(Color.valueOf("868686"));
        skin.add("default", scroll);
        List.ListStyle list = new List.ListStyle(body, PAPER, MUTED, fill(LINE));
        list.background = container();
        skin.add("default", list);
        SelectBox.SelectBoxStyle select = new SelectBox.SelectBoxStyle(body, PAPER, darkControl.tint(Color.WHITE), scroll, list);
        select.backgroundOver = darkControl.tint(Color.valueOf("d8c49d"));
        select.backgroundOpen = select.backgroundOver;
        skin.add("default", select);
        skin.add("default", new Window.WindowStyle(assets.get("ui/heading.ttf", BitmapFont.class), PAPER, container()));
        skin.get(Window.WindowStyle.class).stageBackground = fill(new Color(0, 0, 0, 0.8f));
    }
    public void apply(GameSettings settings) {
        body.getData().setScale(settings.largerText ? 1.15f : 1f);
        mono.getData().setScale(settings.largerText ? 1.1f : 1f);
    }
    public Drawable fill(Color color) { return new TextureRegionDrawable(pixel).tint(color); }
    public Drawable screenFrame() { return screenFrame.tint(Color.WHITE); }
    public Drawable container() { return panelFrame.tint(Color.WHITE); }
    public Drawable panel(Color background, Color border) {
        return panelFrame.tint(border.equals(AMBER) ? Color.valueOf("e3c27b") : Color.WHITE);
    }
    private static NinePatchDrawable patch(Texture texture, int left, int right, int top, int bottom,
                                           float padLeft, float padRight, float padTop, float padBottom) {
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        NinePatchDrawable drawable = new NinePatchDrawable(new NinePatch(texture, left, right, top, bottom));
        drawable.setPadding(padLeft, padRight, padTop, padBottom);
        return drawable;
    }
    @Override public void dispose() { skin.dispose(); }
}
