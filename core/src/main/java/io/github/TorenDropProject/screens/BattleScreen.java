package io.github.TorenDropProject.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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

    public BattleScreen(Main main, SpriteBatch spriteBatch, AssetManager assetManager, PlayerEntityFactory playerFactory, ScreenManager screenManager) {
        this.main = main;
        this.assetManager = assetManager;
        this.spriteBatch = spriteBatch;
        this.screenManager = screenManager;
        this.ashleyEngine = playerFactory.ashleyEngine;
        this.battleScreenGUI = new BattleScreenGUI(this, screenManager, spriteBatch);

        grassLandBackground = new GrasslandTestBackGroundLoader(main.assetManager);
        player = playerFactory.createPlayer();

        touchPos = new Vector2();

        devConsoleSpriteBatch = new SpriteBatch();
        devConsoleFont = new BitmapFont();
        devConsoleFont.setColor(Color.GREEN);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(Color.GREEN);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
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

    }

    private void simpleGuiCreate(){

        screenWidth = main.viewport.getScreenWidth();
        screenHeight = main.viewport.getScreenHeight();

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
        float worldWidthIns = main.viewport.getWorldWidth();
        float worldHeightIns = main.viewport.getWorldHeight();
        float delta = Gdx.graphics.getDeltaTime();
    }

    /**
     * Keyboard control
     */
    private void input() {

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

        spriteBatch.begin();
        grassLandBackground.drawBackGround(spriteBatch);
        ashleyEngine.update(delta);

        if (modalActive){
            ModalScreen screen = ScreenManager.manager.getModalScreen(0);
            screen.draw();
        }
        spriteBatch.end();

    }


}
