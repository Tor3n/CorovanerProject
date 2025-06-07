package io.github.TorenDropProject.screens;


import io.github.TorenDropProject.screens.modals.ModalScreen;

import java.util.ArrayList;
import java.util.HashMap;

public class ScreenManager {
    static ScreenManager manager;
    private GameScreen currentScreen;
    HashMap<String, GameScreen> gameScreens;
    ArrayList<ModalScreen> modalScreens;

    public ScreenManager() {
        gameScreens = new HashMap<>();
        modalScreens = new ArrayList<>();
        manager = this;
    }

    public static ScreenManager getScreenManager(){
        return manager;
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
        if (currentScreen != null) {
            currentScreen.hide();
            currentScreen.dispose();
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
            currentScreen.dispose();
        }
    }
}
