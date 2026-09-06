package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.TorenDropProject.menus.*;
import java.util.function.Consumer;

/** Interactive graph, drawn from the same destination/route catalog used by travel rules. */
public final class TravelMap extends WidgetGroup {
    private final MenuTheme theme;
    private final FrontierCatalog catalog;
    private final String current, selected;
    public TravelMap(MenuTheme theme, FrontierCatalog catalog, String current, String selected, Consumer<String> choose) {
        this.theme = theme; this.catalog = catalog; this.current = current; this.selected = selected;
        for (FrontierCatalog.Destination destination : catalog.destinations) {
            TextButton marker = new TextButton(destination.name, theme.skin);
            marker.setName(destination.id);
            marker.setChecked(destination.id.equals(selected));
            marker.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) { choose.accept(destination.id); }
            });
            addActor(marker);
        }
    }
    @Override public void layout() {
        for (Actor marker : getChildren()) {
            FrontierCatalog.Destination d = catalog.destination(marker.getName());
            marker.setBounds(MathUtils.clamp(d.x * getWidth() - 94, 8, getWidth() - 196), d.y * getHeight() - 55, 188, 44);
        }
    }
    @Override public float getPrefWidth() { return 650; }
    @Override public float getPrefHeight() { return 410; }
    @Override public void draw(Batch batch, float parentAlpha) {
        validate();
        float x = getX(), y = getY(), w = getWidth(), h = getHeight();
        batch.setColor(MenuTheme.INK); batch.draw(theme.pixel, x, y, w, h);
        batch.setColor(0.24f, 0.31f, 0.25f, 0.5f);
        for (int gx = 0; gx < w; gx += 40) batch.draw(theme.pixel, x + gx, y, 1, h);
        for (int gy = 0; gy < h; gy += 40) batch.draw(theme.pixel, x, y + gy, w, 1);
        batch.setColor(0.35f, 0.39f, 0.28f, 0.7f);
        for (int ridge = 0; ridge < 8; ridge++) {
            for (int t = 0; t < 75; t++) {
                float ax = w * (0.34f + ridge * 0.012f) + MathUtils.sin(t * 0.13f) * 16;
                float ay = t / 75f * h;
                line(batch, x + ax, y + ay, x + ax + 3, y + ay + 5, 1);
            }
        }
        for (FrontierCatalog.Route route : catalog.routes) {
            FrontierCatalog.Destination from = catalog.destination(route.from), to = catalog.destination(route.to);
            batch.setColor(route.connects(current, selected) ? MenuTheme.AMBER : MenuTheme.LINE);
            for (int i = 0; i < 24; i += 2) {
                float a = i / 24f, b = (i + 1) / 24f;
                line(batch, x + MathUtils.lerp(from.x, to.x, a) * w, y + MathUtils.lerp(from.y, to.y, a) * h,
                    x + MathUtils.lerp(from.x, to.x, b) * w, y + MathUtils.lerp(from.y, to.y, b) * h, 2);
            }
        }
        for (FrontierCatalog.Destination destination : catalog.destinations) {
            float dx = x + destination.x * w, dy = y + destination.y * h;
            batch.setColor(destination.id.equals(current) ? MenuTheme.GREEN : MenuTheme.AMBER);
            batch.draw(theme.pixel, dx - 5, dy - 5, 10, 10);
            if (destination.id.equals(current)) {
                line(batch, dx - 10, dy - 10, dx + 10, dy - 10, 1);
                line(batch, dx - 10, dy + 10, dx + 10, dy + 10, 1);
                line(batch, dx - 10, dy - 10, dx - 10, dy + 10, 1);
                line(batch, dx + 10, dy - 10, dx + 10, dy + 10, 1);
            }
        }
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }
    private void line(Batch batch, float x1, float y1, float x2, float y2, float thickness) {
        float dx = x2 - x1, dy = y2 - y1;
        batch.draw(theme.pixel, x1, y1, 0, thickness / 2, (float)Math.sqrt(dx * dx + dy * dy), thickness,
            1, 1, MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees);
    }
}
