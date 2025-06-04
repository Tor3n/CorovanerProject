package io.github.TorenDropProject.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class MovementSystem extends IteratingSystem {

    public MovementSystem() {
        super(Family.all(PlayerEntityFactory.PositionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerEntityFactory.PositionComponent position = entity.getComponent(PlayerEntityFactory.PositionComponent.class);
        PlayerEntityFactory.VelocityComponent velocity = entity.getComponent(PlayerEntityFactory.VelocityComponent.class);

        position.x += velocity.dx*deltaTime;
        position.y += velocity.dy*deltaTime;
    }
}
