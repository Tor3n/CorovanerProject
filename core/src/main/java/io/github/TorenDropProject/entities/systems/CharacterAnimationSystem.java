package io.github.TorenDropProject.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import io.github.TorenDropProject.entities.components.AnimationComponent;
import io.github.TorenDropProject.entities.components.VelocityComponent;

/** Animate after collision and world transforms, before sockets are queried and the world is drawn. */
public final class CharacterAnimationSystem extends IteratingSystem {
    public CharacterAnimationSystem() {
        super(Family.all(AnimationComponent.class, VelocityComponent.class).get(), 4);
    }
    @Override protected void processEntity(Entity entity, float delta) {
        AnimationComponent animation = entity.getComponent(AnimationComponent.class);
        if (animation.paused) return;
        VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
        float speed = (float)Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        String clip = animation.previewClip;
        if (clip == null) {
            // Hysteresis avoids alternating walk/run while sliding along uneven collision boundaries.
            float runThreshold = animation.clip.equals("run") ? 2.4f : 2.8f;
            clip = speed < 0.03f ? "idle" : speed >= runThreshold ? "run" : "walk";
        }
        float rate = 1f;
        if (animation.previewClip == null && !clip.equals("idle")) {
            rate = MathUtils.clamp(speed / (clip.equals("run") ? AnimationComponent.RUN_SPEED : AnimationComponent.WALK_SPEED), 0f, 2f);
        }
        if (!clip.equals(animation.clip)) {
            animation.clip = clip;
            animation.controller.animate(clip, -1, rate, null, 0.15f);
        } else if (animation.controller.current != null) {
            animation.controller.current.speed = rate;
        }
        animation.controller.update(delta);
    }
}
