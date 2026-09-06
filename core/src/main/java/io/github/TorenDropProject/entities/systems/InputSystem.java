package io.github.TorenDropProject.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.TorenDropProject.entities.components.MovementIntentComponent;
import io.github.TorenDropProject.entities.components.SelectedComponent;
import io.github.TorenDropProject.entities.components.NavigationComponent;

public final class InputSystem extends IteratingSystem {
    public InputSystem() {
        super(Family.all(MovementIntentComponent.class, SelectedComponent.class).get(), 0);
    }
    @Override protected void processEntity(Entity entity, float deltaTime) {
        float right = (Gdx.input.isKeyPressed(Input.Keys.D) ? 1 : 0) - (Gdx.input.isKeyPressed(Input.Keys.A) ? 1 : 0);
        float up = (Gdx.input.isKeyPressed(Input.Keys.W) ? 1 : 0) - (Gdx.input.isKeyPressed(Input.Keys.S) ? 1 : 0);
        NavigationComponent route = entity.getComponent(NavigationComponent.class);
        if (right == 0 && up == 0 && route != null && route.active()) return;
        if (route != null) route.clear();
        MovementIntentComponent intent = entity.getComponent(MovementIntentComponent.class);
        intent.maxDistance = Float.POSITIVE_INFINITY;
        setIntent(intent, right, up);
    }
    /** Fixed camera's ground-plane basis; MovementSystem normalizes the resulting intent. */
    public static void setIntent(MovementIntentComponent intent, float right, float up) {
        intent.x = (right - up) * 0.70710678f;
        intent.z = (-right - up) * 0.70710678f;
    }
}
