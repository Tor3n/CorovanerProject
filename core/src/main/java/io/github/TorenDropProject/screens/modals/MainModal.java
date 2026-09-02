package io.github.TorenDropProject.screens.modals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainModal implements ModalScreen {
    private final SpriteBatch spriteBatch;
    private final Texture texture;
    private final Viewport viewport;

    public MainModal(AssetManager assetManager, SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        this.texture = assetManager.get("MainModal.png", Texture.class);
        this.viewport = new ScreenViewport();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void draw() {
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        float modalSize = Math.min(viewport.getWorldWidth(), viewport.getWorldHeight()) * 0.6f;
        float x = (viewport.getWorldWidth() - modalSize) / 2f;
        float y = (viewport.getWorldHeight() - modalSize) / 2f;

        spriteBatch.begin();
        spriteBatch.draw(texture, x, y, modalSize, modalSize);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        // Texture, batch, and their resources are owned by Main.
    }
}
