package io.github.TorenDropProject.screens;

import com.badlogic.gdx.Game;
import io.github.TorenDropProject.screens.modals.ModalScreen;

import java.util.ArrayList;

public class ScreenManager {
    static ScreenManager manager;
    private GameScreen currentScreen;
    ArrayList<GameScreen> gameScreens;
    ArrayList<ModalScreen> modalScreens;

    public ScreenManager() {
        gameScreens = new ArrayList<>();
        modalScreens = new ArrayList<>();
        manager = this;
    }

    public static ScreenManager getScreenManager(){
        return manager;
    }

    public void addGameScreen(GameScreen gameScreen) {
        this.gameScreens.add(gameScreen);
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
