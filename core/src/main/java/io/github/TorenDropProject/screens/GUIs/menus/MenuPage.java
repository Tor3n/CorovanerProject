package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import io.github.TorenDropProject.menus.*;

/** Common page layout and accessible controls; feature pages own only their view state. */
public abstract class MenuPage {
    protected final MenuController menus;
    protected final MenuTheme theme;
    protected final Table root = new Table();
    protected Table content;
    protected Label notice;
    private Stage stage;
    private final MenuHelp help;
    private final String section, title;

    protected MenuPage(MenuController menus, MenuTheme theme, String section, String title) {
        this.menus = menus;
        this.theme = theme;
        this.section = section;
        this.title = title;
        help = new MenuHelp(theme);
        root.setFillParent(true);
    }
    public final void attach(Stage stage) { this.stage = stage; stage.addActor(root); }
    public void show() { rebuild(); }
    public void hide() { help.hide(); }
    public boolean dismissHelp() { return help.hide(); }
    protected boolean scrollContent() { return true; }
    public void back() { menus.back(); }
    public final void rebuild() {
        String focusedName = stage.getKeyboardFocus() == null ? null : stage.getKeyboardFocus().getName();
        stage.setKeyboardFocus(null);
        help.hide();
        root.clearChildren();
        buildLayout();
        root.validate();
        if (focusedName != null) {
            Actor focused = root.findActor(focusedName);
            if (focused != null && (!(focused instanceof Button) || !((Button)focused).isDisabled())) stage.setKeyboardFocus(focused);
        }
    }
    /** Pages with an illustrated composition can replace the shared header/footer layout. */
    protected void buildLayout() {
        root.pad(28, 34, 22, 34);
        Table header = new Table();
        header.add(label("C / C     COROVANER", "accent")).left();
        header.add(label("FRONTIER FIELD TERMINAL    /    " + section, "mono")).expandX().right();
        root.add(header).growX().padBottom(20).row();
        root.add(rule()).height(1).growX().row();
        Table heading = new Table();
        heading.add(label(title, "heading")).expandX().left();
        if (menus.journey != null) {
            heading.add(label("DAY " + String.format("%02d", menus.journey.day()) + "    /    "
                + menus.catalog.destination(menus.journey.location).name.toUpperCase(), "accent")).expandX().right();
        }
        root.add(heading).growX().padTop(18).padBottom(20).row();
        content = new Table();
        content.top().left();
        ScrollPane scroll = null;
        if (scrollContent()) {
            scroll = new ScrollPane(content, theme.skin);
            scroll.setFadeScrollBars(false);
            scroll.setScrollingDisabled(true, false);
            root.add(scroll).grow().row();
        } else {
            content.setName("bounded-content");
            root.add(content).grow().row();
        }
        build(content);
        notice = label("", "accent");
        notice.setWrap(true);
        root.add(notice).growX().minHeight(24).padTop(10).row();
        root.add(rule()).height(1).growX().padTop(8).padBottom(14).row();
        Table footer = new Table();
        footer.add(button("<  Back", this::back)).width(120).height(38);
        footerActions(footer);
        root.add(footer).growX();
        stage.setScrollFocus(scroll);
    }
    protected void footerActions(Table footer) {
        footer.add(label("THE ROAD REMEMBERS.", "mono")).expandX().right();
    }
    protected abstract void build(Table table);
    protected Label label(String text, String style) { return new Label(text, theme.skin, style); }
    protected Label prose(String text) {
        Label label = label(text, "muted"); label.setWrap(true); label.setAlignment(Align.topLeft); return label;
    }
    protected Actor rule() { return new Image(theme.fill(MenuTheme.LINE)); }
    protected Table panel() {
        Table panel = new Table();
        panel.setBackground(theme.skin.get(TextField.TextFieldStyle.class).background);
        panel.pad(24); panel.top().left();
        return panel;
    }
    protected TextButton button(String text, Runnable action) { return button(text, action, false); }
    protected TextButton button(String text, Runnable action, boolean primary) {
        TextButton button = new TextButton(text, theme.skin, primary ? "primary" : "default");
        button.setName(text);
        button.setProgrammaticChangeEvents(false);
        button.pad(10, 16, 10, 16);
        button.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (!button.isDisabled()) attempt(action);
            }
        });
        return button;
    }
    protected TextButton stat(String title, String value, String explanation) {
        TextButton stat = button(title + "   " + value + "   [?]", () -> { });
        stat.getLabel().setWrap(true);
        stat.getLabel().setAlignment(Align.left);
        stat.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { help.show(stat, title, explanation); }
        });
        help.attach(stat, title, explanation);
        return stat;
    }
    protected <T extends Actor> T hint(T actor, String title, String explanation) {
        help.attach(actor, title, explanation);
        return actor;
    }
    protected void attempt(Runnable action) {
        try { action.run(); }
        catch (RuntimeException failure) {
            notice.setText(failure instanceof IllegalArgumentException || failure instanceof IllegalStateException
                ? failure.getMessage() : "The operation failed. Your current journey is still available.");
            Gdx.app.error("Menus", "Menu operation failed", failure);
        }
    }
    protected float dialogScale() { return 1f; }
    protected void confirm(String title, String message, Runnable action) {
        help.hide();
        float scale = dialogScale();
        Dialog dialog = new Dialog(title, theme.skin) {
            @Override protected void result(Object accepted) {
                if (Boolean.TRUE.equals(accepted)) attempt(action);
            }
        };
        dialog.padTop(Math.max(dialog.getPadTop(), dialog.getTitleLabel().getPrefHeight() + 16));
        Label body = prose(message);
        body.setFontScale(body.getStyle().font.getData().scaleX * scale);
        dialog.getContentTable().pad(28 * scale);
        dialog.getContentTable().add(body).width(480 * scale);
        dialog.getButtonTable().pad(12 * scale).defaults().width(160 * scale).height(48 * scale).pad(6 * scale);
        dialog.button("Cancel", false).button("Confirm", true);
        for (Actor actor : dialog.getButtonTable().getChildren()) if (actor instanceof TextButton) {
            Label caption = ((TextButton)actor).getLabel();
            caption.setFontScale(caption.getStyle().font.getData().scaleX * scale);
        }
        dialog.key(Input.Keys.ESCAPE, false);
        dialog.setMovable(false);
        dialog.show(stage);
        stage.setKeyboardFocus(dialog.getButtonTable().getChildren().first());
    }
    protected void nav(Table table, String label, MenuId id) {
        table.add(button(label, () -> menus.open(id))).growX().height(50).padBottom(8).row();
    }
    protected String signed(int value) { return (value >= 0 ? "+" : "") + value; }
}
