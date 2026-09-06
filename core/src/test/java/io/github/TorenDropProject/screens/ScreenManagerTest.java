package io.github.TorenDropProject.screens;

import org.junit.Test;
import static org.junit.Assert.*;

public class ScreenManagerTest {
    @Test public void returningToBattleUsesLatestSizeAndDisposesEachScreenOnce() {
        ScreenManager manager = new ScreenManager();
        StubScreen battle = new StubScreen(), menu = new StubScreen();
        manager.addGameScreen("battle",battle);
        manager.addGameScreen("battleAlias",battle);
        manager.addGameScreen("menu",menu);
        manager.resize(1100,800);
        manager.setScreen(battle);
        manager.pushScreen(menu);
        assertEquals(1,battle.hides);
        manager.resize(800,1100);
        assertEquals(1100,battle.width);
        manager.popScreen();
        assertEquals(800,battle.width);
        assertEquals(1100,battle.height);
        assertEquals(2,battle.shows);
        assertFalse(manager.canGoBack());
        manager.dispose();
        assertEquals(1,battle.disposals);
        assertEquals(1,menu.disposals);
    }
    private static final class StubScreen implements GameScreen {
        int width,height,shows,hides,disposals;
        @Override public void show() { assertTrue(width > 0 && height > 0); shows++; }
        @Override public void resize(int width,int height) { this.width=width; this.height=height; }
        @Override public void hide() { hides++; }
        @Override public void dispose() { disposals++; }
        @Override public void render(float delta) { }
        @Override public void pause() { }
        @Override public void resume() { }
    }
}
