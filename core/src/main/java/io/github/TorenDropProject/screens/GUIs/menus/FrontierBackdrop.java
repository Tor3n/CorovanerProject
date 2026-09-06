package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import io.github.TorenDropProject.menus.GameSettings;
import io.github.TorenDropProject.menus.MenuTheme;

/** Resolution-independent linework: topographic traces, a pale sun and a ruined frontier. */
public final class FrontierBackdrop extends Actor {
    private final MenuTheme theme;
    private final GameSettings settings;
    private final boolean landscape;
    public FrontierBackdrop(MenuTheme theme, GameSettings settings, boolean landscape) {
        this.theme = theme; this.settings = settings; this.landscape = landscape;
        setTouchable(Touchable.disabled);
    }
    @Override public void draw(Batch batch, float parentAlpha) {
        float w = getStage().getViewport().getWorldWidth(), h = getStage().getViewport().getWorldHeight();
        batch.setColor(MenuTheme.INK); batch.draw(theme.pixel, 0, 0, w, h);
        if (settings.decorations) {
            batch.setColor(0.19f, 0.24f, 0.19f, 0.28f);
            for (int row = 0; row < 23; row++) {
                float oldY = 0;
                for (int x = 0; x < w; x += 16) {
                    float y = row * 42 + MathUtils.sin(x * 0.008f + row * 0.25f) * (20 + row * 2)
                        + MathUtils.cos(x * 0.019f + row) * 8;
                    if (x > 0) line(batch, x - 16, oldY, x, y, 1);
                    oldY = y;
                }
            }
            if (landscape) {
                float cx = w * 0.76f, cy = h * 0.57f;
                batch.setColor(0.63f, 0.47f, 0.24f, 0.45f);
                for (int y = -98; y <= 98; y += 3) {
                    float dx = (float)Math.sqrt(98 * 98 - y * y);
                    batch.draw(theme.pixel, cx - dx, cy + y, dx * 2, 1);
                }
                batch.setColor(0.25f, 0.29f, 0.23f, 0.7f);
                for (int x = 0; x < w; x += 4) {
                    float height = 160 + MathUtils.sin(x * 0.012f) * 34 + MathUtils.cos(x * 0.03f) * 13;
                    batch.draw(theme.pixel, x, 0, 4, height);
                }
                batch.setColor(0.045f, 0.07f, 0.06f, 1);
                float towerX = w * 0.77f;
                line(batch, towerX - 50, 80, towerX, 390, 5);
                line(batch, towerX + 50, 80, towerX, 390, 5);
                for (int y = 120; y < 350; y += 40) {
                    float half = (390 - y) / 6f;
                    line(batch, towerX - half, y, towerX + half, y + 28, 2);
                    line(batch, towerX + half, y, towerX - half, y + 28, 2);
                }
                line(batch, towerX - 62, 330, towerX + 62, 330, 4);
                line(batch, towerX, 390, towerX, 425, 2);
            }
        }
        batch.setColor(Color.WHITE);
    }
    private void line(Batch batch, float x1, float y1, float x2, float y2, float thickness) {
        float dx = x2 - x1, dy = y2 - y1;
        batch.draw(theme.pixel, x1, y1, 0, thickness / 2, (float)Math.sqrt(dx * dx + dy * dy),
            thickness, 1, 1, MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees);
    }
}
