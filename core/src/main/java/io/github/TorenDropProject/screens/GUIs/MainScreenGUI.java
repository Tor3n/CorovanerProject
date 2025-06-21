package io.github.TorenDropProject.screens.GUIs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.TorenDropProject.screens.BattleScreen;
import io.github.TorenDropProject.screens.GameScreen;
import io.github.TorenDropProject.screens.MainMenuScreen;
import io.github.TorenDropProject.screens.ScreenManager;

public class MainScreenGUI {
    MainMenuScreen gameScreen;
    private Stage stage;
    private ScreenManager screenManager;
    ScreenViewport guiMainViewPort;
    private SpriteBatch spriteBatch;
    private final int border = 5 ;
    TextArea infoTextArea;
    TextButton mainContinueButton;
    TextButton settingsButton;
    AssetManager assetManager;
    private Texture background;

    public MainScreenGUI(MainMenuScreen mainMenuScreen, ScreenManager screenManager, SpriteBatch spriteBatch, AssetManager assetManager) {
        this.gameScreen = mainMenuScreen;
        this.screenManager = screenManager;
        this.spriteBatch = spriteBatch;
        this.assetManager = assetManager;
        guiMainViewPort = new ScreenViewport();
        stage = new Stage(guiMainViewPort);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        createTesGroundButton(skin);

        createSettingsButton(skin);

        createInfoArea(skin);

        stage.addActor(mainContinueButton);
        stage.addActor(settingsButton);
        stage.addActor(infoTextArea);

    }

    private void createSettingsButton(Skin skin) {
        settingsButton = new TextButton("Settings", skin);
        settingsButton.setPosition(5f, 140);
        settingsButton.setSize(90,25);

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("settingsButton clicked!");
            }
        });
    }

    private void createTesGroundButton(Skin skin) {
        mainContinueButton = new TextButton("TestGround", skin);
        mainContinueButton.setPosition(5f, guiMainViewPort.getScreenHeight()/10f);
        mainContinueButton.setSize(90,25);

        mainContinueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("createTesGroundButton clicked x: "+x+", y: "+y);
                screenManager.setScreen(screenManager.getGameScreen("BattleScreen"));
            }
        });
    }

    private void createInfoArea(Skin skin) {

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();

        // Set the font
        textFieldStyle.font = skin.getFont("default-font");
        textFieldStyle.fontColor = Color.WHITE; // Set text color

        // Set a transparent background
        textFieldStyle.background = null; // No background
        textFieldStyle.focusedBackground = null; // No focused background
        textFieldStyle.disabledBackground = null; // No disabled background

        infoTextArea = new TextArea("**** \nWe are in development! Yay!\n \n Jira:  \n \n https://tor3n.atlassian.net/jira/software/projects/SMS/boards/1", textFieldStyle);
        infoTextArea.setPosition((stage.getWidth())-(300)- border,+border);
        infoTextArea.setSize(300, 400);
        infoTextArea.setDisabled(true);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(1, 1, 1, 0.2f)); // Semi-transparent white
        pixmap.fill();
        textFieldStyle.background = new TextureRegionDrawable(new Texture(pixmap));
        pixmap.dispose();

    }

    public void draw(float delta) {
        input();

        Gdx.input.setInputProcessor(stage);
        stage.act(delta);
        stage.draw();
    }

    private void input() {
        if(Gdx.input.isTouched()){
            System.out.println(">> x: "+Gdx.input.getX()+", y: "+Gdx.input.getY());

            Vector2 touchPos = new Vector2();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            Vector2 unprojectedTouch = guiMainViewPort.unproject(touchPos);

            System.out.println("unprojected: "+unprojectedTouch);
        }
    }

    public void resize(int width, int height) {
        guiMainViewPort.update(width, height, true);
    }
}
