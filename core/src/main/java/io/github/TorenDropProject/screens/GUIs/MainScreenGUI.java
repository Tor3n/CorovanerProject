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

    public MainScreenGUI(MainMenuScreen mainMenuScreen, ScreenManager screenManager, SpriteBatch spriteBatch) {
        this.gameScreen = mainMenuScreen;
        this.screenManager = screenManager;
        this.spriteBatch = spriteBatch;
        guiMainViewPort = new ScreenViewport();
        stage = new Stage(guiMainViewPort);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));


        TextButton mainContinueButton = new TextButton("Continue", skin);
        mainContinueButton.setPosition(0f, 110f);
        mainContinueButton.setSize(90,25);

        mainContinueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenManager.setScreen(screenManager.getGameScreen("BattleScreen"));
            }
        });

        TextButton settings = new TextButton("charsheet", skin);
        settings.setPosition(0, 130);
        settings.setSize(90,25);

        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("test clicked!");
            }
        });

        stage.addActor(mainContinueButton);
        stage.addActor(settings);

    }

    public void draw(float delta) {
        Gdx.input.setInputProcessor(stage);
        stage.act(delta);
        stage.draw();
    }
}
