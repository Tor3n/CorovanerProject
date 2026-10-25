package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

/** Live text over a painted enamel face, with one independent round hover/focus lamp. */
public final class TerminalButton extends TextButton {
    private final TextureRegion lamp;

    public TerminalButton(String text, TextButtonStyle style, TextureRegion lamp) {
        super(text, style);
        this.lamp = lamp;
        // Reserve the lamp area even when it is hidden, so the caption never moves.
        pad(0, 68, 12, 12);
        setProgrammaticChangeEvents(false);
    }

    public boolean isLampVisible() {
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

    @Override public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (isLampVisible()) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(lamp, getX() + 12, getY() + getHeight() - 80 - (isPressed() ? 2 : 0), 70, 70);
            batch.setColor(Color.WHITE);
        }
    }
}
