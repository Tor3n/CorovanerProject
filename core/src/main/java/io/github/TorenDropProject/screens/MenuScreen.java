package io.github.TorenDropProject.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.TorenDropProject.Main;

public class MenuScreen implements GameScreen{

    private SpriteBatch batch;
    private Texture background;

    public MenuScreen(Main main, SpriteBatch batch) {
        this.batch = batch;
        //this.background = new Texture("menu_background.png");
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        //viewport.update(width, height, true);
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
