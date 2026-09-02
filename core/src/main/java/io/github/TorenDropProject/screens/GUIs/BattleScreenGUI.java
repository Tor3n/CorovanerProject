package io.github.TorenDropProject.screens.GUIs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.TorenDropProject.screens.ScreenManager;

public class BattleScreenGUI {
    private Stage stage;
    private ScreenManager screenManager;
    ScreenViewport guiViewPort;
    private Skin skin;

    public BattleScreenGUI(ScreenManager screenManager) {
        this.screenManager = screenManager;

        //pixel for pixel - ideal for GUI
        guiViewPort = new ScreenViewport();
        stage = new Stage(guiViewPort);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

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
        guiViewPort.update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
