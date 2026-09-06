package io.github.TorenDropProject.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public final class GameSettings {
    public boolean fullscreen;
    public boolean vsync = true;
    public boolean largerText;
    public boolean decorations = true;
    private final Preferences preferences;
    private int windowWidth = 1300, windowHeight = 800;

    public GameSettings(Preferences preferences) {
        this.preferences = preferences;
        fullscreen = preferences.getBoolean("fullscreen", false);
        vsync = preferences.getBoolean("vsync", true);
        largerText = preferences.getBoolean("largerText", false);
        decorations = preferences.getBoolean("decorations", true);
    }
    public void apply(boolean fullscreen, boolean vsync, boolean largerText, boolean decorations) {
        if (fullscreen != Gdx.graphics.isFullscreen()) {
            if (fullscreen) {
                windowWidth = Gdx.graphics.getWidth();
                windowHeight = Gdx.graphics.getHeight();
            }
            boolean success = fullscreen ? Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode())
                : Gdx.graphics.setWindowedMode(windowWidth, windowHeight);
            if (!success) throw new IllegalStateException("The display mode could not be changed.");
        }
        Gdx.graphics.setVSync(vsync);
        this.fullscreen = fullscreen;
        this.vsync = vsync;
        this.largerText = largerText;
        this.decorations = decorations;
        preferences.putBoolean("fullscreen", fullscreen).putBoolean("vsync", vsync)
            .putBoolean("largerText", largerText).putBoolean("decorations", decorations).flush();
    }
}
