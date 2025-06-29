package io.github.TorenDropProject;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import io.github.TorenDropProject.entities.systems.InputSystem;
import io.github.TorenDropProject.entities.systems.MovementSystem;
import io.github.TorenDropProject.entities.PlayerEntityFactory;
import io.github.TorenDropProject.entities.systems.RenderSystem;
import io.github.TorenDropProject.pregame.SplashScreenAssetLoader;
import io.github.TorenDropProject.screens.BattleScreen;
import io.github.TorenDropProject.screens.MainMenuScreen;
import io.github.TorenDropProject.screens.ScreenManager;
import io.github.TorenDropProject.screens.modals.MainModal;
import io.github.TorenDropProject.screens.modals.ModalScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {

    private int screenW;
    private int screenH;

    public static int worldWidth;
    public static int worldHeight;
    public AssetManager assetManager;
    SpriteBatch spriteBatch;
    boolean assetsLoaded = false;
    boolean postLoadedComplete = false;
    SplashScreenAssetLoader splashPseudoScreen;
    ScreenManager screenManager;
    Engine ashleyEngine;
    public OrthographicCamera camera;
    public PlayerEntityFactory entityFactory;
    MovementSystem playerMovementSystem;
    RenderSystem playerRenderSystem;
    InputSystem playerInputSystem;
    Pixmap arrowPixmap;

    public Main(int w, int h){
        screenW = w;
        screenH = h;
    }

    @Override
    public void create() {

        worldWidth=32;
        worldHeight=20;

        VisUI.load();

        assetManager = new AssetManager();
        splashPseudoScreen = new SplashScreenAssetLoader(assetManager).loadAssets();

        screenManager = new ScreenManager();
        spriteBatch = new SpriteBatch();
    }

    private boolean postloaded(){
        createCursors();

        ashleyEngine = new Engine();
        entityFactory = new PlayerEntityFactory(ashleyEngine, assetManager);
        playerMovementSystem = new MovementSystem();
        playerRenderSystem = new RenderSystem(spriteBatch);
        playerInputSystem = new InputSystem();

        ashleyEngine.addSystem(playerMovementSystem);
        ashleyEngine.addSystem(playerRenderSystem);
        ashleyEngine.addSystem(playerInputSystem);

        //menu screens
        MainMenuScreen mainMenuScreen = new MainMenuScreen(this, spriteBatch, assetManager, screenManager);
        ModalScreen mainModal = new MainModal(this, spriteBatch);

        //battle screens
        BattleScreen battleScreen = new BattleScreen(this, spriteBatch, assetManager, entityFactory, screenManager);

        screenManager.addGameScreen("BattleScreen", battleScreen);
        screenManager.addGameScreen("MainMenu", mainMenuScreen);
        screenManager.addModalScreen(mainModal);
        screenManager.setScreen(mainMenuScreen);

        return true;
    }

    @Override
    public void resize(int width, int height) {
        screenManager.resize(width, height);
    }

    @Override
    public void render() {
        preload();
        if(postLoadedComplete){
            screenManager.render(Gdx.graphics.getDeltaTime());
        }
    }

    private void preload() {
        if (assetsLoaded){
            ScreenUtils.clear(Color.BLACK);
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
        VisUI.dispose();
        // Destroy application's resources here.
    }


    private void createCursors() {
        arrowPixmap = assetManager.get("cursors/arrowCursor3.png", Pixmap.class);
        Cursor arrowCursor = Gdx.graphics.newCursor(arrowPixmap, 0, 0);
        Gdx.graphics.setCursor(arrowCursor);
    }
}
