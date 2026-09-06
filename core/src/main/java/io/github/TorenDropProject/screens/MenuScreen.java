package io.github.TorenDropProject.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.github.TorenDropProject.menus.*;
import io.github.TorenDropProject.screens.GUIs.menus.FrontierBackdrop;
import io.github.TorenDropProject.screens.GUIs.menus.MenuPage;

/** Owns the stage lifecycle; all menu features use the same viewport and focus rules. */
public class MenuScreen implements GameScreen {
    private final Stage stage = new Stage(new ExtendViewport(1200, 760));
    private final MenuPage page;
    private final MenuTheme theme;
    private final GameSettings settings;
    public MenuScreen(MenuPage page, MenuTheme theme, GameSettings settings, boolean landscape) {
        this.page = page; this.theme = theme; this.settings = settings;
        stage.addActor(new FrontierBackdrop(theme, settings, landscape));
        page.attach(stage);
        stage.addListener(new InputListener() {
            @Override public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.TAB) { focusNext(); return true; }
                if (keycode == Input.Keys.ESCAPE && !hasDialog()) {
                    if (!page.dismissHelp()) Gdx.app.postRunnable(page::back);
                    return true;
                }
                Actor focused = stage.getKeyboardFocus();
                if ((keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) && focused instanceof Button
                    && !((Button)focused).isDisabled()) {
                    focused.fire(new ChangeEvent()); return true;
                }
                return false;
            }
        });
    }
    @Override public void show() {
        theme.apply(settings);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        page.show();
        Gdx.input.setInputProcessor(stage);
    }
    @Override public void render(float delta) {
        stage.getViewport().apply();
        stage.act(Math.min(delta, 0.1f));
        stage.draw();
    }
    private boolean hasDialog() {
        for (Actor actor : stage.getActors()) if (actor instanceof Dialog) return true;
        return false;
    }
    private void focusNext() {
        Array<Actor> controls = new Array<>();
        com.badlogic.gdx.scenes.scene2d.Group scope = stage.getRoot();
        for (Actor actor : stage.getActors()) if (actor instanceof Dialog) scope = (Dialog)actor;
        collect(scope, controls);
        if (controls.size == 0) return;
        int direction = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ? -1 : 1;
        int index = controls.indexOf(stage.getKeyboardFocus(), true);
        Actor next = controls.get(Math.floorMod(index + direction, controls.size));
        stage.setKeyboardFocus(next);
        for (Actor parent = next.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof ScrollPane) {
                ScrollPane pane = (ScrollPane)parent;
                com.badlogic.gdx.math.Vector2 p = next.localToAscendantCoordinates(pane.getActor(), new com.badlogic.gdx.math.Vector2());
                pane.scrollTo(p.x, p.y, next.getWidth(), next.getHeight());
                break;
            }
        }
    }
    private void collect(com.badlogic.gdx.scenes.scene2d.Group group, Array<Actor> controls) {
        for (Actor actor : group.getChildren()) {
            if (!actor.isVisible()) continue;
            if (actor instanceof Button && !((Button)actor).isDisabled()) controls.add(actor);
            else if (actor instanceof TextField || actor instanceof SelectBox) controls.add(actor);
            else if (actor instanceof com.badlogic.gdx.scenes.scene2d.Group) collect((com.badlogic.gdx.scenes.scene2d.Group)actor, controls);
        }
    }
    @Override public void resize(int width, int height) {
        page.dismissHelp();
        if (width > 0 && height > 0) stage.getViewport().update(width, height, true);
    }
    @Override public void hide() {
        page.hide();
        stage.unfocusAll();
        // Finish closing dialogs before a reusable page is shown again.
        for (Actor actor : new Array<>(stage.getActors())) if (actor instanceof Dialog) actor.remove();
        if (Gdx.input.getInputProcessor() == stage) Gdx.input.setInputProcessor(null);
    }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void dispose() { stage.dispose(); }
}
