package io.github.TorenDropProject;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import io.github.TorenDropProject.characters.CharacterDefinition;
import io.github.TorenDropProject.characters.CharacterFactory;
import io.github.TorenDropProject.world.WorldSceneFactory;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.TorenDropProject.pregame.SplashScreenAssetLoader;
import io.github.TorenDropProject.screens.BattleScreen;
import io.github.TorenDropProject.screens.development.CharacterPreviewScreen;
import io.github.TorenDropProject.screens.ScreenManager;
import io.github.TorenDropProject.menus.*;

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
    Pixmap arrowPixmap;
    Cursor arrowCursor;
    MenuTheme menuTheme;
    MenuController menus;

    public Main(int w, int h){
        screenW = w;
        screenH = h;
    }

    @Override
    public void create() {

        worldWidth=32;
        worldHeight=20;

        assetManager = new AssetManager();
        splashPseudoScreen = new SplashScreenAssetLoader(assetManager).loadAssets();

        screenManager = new ScreenManager();
        spriteBatch = new SpriteBatch();
    }

    private boolean postloaded(){
        createCursors();

        CharacterDefinition definition = CharacterDefinition.VAULT_DWELLER;
        CharacterFactory factory = new CharacterFactory(definition, assetManager.get(definition.modelPath, Model.class));
        boolean previewMode = Boolean.getBoolean("corovaner.characterPreview");
        WorldSceneFactory scenes = new WorldSceneFactory(assetManager, factory);
        FrontierCatalog catalog = assetManager.get(FrontierCatalog.PATH, FrontierCatalog.class);
        if (previewMode) {
            CharacterPreviewScreen preview = new CharacterPreviewScreen(scenes.create(catalog.destination("pass").worldMap, false));
            screenManager.addGameScreen("CharacterPreview", preview);
            screenManager.setScreen(preview);
            return true;
        }

        menuTheme = new MenuTheme(assetManager);
        GameSettings settings = new GameSettings(Gdx.app.getPreferences("corovaner.settings"));
        menus = new MenuController(screenManager, catalog,
            new JourneyStore(Gdx.app.getPreferences("corovaner.journeys"), catalog), settings);
        settings.apply(settings.fullscreen, settings.vsync, settings.largerText, settings.decorations);
        MenuRegistry.register(menus, menuTheme);
        BattleScreen battleScreen = new BattleScreen(scenes, menus, menuTheme);
        screenManager.addGameScreen("BattleScreen", battleScreen);
        menus.root(MenuId.MAIN);

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
        screenManager.pause();
    }

    @Override
    public void resume() {
        screenManager.resume();
    }

    @Override
    public void dispose() {
        screenManager.dispose();
        if (arrowCursor != null) {
            arrowCursor.dispose();
        }
        spriteBatch.dispose();
        if (menuTheme != null) menuTheme.dispose();
        assetManager.dispose();
    }


    private void createCursors() {
        arrowPixmap = assetManager.get("cursors/arrowCursor3.png", Pixmap.class);
        arrowCursor = Gdx.graphics.newCursor(arrowPixmap, 0, 0);
        Gdx.graphics.setCursor(arrowCursor);
    }
}
