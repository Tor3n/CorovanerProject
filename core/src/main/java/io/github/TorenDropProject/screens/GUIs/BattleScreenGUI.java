package io.github.TorenDropProject.screens.GUIs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import io.github.TorenDropProject.screens.BattleScreen;
import io.github.TorenDropProject.screens.GameScreen;
import io.github.TorenDropProject.screens.MainMenuScreen;
import io.github.TorenDropProject.screens.ScreenManager;

public class BattleScreenGUI {
    GameScreen gameScreen;
    private Stage stage;
    private ScreenManager screenManager;
    ScreenViewport guiViewPort;
    private SpriteBatch spriteBatch;

    public BattleScreenGUI(BattleScreen battleScreen, ScreenManager screenManager, SpriteBatch spriteBatch) {
        this.gameScreen = battleScreen;
        this.screenManager = screenManager;
        this.spriteBatch = spriteBatch;
        guiViewPort = new ScreenViewport();
        stage = new Stage(guiViewPort);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        TextButton mainMenuButton = new TextButton("main menu", skin);
        mainMenuButton.setPosition(0f, 0f);
        mainMenuButton.setSize(90,25);

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                System.out.println("main menu clicked");
                screenManager.setScreen(screenManager.getGameScreen("MainMenu"));
            }
        });

        TextButton inventory = new TextButton("inventory", skin);
        inventory.setPosition(100, 0);
        inventory.setSize(90,25);

        inventory.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("inventory clicked!");
            }
        });

        TextButton character = new TextButton("charsheet", skin);
        character.setPosition(0, 30);
        character.setSize(90,25);

        character.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("character clicked!");
            }
        });

        TextArea logTextArea = new TextArea("", skin);
        logTextArea.setPosition((stage.getWidth()/2f)-(300/2f),0);
        logTextArea.setSize(300, 100);
        logTextArea.setDisabled(true);

        stage.addActor(logTextArea);
        stage.addActor(mainMenuButton);
        stage.addActor(inventory);
        stage.addActor(character);
    }

    public void draw(float delta) {
        Gdx.input.setInputProcessor(stage);
        stage.act(delta);
        stage.draw();
    }
}
