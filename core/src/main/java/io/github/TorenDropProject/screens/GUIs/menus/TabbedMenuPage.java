package io.github.TorenDropProject.screens.GUIs.menus;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.TorenDropProject.menus.MenuController;
import io.github.TorenDropProject.menus.MenuTheme;

/** Bounded sections with persistent tab and previous/next navigation. */
public abstract class TabbedMenuPage extends MenuPage {
    protected int tab;
    protected TabbedMenuPage(MenuController menus, MenuTheme theme, String section, String title) {
        super(menus, theme, section, title);
    }
    protected abstract String[] tabs();
    protected abstract void buildTab(Table table);
    @Override protected boolean scrollContent() { return false; }
    @Override protected final void build(Table table) {
        String[] names = tabs();
        tab = Math.min(tab, names.length - 1);
        Table navigation = new Table();
        for (int i = 0; i < names.length; i++) {
            final int destination = i;
            TextButton choice = button(names[i], () -> { tab = destination; rebuild(); });
            choice.setChecked(i == tab);
            navigation.add(choice).growX().height(42).padRight(i == names.length - 1 ? 0 : 8);
        }
        table.add(navigation).growX().padBottom(16).row();
        Table body = new Table(); body.top().left(); body.setName("page-body");
        table.add(body).grow();
        buildTab(body);
    }
    @Override protected void footerActions(Table footer) {
        pageNavigation(footer);
        footer.add(label("HOVER OR FOCUS [?] FOR DETAILS", "mono")).expandX().right();
    }
    protected final void pageNavigation(Table footer) {
        TextButton previous = button("< Previous", () -> { tab--; rebuild(); });
        previous.setDisabled(tab == 0);
        footer.add(previous).height(38).padLeft(16);
        footer.add(label((tab + 1) + " / " + tabs().length, "accent")).padLeft(16).padRight(16);
        TextButton next = button("Next >", () -> { tab++; rebuild(); });
        next.setDisabled(tab == tabs().length - 1);
        footer.add(next).height(38);
    }
}
