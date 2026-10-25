package io.github.TorenDropProject.menus;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/** References to the independent terminal paintings; the central AssetManager owns them. */
public final class TerminalMenuAssets {
    public static final float WIDTH = 1920, HEIGHT = 1080;
    public static final String ROOT = "ui-architecture/terminal-layers-v1/";
    public static final String PAINTING = ROOT + "layers/01-caravan-painting.png";
    public static final String PANEL = ROOT + "layers/02-hinged-panel.png";
    public static final String RIM = ROOT + "layers/03-metal-rim.png";
    public static final String BUTTON = ROOT + "controls/button-enamel.png";
    public final TextureRegion painting, panel, rim, button, lampOn, lampOff;
    public final BitmapFont font;

    public static void queue(AssetManager assets) {
        for (String path : new String[] {PAINTING, PANEL, RIM, BUTTON}) {
            TextureParameter parameter = new TextureParameter();
            parameter.genMipMaps = true;
            parameter.minFilter = Texture.TextureFilter.MipMapLinearLinear;
            parameter.magFilter = Texture.TextureFilter.Linear;
            assets.load(path, Texture.class, parameter);
        }
    }

    public TerminalMenuAssets(AssetManager assets, CoffeeEnamelAssets components) {
        painting = region(assets, PAINTING);
        panel = region(assets, PANEL);
        rim = region(assets, RIM);
        button = region(assets, BUTTON);
        lampOn = components.region("lamp-on");
        lampOff = components.region("lamp-off");
        font = assets.get("ui/terminal-button.ttf", BitmapFont.class);
        font.getData().setScale(0.5f);
        font.setUseIntegerPositions(false);
    }

    private static TextureRegion region(AssetManager assets, String path) {
        return new TextureRegion(assets.get(path, Texture.class));
    }

    public TextButton.TextButtonStyle buttonStyle() {
        TextureRegionDrawable face = new TextureRegionDrawable(button);
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = MenuTheme.ESPRESSO;
        style.up = face;
        style.over = face.tint(new Color(1f, 0.96f, 0.86f, 1f));
        style.focused = style.over;
        style.down = face.tint(new Color(0.86f, 0.82f, 0.74f, 1f));
        style.disabled = face.tint(new Color(0.62f, 0.60f, 0.56f, 1f));
        style.disabledFontColor = Color.valueOf("494237");
        style.pressedOffsetY = -2;
        return style;
    }
}
