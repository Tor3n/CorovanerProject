package io.github.TorenDropProject.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.TorenDropProject.Main;
import io.github.TorenDropProject.assetsLoaders.GrasslandTestBackGroundLoader;
import io.github.TorenDropProject.screens.modals.ModalScreen;

public class BattleScreen implements GameScreen{
    private SpriteBatch spriteBatch;
    Texture bucketTexture;

    Sprite playerTestSprite;


    Vector2 touchPos;
    Rectangle bucketRectangle;
    Rectangle dropRectangle;
    GrasslandTestBackGroundLoader grassLandBackground;
    SpriteBatch guiSpriteBatch;
    BitmapFont guiFont;
    ShapeRenderer shapeRenderer;
    DrawModal modalDraw;
    AssetManager assetManager;
    Main main;
    Animation<TextureRegion> walkAnimation;

    float screenWidth;
    float screenHeight;
    int missed;
    int collected;
    boolean modalActive = false;
    private float stateTime = 0f;

    public BattleScreen(Main main, SpriteBatch spriteBatch, AssetManager assetManager, Array<TextureRegion> testWalk) {
        this.main = main;
        this.assetManager = assetManager;
        this.spriteBatch = spriteBatch;
        grassLandBackground = new GrasslandTestBackGroundLoader(main.assetManager);

        playerTestSprite = new Sprite(testWalk.get(0));
        playerTestSprite.setSize(0.34f*3,0.7f*3);
        walkAnimation = new Animation<>(0.1f, testWalk);

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

        float bucketWidth = playerTestSprite.getWidth();
        float bucketHeight = playerTestSprite.getHeight();

        playerTestSprite.setX(MathUtils.clamp(playerTestSprite.getX(), 0, worldWidthIns - bucketWidth));
        bucketRectangle.set(playerTestSprite.getX(), playerTestSprite.getY(), bucketWidth, bucketHeight);

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
            playerTestSprite.translateX(speed*delta);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            playerTestSprite.translateX(- speed*delta);
        }

        //mouseTouch
        if(Gdx.input.isTouched()){
            //Gdx.input.getX();
            //Gdx.input.getY();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            main.viewport.unproject(touchPos);
            playerTestSprite.setCenterX(touchPos.x);
            playerTestSprite.setCenterY(touchPos.y);
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        spriteBatch.begin();
        grassLandBackground.drawBackGround(spriteBatch);
        playerTestSprite.draw(spriteBatch);
        spriteBatch.draw(currentFrame,0,0, 0.34f*3,0.7f*3);

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
