package io.github.TorenDropProject.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.TorenDropProject.Main;
import io.github.TorenDropProject.entities.PlayerEntityFactory;
import io.github.TorenDropProject.screens.GUIs.BattleScreenGUI;
import io.github.TorenDropProject.screens.modals.ModalScreen;


public class BattleScreen implements GameScreen{
    BattleScreenGUI battleScreenGUI;
    private ScreenManager screenManager;

    private SpriteBatch spriteBatch;
    public Engine ashleyEngine;
    SpriteBatch devConsoleSpriteBatch;
    BitmapFont devConsoleFont;
    ShapeRenderer shapeRenderer;
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

    public BattleScreen(SpriteBatch spriteBatch, AssetManager assetManager, Engine ashleyEngine,
                        PlayerEntityFactory playerFactory, ScreenManager screenManager) {
        this.spriteBatch = spriteBatch;
        this.screenManager = screenManager;
        this.ashleyEngine = ashleyEngine;
        this.battleScreenGUI = new BattleScreenGUI(screenManager);

        player = playerFactory.createPlayer();

        currentMap = assetManager.get("maps/mountinPass.tmx", TiledMap.class);

        float unitScale = 1 / 32f;
        mapRenderer = new IsometricTiledMapRenderer(currentMap, unitScale);

        devConsoleSpriteBatch = new SpriteBatch();
        devConsoleFont = new BitmapFont();
        devConsoleFont.setColor(Color.GREEN);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(Color.GREEN);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 30, 20);
        viewport = new ExtendViewport(Main.worldWidth, Main.worldHeight, camera);

        mapRenderer.setView(camera);

        //TODO scan for init position (special tile)
        MapLayers layers = currentMap.getLayers();
        //scan for cell with specital id - init position
        //addd enum for special positions
        //layers.get(0)
        TiledMapTileLayer.Cell  cell;
        //TiledMapTile tile = cell.getTile();

        //if (tile.getId()==) {

       // }

        //IT IS SUPER IMPORTANT!!!! Without it the screen goes black
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void show() {
        battleScreenGUI.show();
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        input();
        draw(delta);
        battleScreenGUI.draw(delta);

        if(devconsole){
            simpleGuiCreate();
        }
        if (modalActive) {
            ModalScreen screen = screenManager.getModalScreen(0);
            screen.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        battleScreenGUI.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        battleScreenGUI.hide();
    }

    @Override
    public void dispose() {
        battleScreenGUI.dispose();
        mapRenderer.dispose();
        devConsoleSpriteBatch.dispose();
        devConsoleFont.dispose();
        shapeRenderer.dispose();
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

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            System.out.println("escape pressed!");
            modalActive = !modalActive;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F2)){
            devconsole = !devconsole;
        }

        camera.update();

    }

    private void draw(float delta) {
        ScreenUtils.clear(Color.BLACK);
        stateTime += delta;


        mapRenderer.setView(camera);
        mapRenderer.render();

        spriteBatch.begin();
        ashleyEngine.update(delta);
        spriteBatch.end();

    }


}
