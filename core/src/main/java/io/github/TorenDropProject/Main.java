package io.github.TorenDropProject;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.TorenDropProject.pregame.SplashPseudoScreen;
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
    SplashPseudoScreen splashPseudoScreen;
    ScreenManager screenManager;

    @Override
    public void create() {
        worldWidth=16;
        worldHeight=10;

        assetManager = new AssetManager();
        splashPseudoScreen = new SplashPseudoScreen(assetManager).loadSplash();
        screenManager = new ScreenManager();
        spriteBatch = new SpriteBatch();
        BattleScreen battleScreen = new BattleScreen(this, spriteBatch);
        MenuScreen menuScreen = new MenuScreen(this, spriteBatch);
        ModalScreen mainModal = new MainModal(this, spriteBatch);

        screenManager.addGameScreen(battleScreen);
        screenManager.addGameScreen(menuScreen);

        screenManager.addModalScreen(mainModal);


        screenManager.setScreen(battleScreen);
    }

    @Override
    public void resize(int width, int height) {
        // Resize your application here. The parameters represent the new window size.
        screenManager.resize(width, height);
    }

    @Override
    public void render() {
        preload();
        screenManager.render(Gdx.graphics.getDeltaTime());
    }

    //TODO refactor preloadScreen
    private void preload() {
        if (assetsLoaded){
            //drawMenue();
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
