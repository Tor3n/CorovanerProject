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
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.TorenDropProject.Main;
import io.github.TorenDropProject.assetsLoaders.GrasslandTestBackGroundLoader;
import io.github.TorenDropProject.entities.PlayerEntityFactory;
import io.github.TorenDropProject.entities.textureLoaders.Directions;
import io.github.TorenDropProject.screens.modals.ModalScreen;


public class BattleScreen implements GameScreen{
    private SpriteBatch spriteBatch;

    public Engine ashleyEngine;
    Vector2 touchPos;
    GrasslandTestBackGroundLoader grassLandBackground;
    SpriteBatch guiSpriteBatch;
    BitmapFont guiFont;
    ShapeRenderer shapeRenderer;
    AssetManager assetManager;
    Main main;
    Entity player;

    float screenWidth;
    float screenHeight;
    int missed;
    int collected;
    boolean modalActive = false;
    private float stateTime = 0f;
    boolean devconsole = false;

    public BattleScreen(Main main, SpriteBatch spriteBatch, AssetManager assetManager, PlayerEntityFactory playerFactory) {
        this.main = main;
        this.assetManager = assetManager;
        this.spriteBatch = spriteBatch;
        this.ashleyEngine = playerFactory.ashleyEngine;
        grassLandBackground = new GrasslandTestBackGroundLoader(main.assetManager);
        player = playerFactory.createPlayer();

        touchPos = new Vector2();

        guiSpriteBatch = new SpriteBatch();
        guiFont = new BitmapFont();
        guiFont.setColor(Color.GREEN);

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

        guiSpriteBatch.begin();
        guiFont.draw(guiSpriteBatch, "collected: "+collected,screenWidth-screenWidth/6, screenHeight-screenHeight/30);
        guiFont.draw(guiSpriteBatch, "missed: "+missed,screenWidth-screenWidth/6, screenHeight-screenHeight/13);
        guiFont.draw(guiSpriteBatch, "fps: "+ Gdx.graphics.getFramesPerSecond(),screenWidth-screenWidth/6, screenHeight-screenHeight/7);
        guiSpriteBatch.end();

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




        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        PlayerEntityFactory.VelocityComponent velocity = player.getComponent(PlayerEntityFactory.VelocityComponent.class);
        velocity.dx = 0f;
        velocity.dy = 0f;

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            modalActive = !modalActive;
            try{
                Thread.sleep(100);
            } catch (Exception e){
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            velocity.dy=+10;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            velocity.dy=-10;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            velocity.dx=-10;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            velocity.dx=+10;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.F2)){
            devconsole = !devconsole;
            try{
                Thread.sleep(100);
            } catch (Exception e){
                e.printStackTrace();
            }

        }

        //mouseTouch
        if(Gdx.input.isTouched()){
            //Gdx.input.getX();
            //Gdx.input.getY();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            main.viewport.unproject(touchPos);
            //playerTestSprite.setCenterX(touchPos.x);
            //playerTestSprite.setCenterY(touchPos.y);
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        stateTime += Gdx.graphics.getDeltaTime();

        spriteBatch.begin();
        grassLandBackground.drawBackGround(spriteBatch);
        ashleyEngine.update(Gdx.graphics.getDeltaTime());

        if (modalActive){
            ModalScreen screen = ScreenManager.manager.getModalScreen(0);
            screen.draw();
        }
        spriteBatch.end();
    }


}
