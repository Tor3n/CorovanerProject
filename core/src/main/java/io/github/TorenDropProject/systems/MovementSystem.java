package io.github.TorenDropProject.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import io.github.TorenDropProject.entities.PlayerEntityFactory;

public class MovementSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private ComponentMapper<PlayerEntityFactory.PositionComponent> pm = ComponentMapper.getFor(PlayerEntityFactory.PositionComponent.class);
    private ComponentMapper<PlayerEntityFactory.VelocityComponent> vm = ComponentMapper.getFor(PlayerEntityFactory.VelocityComponent.class);

    public MovementSystem() {}

    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PlayerEntityFactory.PositionComponent.class, PlayerEntityFactory.VelocityComponent.class).get());
    }

    public void update(float deltaTime) {
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PlayerEntityFactory.PositionComponent position = pm.get(entity);
            PlayerEntityFactory.VelocityComponent velocity = vm.get(entity);

            position.x += velocity.dx * deltaTime;
            position.y += velocity.dy * deltaTime;
        }
    }
}
