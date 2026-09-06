package io.github.TorenDropProject.screens.development;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.TorenDropProject.screens.GameScreen;
import io.github.TorenDropProject.world.WorldScene;

/** Development controls around the exact same WorldScene used by BattleScreen. */
public final class CharacterPreviewScreen implements GameScreen {
    private final WorldScene scene;
    private final ScreenViewport uiViewport = new ScreenViewport();
    private final SpriteBatch uiBatch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private boolean turntable;
    public CharacterPreviewScreen(WorldScene scene) {
        this.scene = scene;
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    @Override public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) turntable = !turntable;
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) scene.session.followNext();
        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS)) scene.cameraRig.zoom(-delta * 0.5f);
        if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) scene.cameraRig.zoom(delta * 0.5f);
        if (turntable) scene.session.rotatePreview(delta * 35f);
        scene.render(delta, false);
        uiViewport.apply();
        uiBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        uiBatch.begin();
        font.draw(uiBatch, "Shared world preview | Space: rotate | Tab: follow next | +/-: zoom", 16, uiViewport.getWorldHeight() - 18);
        uiBatch.end();
    }
    @Override public void resize(int width, int height) { scene.resize(width, height); uiViewport.update(width, Math.max(1, height), true); }
    @Override public void show() { }
    @Override public void hide() { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void dispose() { scene.dispose(); uiBatch.dispose(); font.dispose(); }
}
