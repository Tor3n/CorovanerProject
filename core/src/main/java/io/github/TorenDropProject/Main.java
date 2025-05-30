package io.github.TorenDropProject;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.TorenDropProject.entities.EntityLoader;
import io.github.TorenDropProject.entities.PlayerEntityCreator;
import io.github.TorenDropProject.pregame.SplashScreenAssetLoader;
import io.github.TorenDropProject.screens.BattleScreen;
import io.github.TorenDropProject.screens.MenuScreen;
import io.github.TorenDropProject.screens.ScreenManager;
import io.github.TorenDropProject.screens.modals.MainModal;
import io.github.TorenDropProject.screens.modals.ModalScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    public static int worldWidth;
    public static int worldHeight;
    public AssetManager assetManager;
    SpriteBatch spriteBatch;
    boolean assetsLoaded = false;
    boolean postLoadedComplete = false;
    SplashScreenAssetLoader splashPseudoScreen;
    ScreenManager screenManager;
    public FitViewport viewport;
    Engine ashleyEngine;
    OrthographicCamera camera;
    PlayerEntityCreator player;

    @Override
    public void create() {
        worldWidth=16;
        worldHeight=10;

        assetManager = new AssetManager();
        splashPseudoScreen = new SplashScreenAssetLoader(assetManager).loadAssets();

        screenManager = new ScreenManager();
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(Main.worldWidth, Main.worldHeight, camera);

    }

    private boolean postloaded(){

        ashleyEngine = new Engine();
        player = new PlayerEntityCreator(ashleyEngine, assetManager);
        player.createPlayer();

        BattleScreen battleScreen = new BattleScreen(this, spriteBatch, assetManager, player.loader.testWalk);
        MenuScreen menuScreen = new MenuScreen(this, spriteBatch);
        ModalScreen mainModal = new MainModal(this, spriteBatch);

        screenManager.addGameScreen(battleScreen);
        screenManager.addGameScreen(menuScreen);
        screenManager.addModalScreen(mainModal);
        screenManager.setScreen(battleScreen);

        return true;
    }

    @Override
    public void resize(int width, int height) {
        // Resize your application here. The parameters represent the new window size.
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        //This better be here.
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        preload();
        screenManager.render(Gdx.graphics.getDeltaTime());
    }

    //TODO refactor preloadScreen
    private void preload() {
        if (assetsLoaded){
            if(!postLoadedComplete){
                postLoadedComplete = postloaded();
            }
        } else {
            assetsLoaded = splashPseudoScreen.preloaded(spriteBatch);
        }
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        assetManager.clear();
        spriteBatch.dispose();
        // Destroy application's resources here.
    }
}
