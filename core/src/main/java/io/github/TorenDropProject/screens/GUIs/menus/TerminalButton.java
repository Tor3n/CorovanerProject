package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

/** Live text over enamel, with an unlit lamp that lights on hover or keyboard focus. */
public final class TerminalButton extends TextButton {
    private final TextureRegion lampOn, lampOff;

    public TerminalButton(String text, TextButtonStyle style, TextureRegion lampOn, TextureRegion lampOff) {
        super(text, style);
        this.lampOn = lampOn;
        this.lampOff = lampOff;
        // Both lamp states occupy the same area, so the caption never moves.
        pad(0, 68, 12, 12);
        setProgrammaticChangeEvents(false);
    }

    public boolean isLampLit() {
        if (isDisabled() || getStage() == null) return false;
        if (isOver()) return true;
        if (getStage().getKeyboardFocus() != this) return false;
        // Pointer hover takes precedence over focus on a different menu action.
        if (getParent() != null) {
            Array<Actor> siblings = getParent().getChildren();
            for (int i = 0; i < siblings.size; i++) {
                Actor sibling = siblings.get(i);
                if (sibling instanceof TerminalButton && !((TerminalButton)sibling).isDisabled()
                    && ((TerminalButton)sibling).isOver()) return false;
            }
        }
        return true;
    }

    public TextureRegion getLampRegion() {
        return isLampLit() ? lampOn : lampOff;
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(getLampRegion(), getX() + 12, getY() + getHeight() - 80 - (isPressed() ? 2 : 0), 70, 70);
        batch.setColor(Color.WHITE);
    }
}
