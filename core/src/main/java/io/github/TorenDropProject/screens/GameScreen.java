package io.github.TorenDropProject.screens;

public interface GameScreen {
    void show(); // Called when the screen becomes active
    void render(float delta); // Main rendering method
    void resize(int width, int height); // Called when the window is resized
    void pause(); // Called when the game is paused
    void resume(); // Called when the game resumes
    void hide(); // Called when the screen is no longer active
    void dispose(); // Called to clean up resources
}
