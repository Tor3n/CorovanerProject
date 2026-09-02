package io.github.TorenDropProject.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.TorenDropProject.Main;
import io.github.TorenDropProject.screens.GUIs.MainScreenGUI;

public class MainMenuScreen implements GameScreen{

    private SpriteBatch spriteBatch;
    private Texture background;
    public MainScreenGUI mainScreenGUI;
    public Viewport mainScreenViewport;
    public OrthographicCamera camera;


    public MainMenuScreen(SpriteBatch spriteBatch, AssetManager assetManager, ScreenManager screenManager) {
        this.mainScreenGUI = new MainScreenGUI(screenManager);

        this.spriteBatch = spriteBatch;
        camera = new OrthographicCamera();
        mainScreenViewport = new FillViewport(Main.worldWidth, Main.worldHeight, camera);
        this.background = assetManager.get("MainMenu.png", Texture.class);

        //IT IS SUPER IMPORTANT!!!! Without it the screen goes black
        mainScreenViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

    }


    @Override
    public void show() {
        mainScreenGUI.show();
    }

    @Override
    public void render(float delta) {

        camera.update();
        mainScreenViewport.apply();
        spriteBatch.setProjectionMatrix(camera.combined);


        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, mainScreenViewport.getWorldWidth(),mainScreenViewport.getWorldHeight());
        spriteBatch.end();

        mainScreenGUI.draw(delta);
    }



    @Override
    public void resize(int width, int height) {
        mainScreenViewport.update(width, height, true);
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
        mainScreenGUI.hide();
    }

    @Override
    public void dispose() {
        mainScreenGUI.dispose();
    }
}
