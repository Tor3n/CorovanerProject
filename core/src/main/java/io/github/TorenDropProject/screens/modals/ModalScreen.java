package io.github.TorenDropProject.screens.modals;

import com.badlogic.gdx.utils.Disposable;

public interface ModalScreen extends Disposable {

    void draw();

    void resize(int width, int height);
}
