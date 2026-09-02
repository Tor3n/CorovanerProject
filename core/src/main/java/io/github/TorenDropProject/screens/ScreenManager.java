package io.github.TorenDropProject.screens;


import io.github.TorenDropProject.screens.modals.ModalScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class ScreenManager {
    private GameScreen currentScreen;
    private final HashMap<String, GameScreen> gameScreens;
    private final ArrayList<ModalScreen> modalScreens;

    public ScreenManager() {
        gameScreens = new HashMap<>();
        modalScreens = new ArrayList<>();
    }

    public void addGameScreen(String name, GameScreen gameScreen) {
        this.gameScreens.put(name, gameScreen);
    }

    public GameScreen getGameScreen(String name) {
        return this.gameScreens.get(name);
    }

    public void addModalScreen(ModalScreen modalScreen) {
        this.modalScreens.add(modalScreen);
    }

    public ModalScreen getModalScreen(int pos) {
        return this.modalScreens.get(pos);
    }

    public void setScreen(GameScreen screen) {
        if (currentScreen == screen) {
            return;
        }
        if (currentScreen != null) {
            currentScreen.hide();
        }
        currentScreen = screen;
        if (currentScreen != null) {
            currentScreen.show();
        }
    }

    public void render(float delta) {
        if (currentScreen != null) {
            currentScreen.render(delta);
        }
    }

    public void resize(int width, int height) {
        if (currentScreen != null) {
            currentScreen.resize(width, height);
        }
        for (ModalScreen modalScreen : modalScreens) {
            modalScreen.resize(width, height);
        }
    }

    public void pause() {
        if (currentScreen != null) {
            currentScreen.pause();
        }
    }

    public void resume() {
        if (currentScreen != null) {
            currentScreen.resume();
        }
    }

    public void dispose() {
        if (currentScreen != null) {
            currentScreen.hide();
        }

        Set<GameScreen> uniqueScreens = new LinkedHashSet<>(gameScreens.values());
        if (currentScreen != null) {
            uniqueScreens.add(currentScreen);
        }
        for (GameScreen screen : uniqueScreens) {
            screen.dispose();
        }
        for (ModalScreen modalScreen : modalScreens) {
            modalScreen.dispose();
        }

        currentScreen = null;
        gameScreens.clear();
        modalScreens.clear();
    }
}
