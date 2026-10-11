package io.github.TorenDropProject.entities.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

/** Playback belongs to the actor; clips and meshes remain shared assets. */
public final class AnimationComponent implements Component {
    public static final float WALK_SPEED = 1.76771955f;
    public static final float RUN_SPEED = 3.53543910f;
    public final AnimationController controller;
    public String clip = "idle";
    /** Null during gameplay; the development preview can audition an in-place clip. */
    public String previewClip;
    public boolean paused;

    public AnimationComponent(ModelInstance instance) {
        controller = new AnimationController(instance);
        controller.setAnimation(clip, -1);
        controller.update(0);
    }
}
