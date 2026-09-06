package io.github.TorenDropProject.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.github.TorenDropProject.characters.CharacterFactory;
import io.github.TorenDropProject.entities.components.*;
import io.github.TorenDropProject.entities.systems.*;

/** Simulation lifetime; no camera, batch, or shared-asset disposal. */
public final class BattleSession {
    public final Engine engine = new Engine();
    public final WorldMap map;
    private final Array<Entity> characters = new Array<>();
    private int followedIndex;
    public BattleSession(WorldMap map, CharacterFactory factory, boolean playerInput) {
        this.map = map;
        engine.addSystem(new InputSystem());
        engine.addSystem(new NavigationSystem());
        engine.addSystem(new MovementSystem(map.grid));
        engine.addSystem(new ModelTransformSystem());
        for (int i = 0; i < map.spawns.size; i++) {
            Vector3 spawn = map.spawns.get(i);
            Entity character = factory.create(spawn.x, spawn.y, spawn.z, playerInput && i == 0);
            character.getComponent(WorldTransformComponent.class).yawDegrees = i * 75f;
            characters.add(character);
            engine.addEntity(character);
        }
        engine.getSystem(ModelTransformSystem.class).update(0);
    }
    public Entity followed() { return characters.get(followedIndex); }
    public void followNext() { followedIndex = (followedIndex + 1) % characters.size; }
    public Entity selected() {
        for (Entity entity : characters) if (entity.getComponent(SelectedComponent.class) != null) return entity;
        return null;
    }
    public boolean select(Entity actor) {
        if (!characters.contains(actor, true)) return false;
        Entity previous = selected();
        if (previous != null) {
            previous.remove(SelectedComponent.class);
            if (!previous.getComponent(NavigationComponent.class).active()) {
                MovementIntentComponent intent = previous.getComponent(MovementIntentComponent.class);
                intent.x = intent.z = 0;
            }
        }
        actor.add(new SelectedComponent());
        followedIndex = characters.indexOf(actor, true);
        return true;
    }
    public void selectNext() { select(characters.get((followedIndex + 1) % characters.size)); }
    public boolean moveSelected(Vector3 destination) {
        Entity actor = selected();
        if (actor == null) return false;
        WorldTransformComponent p = actor.getComponent(WorldTransformComponent.class);
        NavigationComponent route = actor.getComponent(NavigationComponent.class);
        Array<Vector3> path = new GridPathfinder(map.grid).find(p.x, p.y, p.z, destination.x, destination.z,
            actor.getComponent(CollisionComponent.class).radius);
        if (path.isEmpty()) return false;
        route.clear(); route.waypoints.addAll(path);
        return true;
    }
    public void update(float delta) { engine.update(Math.min(delta, 0.1f)); }
    public void rotatePreview(float degrees) {
        for (Entity entity : characters) entity.getComponent(WorldTransformComponent.class).yawDegrees += degrees;
    }
    public void stopActors() {
        for (Entity entity : characters) {
            entity.getComponent(NavigationComponent.class).clear();
            MovementIntentComponent intent = entity.getComponent(MovementIntentComponent.class);
            VelocityComponent velocity = entity.getComponent(VelocityComponent.class);
            intent.x = intent.z = velocity.x = velocity.z = 0;
            intent.maxDistance = Float.POSITIVE_INFINITY;
        }
    }
    public void clear() { engine.removeAllEntities(); engine.removeAllSystems(); characters.clear(); }
}
