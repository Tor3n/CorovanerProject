import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/** Small integration helper. The caller owns the atlas and font lifetimes. */
public final class CorovanerUi {
    public static final float UI_SCALE = 0.5f;

    private CorovanerUi() {}

    public static Drawable surface(TextureAtlas atlas, String name) {
        NinePatch patch = atlas.createPatch(name);
        if (patch == null) throw new IllegalArgumentException("Missing surface: " + name);
        patch.scale(UI_SCALE, UI_SCALE);
        return new NinePatchDrawable(patch);
    }

    public static TextButton.TextButtonStyle primaryStyle(TextureAtlas atlas, BitmapFont font) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = surface(atlas, "button-enamel-up");
        style.over = surface(atlas, "button-enamel-over");
        style.focused = style.over;
        style.down = surface(atlas, "button-enamel-down");
        NinePatch disabled = atlas.createPatch("button-enamel-up");
        disabled.scale(UI_SCALE, UI_SCALE);
        disabled.setColor(Color.valueOf("888078"));
        style.disabled = new NinePatchDrawable(disabled);
        style.font = font;
        style.fontColor = Color.valueOf("21160B");
        style.overFontColor = style.fontColor;
        style.downFontColor = style.fontColor;
        style.disabledFontColor = Color.valueOf("554B3B");
        style.pressedOffsetY = -1;
        return style;
    }

    /** Idle/disabled show lamp-off. Hover and keyboard focus switch to lamp-on. */
    public static final class HoverLampButton extends TextButton {
        private final TextureAtlas.AtlasRegion lampOn, lampOff;

        public HoverLampButton(String text, TextButtonStyle style, TextureAtlas atlas) {
            super(text, style);
            lampOn = atlas.findRegion("lamp-on");
            lampOff = atlas.findRegion("lamp-off");
            if (lampOn == null || lampOff == null) throw new IllegalArgumentException("Missing lamp sprites");
            // Reserve space symmetrically so text does not jump between states.
            padLeft(48);
            padRight(48);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            boolean focused = getStage() != null && getStage().getKeyboardFocus() == this;
            TextureAtlas.AtlasRegion lamp = !isDisabled() && (isOver() || focused) ? lampOn : lampOff;
            float oldColor = batch.getPackedColor();
            Color actorColor = getColor();
            batch.setColor(actorColor.r, actorColor.g, actorColor.b, actorColor.a * parentAlpha);
            float size = 28;
            float pressed = isPressed() ? -1 : 0;
            batch.draw(lamp, getX() + 15, getY() + (getHeight() - size) / 2 + pressed, size, size);
            batch.setPackedColor(oldColor);
        }
    }
}
