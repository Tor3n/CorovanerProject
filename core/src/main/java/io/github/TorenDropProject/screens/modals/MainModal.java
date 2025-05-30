package io.github.TorenDropProject.screens.modals;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.TorenDropProject.Main;

public class MainModal implements ModalScreen {
    private SpriteBatch spriteBatch;
    private AssetManager assetManager;
    private Texture texture;

    public MainModal(Main main, SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        this.assetManager = main.assetManager;
    }

    public void draw() {
        texture = assetManager.get("MainModal.png", Texture.class);
        spriteBatch.draw(texture,Main.worldWidth/2f-5/2f,Main.worldHeight/2f-5/2f, 5,5);
    }
}
