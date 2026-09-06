package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import io.github.TorenDropProject.menus.MenuTheme;

/** One bounded tooltip per page. No shared timers or actors survive page navigation. */
final class MenuHelp {
    private final MenuTheme theme;
    private Table popup;
    private Actor owner;
    MenuHelp(MenuTheme theme) { this.theme = theme; }

    void attach(Actor target, String title, String explanation) {
        target.addListener(new InputListener() {
            @Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) show(target, title, explanation);
            }
            @Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (toActor != null && toActor.isDescendantOf(target)) return;
                if (owner == target && target.getStage() != null && target.getStage().getKeyboardFocus() != target) hide();
            }
        });
        target.addListener(new FocusListener() {
            @Override public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) show(target, title, explanation);
                else if (owner == target) hide();
            }
        });
    }
    void show(Actor target, String title, String explanation) {
        Stage stage = target.getStage();
        if (stage == null) return;
        hide();
        owner = target;
        popup = new Table();
        popup.setName("stat-tooltip");
        popup.setTouchable(Touchable.disabled);
        popup.setBackground(theme.skin.get(TextField.TextFieldStyle.class).focusedBackground);
        popup.pad(18);
        Label heading = new Label(title.toUpperCase(), theme.skin, "accent");
        heading.setWrap(true);
        Label body = new Label(explanation, theme.skin);
        body.setWrap(true);
        float width = Math.min(350, stage.getWidth() - 60);
        popup.add(heading).width(width).left().padBottom(10).row();
        popup.add(body).width(width).left();
        popup.pack();
        Vector2 anchor = target.localToStageCoordinates(new Vector2(target.getWidth(), target.getHeight()));
        float x = anchor.x + 12;
        if (x + popup.getWidth() > stage.getWidth() - 12) x = anchor.x - target.getWidth() - popup.getWidth() - 12;
        popup.setPosition(MathUtils.clamp(x, 12, stage.getWidth() - popup.getWidth() - 12),
            MathUtils.clamp(anchor.y - popup.getHeight(), 12, stage.getHeight() - popup.getHeight() - 12));
        stage.addActor(popup);
    }
    boolean hide() {
        boolean visible = popup != null;
        if (popup != null) popup.remove();
        popup = null; owner = null;
        return visible;
    }
}
