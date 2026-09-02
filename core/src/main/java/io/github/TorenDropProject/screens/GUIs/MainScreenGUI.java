package io.github.TorenDropProject.screens.GUIs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.TorenDropProject.screens.ScreenManager;

public class MainScreenGUI {
    private Stage stage;
    private ScreenManager screenManager;
    ScreenViewport guiMainViewPort;
    private final int border = 5 ;
    TextArea infoTextArea;
    TextButton mainContinueButton;
    TextButton settingsButton;
    private Skin skin;
    private Texture infoBackgroundTexture;


    public MainScreenGUI(ScreenManager screenManager) {
        this.screenManager = screenManager;

        //pixel for pixel - ideal for GUI
        guiMainViewPort = new ScreenViewport();
        stage = new Stage(guiMainViewPort);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

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
        infoBackgroundTexture = new Texture(pixmap);
        textFieldStyle.background = new TextureRegionDrawable(infoBackgroundTexture);
        pixmap.dispose();

    }

    public void draw(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    public void hide() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    public void resize(int width, int height) {
        guiMainViewPort.update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
        infoBackgroundTexture.dispose();
    }
}
