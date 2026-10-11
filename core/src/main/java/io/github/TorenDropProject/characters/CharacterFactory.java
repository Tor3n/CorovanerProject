package io.github.TorenDropProject.characters;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import io.github.TorenDropProject.entities.components.*;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.utils.MaterialConverter;

/** Creates independent actors referencing one AssetManager-owned Model. */
public final class CharacterFactory {
    private final CharacterDefinition definition;
    private final Model model;
    public CharacterFactory(CharacterDefinition definition, Model model) { this.definition = definition; this.model = model; }
    public CharacterFactory(CharacterDefinition definition, SceneAsset asset) {
        this(definition, asset.scene.model);
        if (asset.maxBones > 32) throw new IllegalArgumentException("Character exceeds the world's 32-bone shader budget");
        MaterialConverter.makeCompatible(model.materials);
        // Metallic/roughness cannot be reproduced by the world's simple Phong shader.
        // Keep this character matte instead of turning cloth and skin into shiny plastic.
        for (Material material : model.materials) {
            material.remove(ColorAttribute.Specular | TextureAttribute.Specular | FloatAttribute.Shininess);
        }
        for (String clip : new String[]{"idle", "walk", "run"}) {
            if (model.getAnimation(clip) == null) throw new IllegalArgumentException("Missing character animation: " + clip);
        }
    }
    public Entity create(float x, float y, float z, boolean playerControlled) {
        Entity entity = new Entity();
        entity.add(new WorldTransformComponent(x, y, z));
        entity.add(new MovementIntentComponent());
        entity.add(new NavigationComponent());
        entity.add(new VelocityComponent());
        entity.add(new CollisionComponent(definition.collisionRadius));
        ModelComponent appearance = new ModelComponent(new ModelInstance(model), definition.scale);
        entity.add(appearance);
        if (model.getAnimation("idle") != null) {
            entity.add(new AnimationComponent(appearance.instance));
            entity.getComponent(MovementIntentComponent.class).speed = AnimationComponent.WALK_SPEED;
            // Measured across every exported protagonist clip, including the attached rifle, with padding.
            if (definition == CharacterDefinition.PROTAGONIST) {
                appearance.localBounds.set(new Vector3(-0.77f, -0.05f, -0.67f), new Vector3(0.77f, 2.36f, 1.21f));
            }
        }
        if (playerControlled) entity.add(new PlayerControlledComponent()).add(new SelectedComponent());
        return entity;
    }
}
