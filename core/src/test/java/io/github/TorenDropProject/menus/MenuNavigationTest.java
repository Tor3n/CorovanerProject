package io.github.TorenDropProject.menus;

import io.github.TorenDropProject.screens.GameScreen;
import io.github.TorenDropProject.screens.ScreenManager;
import org.junit.Test;
import static org.junit.Assert.*;

public class MenuNavigationTest {
    @Test public void nestedMenusSuspendBattleAndRestoreReturnDestination() {
        ScreenManager manager = new ScreenManager();
        FakeScreen battle = new FakeScreen(), pause = new FakeScreen(), settings = new FakeScreen();
        manager.addGameScreen("battle", battle); manager.addGameScreen("pause", pause); manager.addGameScreen("settings", settings);
        manager.setScreen(battle); manager.render(1);
        manager.pushScreen(pause); manager.pushScreen(settings);
        manager.resize(900, 600); manager.render(1);
        assertEquals(1, battle.frames);
        assertEquals(0, pause.frames);
        manager.popScreen(); manager.render(1);
        assertEquals(1, pause.frames);
        manager.popScreen(); manager.render(1);
        assertEquals(2, battle.frames);
        assertEquals(900, battle.width);
        assertFalse(manager.canGoBack());
        manager.dispose();
        assertEquals(1, battle.disposals); assertEquals(1, pause.disposals); assertEquals(1, settings.disposals);
    }
    @Test public void rootNavigationDropsOldBattleHistory() {
        ScreenManager manager = new ScreenManager();
        FakeScreen battle = new FakeScreen(), travel = new FakeScreen();
        manager.setScreen(battle); manager.pushScreen(travel); manager.setScreen(travel);
        assertFalse(manager.canGoBack());
        manager.popScreen(); manager.render(1);
        assertEquals(0, battle.frames); assertEquals(1, travel.frames);
    }
    private static final class FakeScreen implements GameScreen {
        int frames, disposals, width;
        public void show() { }
        public void render(float delta) { frames++; }
        public void resize(int width, int height) { this.width = width; }
        public void pause() { }
        public void resume() { }
        public void hide() { }
        public void dispose() { disposals++; }
    }
}
