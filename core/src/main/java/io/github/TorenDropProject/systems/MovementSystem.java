package io.github.TorenDropProject.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import io.github.TorenDropProject.entities.PlayerEntityCreator;

public class MovementSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private ComponentMapper<PlayerEntityCreator.PositionComponent> pm = ComponentMapper.getFor(PlayerEntityCreator.PositionComponent.class);
    private ComponentMapper<PlayerEntityCreator.VelocityComponent> vm = ComponentMapper.getFor(PlayerEntityCreator.VelocityComponent.class);

    public MovementSystem() {}

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PlayerEntityCreator.PositionComponent.class, PlayerEntityCreator.VelocityComponent.class).get());
    }

    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PlayerEntityCreator.PositionComponent position = pm.get(entity);
            PlayerEntityCreator.VelocityComponent velocity = vm.get(entity);

            position.x += velocity.x * deltaTime;
            position.y += velocity.y * deltaTime;
        }
    }
}
