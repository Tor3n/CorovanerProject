package io.github.TorenDropProject.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import io.github.TorenDropProject.entities.components.*;
import io.github.TorenDropProject.world.WorldGrid;

public final class MovementSystem extends IteratingSystem {
    private final WorldGrid grid;
    public MovementSystem(WorldGrid grid) {
        super(Family.all(WorldTransformComponent.class, MovementIntentComponent.class,
            VelocityComponent.class, CollisionComponent.class).get(), 2);
        this.grid = grid;
    }
    @Override protected void processEntity(Entity entity, float delta) {
        WorldTransformComponent p = entity.getComponent(WorldTransformComponent.class);
        MovementIntentComponent intent = entity.getComponent(MovementIntentComponent.class);
        VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
        float radius = entity.getComponent(CollisionComponent.class).radius;
        float length = (float)Math.sqrt(intent.x * intent.x + intent.z * intent.z);
        float speed = delta > 0 ? Math.min(intent.speed, intent.maxDistance / delta) : 0;
        velocity.x = length > 0 ? intent.x / length * speed : 0;
        velocity.z = length > 0 ? intent.z / length * speed : 0;
        float beforeX = p.x, beforeZ = p.z;
        // Substeps prevent crossing a blocked cell during a slow frame; axis separation slides along walls.
        int steps = Math.max(1, (int)Math.ceil(intent.speed * delta / (grid.cellSize * 0.2f)));
        for (int i = 0; i < steps; i++) {
            float x = p.x + velocity.x * delta / steps;
            if (grid.canStand(x, p.z, radius, p.y)) p.x = x;
            float z = p.z + velocity.z * delta / steps;
            if (grid.canStand(p.x, z, radius, p.y)) p.z = z;
            p.y = grid.elevation(grid.column(p.x), grid.row(p.z));
        }
        if (delta > 0) {
            velocity.x = (p.x - beforeX) / delta;
            velocity.z = (p.z - beforeZ) / delta;
        }
        if (velocity.x * velocity.x + velocity.z * velocity.z > 0.0001f) {
            p.yawDegrees = (float)Math.toDegrees(Math.atan2(velocity.x, velocity.z));
        }
    }
}
