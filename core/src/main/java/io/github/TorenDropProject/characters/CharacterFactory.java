package io.github.TorenDropProject.characters;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import io.github.TorenDropProject.entities.components.*;

/** Creates independent actors referencing one AssetManager-owned Model. */
public final class CharacterFactory {
    private final CharacterDefinition definition;
    private final Model model;
    public CharacterFactory(CharacterDefinition definition, Model model) { this.definition = definition; this.model = model; }
    public Entity create(float x, float y, float z, boolean playerControlled) {
        Entity entity = new Entity();
        entity.add(new WorldTransformComponent(x, y, z));
        entity.add(new MovementIntentComponent());
        entity.add(new NavigationComponent());
        entity.add(new VelocityComponent());
        entity.add(new CollisionComponent(definition.collisionRadius));
        entity.add(new ModelComponent(new ModelInstance(model), definition.scale));
        if (playerControlled) entity.add(new PlayerControlledComponent()).add(new SelectedComponent());
        return entity;
    }
}
