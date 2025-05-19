package io.github.TorenDropProject.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.TorenDropProject.Main;
import io.github.TorenDropProject.assetsLoaders.GrasslandTestBackGroundLoader;
import io.github.TorenDropProject.screens.modals.ModalScreen;

public class BattleScreen implements GameScreen{
    private SpriteBatch spriteBatch;
    Texture bucketTexture;
    FitViewport viewport;
    Sprite bucketSprite;
    Vector2 touchPos;
    Rectangle bucketRectangle;
    Rectangle dropRectangle;
    GrasslandTestBackGroundLoader grassLandBackground;
    SpriteBatch guiSpriteBatch;
    BitmapFont guiFont;
    ShapeRenderer shapeRenderer;
    DrawModal modalDraw;

    float screenWidth;
    float screenHeight;
    int missed;
    int collected;
    boolean modalActive = false;

    public BattleScreen(Main main, SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        grassLandBackground = new GrasslandTestBackGroundLoader(main.assetManager);
        bucketTexture = new Texture("bucket.png");


        OrthographicCamera camera = new OrthographicCamera();
        viewport = new FitViewport(Main.worldWidth, Main.worldHeight, camera);

        bucketSprite = new Sprite(bucketTexture);
        bucketSprite.setSize(1,1);

        touchPos = new Vector2();

        bucketRectangle = new Rectangle();

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
        simpleGuiCreate();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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

        screenWidth = viewport.getScreenWidth();
        screenHeight = viewport.getScreenHeight();

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

        float worldWidthIns = viewport.getWorldWidth();
        float worldHeightIns = viewport.getWorldHeight();

        float bucketWidth = bucketSprite.getWidth();
        float bucketHeight = bucketSprite.getHeight();

        bucketSprite.setX(MathUtils.clamp(bucketSprite.getX(), 0, worldWidthIns - bucketWidth));
        bucketRectangle.set(bucketSprite.getX(), bucketSprite.getY(), bucketWidth, bucketHeight);

        float delta = Gdx.graphics.getDeltaTime();

    }

    /**
     * Keyboard control
     */
    private void input() {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            modalActive = !modalActive;
            try{
                Thread.sleep(100);
            } catch (Exception e){

            }

            /*modalDraw = () -> {
                modalActive = true;
                ModalScreen screen = ScreenManager.manager.getModalScreen(0);
                screen.draw();
            };
            modalDraw.drawModal();*/
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            bucketSprite.translateX(speed*delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            bucketSprite.translateX(- speed*delta);
        }

        //mouseTouch
        if(Gdx.input.isTouched()){
            //Gdx.input.getX();
            //Gdx.input.getY();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            bucketSprite.setCenterX(touchPos.x);
            bucketSprite.setCenterY(touchPos.y);
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();
        grassLandBackground.drawBackGround(spriteBatch);
        bucketSprite.draw(spriteBatch);
        if (modalActive){
            //modalDraw.drawModal();
            ModalScreen screen = ScreenManager.manager.getModalScreen(0);
            screen.draw();
        }
        spriteBatch.end();
    }

    @FunctionalInterface
    public interface DrawModal{
        void drawModal();
    }

}
