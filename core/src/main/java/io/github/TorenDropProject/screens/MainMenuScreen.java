package io.github.TorenDropProject.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.TorenDropProject.Main;
import io.github.TorenDropProject.screens.GUIs.BattleScreenGUI;
import io.github.TorenDropProject.screens.GUIs.MainScreenGUI;

public class MainMenuScreen implements GameScreen{

    private SpriteBatch spriteBatch;
    private Texture background;
    public ScreenManager screenManager;
    public Main main;
    public MainScreenGUI mainScreenGUI;
    Viewport viewport;

    public MainMenuScreen(Main main, SpriteBatch spriteBatch, AssetManager assetManager, ScreenManager screenManager) {
        this.main = main;
        this.spriteBatch = spriteBatch;
        this.background = assetManager.get("MainMenu.png", Texture.class);
        this.screenManager = screenManager;

        this.mainScreenGUI = new MainScreenGUI(this, screenManager, spriteBatch);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        spriteBatch.begin();
        spriteBatch.draw(background,0,0,main.viewport.getWorldWidth(),main.viewport.getWorldHeight());
        spriteBatch.end();

        mainScreenGUI.draw(delta);

    }



    @Override
    public void resize(int width, int height) {
        main.viewport.update(width, height, true);
        mainScreenGUI.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
