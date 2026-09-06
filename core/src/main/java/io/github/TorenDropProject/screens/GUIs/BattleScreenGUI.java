package io.github.TorenDropProject.screens.GUIs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.TorenDropProject.menus.*;

/** Lightweight gameplay HUD. Shared theme resources are owned by Main. */
public final class BattleScreenGUI {
    private final Stage stage = new Stage(new ExtendViewport(1200, 760));
    private final MenuController menus;
    private final MenuTheme theme;
    private final Label identity;
    private final Label location;
    private final Label feedback;
    private InputProcessor input;
    public BattleScreenGUI(MenuController menus, MenuTheme theme) {
        this.menus = menus; this.theme = theme;
        Table root = new Table(); root.setFillParent(true); root.pad(18);
        identity = new Label("", theme.skin, "accent");
        Table top = new Table(); top.setBackground(theme.container()); top.pad(14);
        top.setTouchable(Touchable.enabled);
        top.add(identity).expandX().left();
        location = new Label("", theme.skin, "mono");
        top.add(location).right();
        root.add(top).growX().top().row();
        root.add().expand().row();
        feedback = new Label("Click a scout to select; click the ground to move.", theme.skin, "accent");
        root.add(feedback).left().padBottom(10).row();
        Table bottom = new Table(); bottom.setBackground(theme.container()); bottom.pad(12);
        bottom.setTouchable(Touchable.enabled);
        add(bottom, "Esc / Pause", menus::pause);
        add(bottom, "C / Character", () -> menus.open(MenuId.CHARACTER));
        add(bottom, "I / Inventory", () -> menus.open(MenuId.INVENTORY));
        add(bottom, "J / Journal", () -> menus.open(MenuId.JOURNAL));
        add(bottom, "M / Travel", () -> menus.open(MenuId.TRAVEL));
        bottom.add(new Label("Click: Select / Move   |   +/-: Zoom", theme.skin, "mono")).expandX().right();
        root.add(bottom).growX(); stage.addActor(root);
    }
    private void add(Table table, String text, Runnable action) {
        TextButton button = new TextButton(text, theme.skin);
        button.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { action.run(); }
        });
        table.add(button).height(44).padRight(8);
    }
    public void show(InputProcessor worldInput) {
        theme.apply(menus.settings);
        if (menus.journey != null) identity.setText(menus.journey.character.name.toUpperCase() + " / "
            + menus.catalog.archetype(menus.journey.character.archetype).name.toUpperCase());
        if (menus.journey != null) location.setText(menus.catalog.destination(menus.journey.location).name.toUpperCase());
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        input = new InputMultiplexer(stage, worldInput);
        Gdx.input.setInputProcessor(input);
    }
    public void setWorldMessage(String text) { feedback.setText(text); }
    public boolean containsScreenPoint(int x, int y) {
        Vector2 point = stage.screenToStageCoordinates(new Vector2(x, y));
        Actor hit = stage.hit(point.x, point.y, true);
        return hit != null && hit != stage.getRoot();
    }
    public void draw(float delta) { stage.getViewport().apply(); stage.act(delta); stage.draw(); }
    public boolean hasKeyboardFocus() { return stage.getKeyboardFocus() instanceof TextField; }
    public void hide() {
        stage.unfocusAll();
        if (Gdx.input.getInputProcessor() == input) Gdx.input.setInputProcessor(null);
    }
    public void resize(int width, int height) { if (width > 0 && height > 0) stage.getViewport().update(width, height, true); }
    public void dispose() { stage.dispose(); }
}
