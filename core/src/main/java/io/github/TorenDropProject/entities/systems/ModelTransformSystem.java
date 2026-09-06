package io.github.TorenDropProject.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import io.github.TorenDropProject.entities.components.ModelComponent;
import io.github.TorenDropProject.entities.components.WorldTransformComponent;

/** Priority 3 synchronizes scene state. GPU drawing happens once, after the Engine update. */
public final class ModelTransformSystem extends IteratingSystem {
    public ModelTransformSystem() { super(Family.all(WorldTransformComponent.class, ModelComponent.class).get(), 3); }
    @Override protected void processEntity(Entity entity, float delta) {
        WorldTransformComponent p = entity.getComponent(WorldTransformComponent.class);
        ModelComponent m = entity.getComponent(ModelComponent.class);
        m.instance.transform.setToTranslation(p.x, p.y, p.z).rotate(Vector3.Y, p.yawDegrees).scale(m.scale, m.scale, m.scale);
        m.worldBounds.set(m.localBounds).mul(m.instance.transform);
    }
}
