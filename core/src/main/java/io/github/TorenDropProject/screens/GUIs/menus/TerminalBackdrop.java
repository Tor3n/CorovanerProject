package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import io.github.TorenDropProject.menus.GameSettings;
import io.github.TorenDropProject.menus.MenuTheme;
import io.github.TorenDropProject.menus.TerminalMenuAssets;

/** Subdued painted surroundings behind the centered, uncropped 16:9 terminal. */
public final class TerminalBackdrop extends Actor {
    private final MenuTheme theme;
    private final GameSettings settings;

    public TerminalBackdrop(MenuTheme theme, GameSettings settings) {
        this.theme = theme;
        this.settings = settings;
        setTouchable(Touchable.disabled);
    }

    @Override public void draw(Batch batch, float parentAlpha) {
        float width = getStage().getViewport().getWorldWidth();
        float height = getStage().getViewport().getWorldHeight();
        batch.setColor(0.07f, 0.06f, 0.045f, parentAlpha);
        batch.draw(theme.pixel, 0, 0, width, height);
        if (settings.decorations) {
            float scale = Math.max(width / TerminalMenuAssets.WIDTH, height / TerminalMenuAssets.HEIGHT);
            float paintedWidth = TerminalMenuAssets.WIDTH * scale;
            float paintedHeight = TerminalMenuAssets.HEIGHT * scale;
            batch.setColor(0.32f, 0.29f, 0.25f, parentAlpha);
            batch.draw(theme.terminal.painting, (width - paintedWidth) / 2, (height - paintedHeight) / 2,
                paintedWidth, paintedHeight);
        }
        batch.setColor(Color.WHITE);
    }
}
