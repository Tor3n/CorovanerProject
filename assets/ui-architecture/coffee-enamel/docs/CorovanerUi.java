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
        style.fontColor = Color.valueOf("2B1D0C");
        style.overFontColor = style.fontColor;
        style.downFontColor = style.fontColor;
        style.disabledFontColor = Color.valueOf("554B3B");
        style.pressedOffsetY = -1;
        return style;
    }

    /** Idle has no lamp. Hover and keyboard focus reveal the circular overlay. */
    public static final class HoverLampButton extends TextButton {
        private final TextureAtlas.AtlasRegion lamp;

        public HoverLampButton(String text, TextButtonStyle style, TextureAtlas atlas) {
            super(text, style);
            lamp = atlas.findRegion("lamp-on");
            if (lamp == null) throw new IllegalArgumentException("Missing lamp-on");
            // Reserve space symmetrically so text does not jump between states.
            padLeft(48);
            padRight(48);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            boolean focused = getStage() != null && getStage().getKeyboardFocus() == this;
            if (!isDisabled() && (isOver() || focused)) {
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
}
