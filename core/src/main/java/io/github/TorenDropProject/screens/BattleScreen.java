package io.github.TorenDropProject.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisTextButton;
import io.github.TorenDropProject.Main;
import io.github.TorenDropProject.assetsLoaders.GrasslandTestBackGroundLoader;
import io.github.TorenDropProject.entities.PlayerEntityFactory;
import io.github.TorenDropProject.entities.systems.InputSystem;
import io.github.TorenDropProject.screens.GUIs.BattleScreenGUI;
import io.github.TorenDropProject.screens.modals.ModalScreen;


public class BattleScreen implements GameScreen{
    BattleScreenGUI battleScreenGUI;
    private ScreenManager screenManager;

    private SpriteBatch spriteBatch;
    private Stage stage;

    public Engine ashleyEngine;
    Vector2 touchPos;
    GrasslandTestBackGroundLoader grassLandBackground;
    SpriteBatch devConsoleSpriteBatch;
    BitmapFont devConsoleFont;
    ShapeRenderer shapeRenderer;
    public AssetManager assetManager;
    public Main main;
    Entity player;

    float screenWidth;
    float screenHeight;
    int missed;
    int collected;
    boolean modalActive = false;
    private float stateTime = 0f;
    boolean devconsole = false;
    public Viewport viewport;
    public OrthographicCamera camera;
    public IsometricTiledMapRenderer mapRenderer;
    public TiledMap currentMap;

    public BattleScreen(Main main, SpriteBatch spriteBatch, AssetManager assetManager, PlayerEntityFactory playerFactory, ScreenManager screenManager) {
        this.main = main;
        this.assetManager = assetManager;
        this.spriteBatch = spriteBatch;
        this.screenManager = screenManager;
        this.ashleyEngine = playerFactory.ashleyEngine;
        this.battleScreenGUI = new BattleScreenGUI(this, screenManager, spriteBatch);

        grassLandBackground = new GrasslandTestBackGroundLoader(main.assetManager);
        player = playerFactory.createPlayer();

        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("maps/mountinPass.tmx", TiledMap.class);

        while(!assetManager.isLoaded("maps/mountinPass.tmx")) {
            assetManager.update(5);
        }

        if(assetManager.isFinished()){
            System.out.println("Battle screen is loaded");
            currentMap = assetManager.get("maps/mountinPass.tmx");
        }

        float unitScale = 1 / 32f;
        mapRenderer = new IsometricTiledMapRenderer(currentMap, unitScale);

        touchPos = new Vector2();

        devConsoleSpriteBatch = new SpriteBatch();
        devConsoleFont = new BitmapFont();
        devConsoleFont.setColor(Color.GREEN);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(Color.GREEN);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 30, 20);
        viewport = new ExtendViewport(Main.worldWidth, Main.worldHeight, camera);

        mapRenderer.setView(camera);

        //IT IS SUPER IMPORTANT!!!! Without it the screen goes black
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        input();
        logic();
        draw();
        battleScreenGUI.draw(Gdx.graphics.getDeltaTime());

        if(devconsole){
            simpleGuiCreate();
        }
    }

    @Override
    public void resize(int width, int height) {

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
        assetManager.unload("maps/mountinPass.tmx");
    }

    private void simpleGuiCreate(){

        screenWidth = viewport.getScreenWidth();
        screenHeight = viewport.getScreenHeight();

        devConsoleSpriteBatch.begin();
        devConsoleFont.draw(devConsoleSpriteBatch, "collected: "+collected,screenWidth-screenWidth/6, screenHeight-screenHeight/30);
        devConsoleFont.draw(devConsoleSpriteBatch, "missed: "+missed,screenWidth-screenWidth/6, screenHeight-screenHeight/13);
        devConsoleFont.draw(devConsoleSpriteBatch, "fps: "+ Gdx.graphics.getFramesPerSecond(),screenWidth-screenWidth/6, screenHeight-screenHeight/7);
        devConsoleSpriteBatch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(screenWidth-screenWidth/5,screenHeight-screenHeight/6-2,screenWidth/5,screenHeight/6);
        shapeRenderer.end();
    }

    private void logic() {
        float worldWidthIns = viewport.getWorldWidth();
        float worldHeightIns = viewport.getWorldHeight();
        float delta = Gdx.graphics.getDeltaTime();
    }

    /**
     * Keyboard control
     */
    private void input() {

        PlayerEntityFactory.PositionComponent positionComponent = player.getComponent(PlayerEntityFactory.PositionComponent.class);
        float camX = positionComponent.x;
        float camY = positionComponent.y;
        camera.position.set(camX, camY,0);


        //float camX = camera.position.x;
        //float camY = camera.position.y;

        InputSystem inputSystem = ashleyEngine.getSystem(InputSystem.class);

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            modalActive = !modalActive;
            try{
                Thread.sleep(100);
            } catch (Exception e){
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.F2)){
            devconsole = !devconsole;
            try{
                Thread.sleep(100);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        float speed = 1f;


        /*
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            camera.position.add(0,-speed,0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            camera.position.add(0, +speed,0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            camera.position.add(-speed, 0,0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            camera.position.add(+speed, 0,0);
        } */

        System.out.println("camera position: "+ camera.position.x+", y: "+camera.position.y);

        camera.update();

        if(isInputEntityInput()){
            inputSystem.setInputEntityRelated(true);
        } else {

        }


    }

    private boolean isInputEntityInput() {
        return true;
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;


        mapRenderer.setView(camera);
        mapRenderer.render();

        spriteBatch.begin();
        //grassLandBackground.drawBackGround(spriteBatch);

        ashleyEngine.update(delta);


        if (modalActive){
            ModalScreen screen = ScreenManager.manager.getModalScreen(0);
            screen.draw();
        }
        spriteBatch.end();

    }


}
