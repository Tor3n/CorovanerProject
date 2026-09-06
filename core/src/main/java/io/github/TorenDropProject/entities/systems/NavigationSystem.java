package io.github.TorenDropProject.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import io.github.TorenDropProject.entities.components.*;

/** Converts a route to movement intent before collision/movement runs. */
public final class NavigationSystem extends IteratingSystem {
    public NavigationSystem() {
        super(Family.all(NavigationComponent.class, WorldTransformComponent.class, MovementIntentComponent.class).get(), 1);
    }
    @Override protected void processEntity(Entity entity, float delta) {
        NavigationComponent route = entity.getComponent(NavigationComponent.class);
        if (!route.active()) return;
        WorldTransformComponent p = entity.getComponent(WorldTransformComponent.class);
        MovementIntentComponent intent = entity.getComponent(MovementIntentComponent.class);
        while (route.active()) {
            Vector3 point = route.waypoints.get(route.next);
            float dx = point.x - p.x, dz = point.z - p.z;
            float distance = (float)Math.hypot(dx, dz);
            if (distance < 0.015f) { route.next++; continue; }
            intent.x = dx; intent.z = dz;
            intent.maxDistance = distance;
            return;
        }
        route.clear(); intent.x = intent.z = 0; intent.maxDistance = Float.POSITIVE_INFINITY;
    }
}
