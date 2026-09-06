package io.github.TorenDropProject.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import io.github.TorenDropProject.entities.components.ModelComponent;
import io.github.TorenDropProject.entities.components.NavigationComponent;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Screen coordinates enter here; selection and destinations use the same camera as world rendering. */
public final class WorldInputController extends InputAdapter {
    private final WorldScene scene;
    private final BooleanSupplier enabled;
    private final BiPredicate<Integer, Integer> overHud;
    private final Consumer<String> feedback;
    public WorldInputController(WorldScene scene, BooleanSupplier enabled,
                                BiPredicate<Integer, Integer> overHud, Consumer<String> feedback) {
        this.scene = scene; this.enabled = enabled; this.overHud = overHud; this.feedback = feedback;
    }
    @Override public boolean touchDown(int x, int y, int pointer, int button) {
        if (pointer != 0 || button != Input.Buttons.LEFT || !enabled.getAsBoolean() || overHud.test(x, y)) return false;
        Ray ray = scene.cameraRig.pickRay(x, y);
        Vector3 hit = new Vector3();
        Entity closest = null;
        float distance = Float.POSITIVE_INFINITY;
        for (Entity actor : scene.session.engine.getEntitiesFor(Family.all(ModelComponent.class, NavigationComponent.class).get())) {
            if (Intersector.intersectRayBounds(ray, actor.getComponent(ModelComponent.class).worldBounds, hit)) {
                float next = ray.origin.dst2(hit);
                if (next < distance) { distance = next; closest = actor; }
            }
        }
        if (closest != null) {
            scene.session.select(closest);
            feedback.accept("Scout selected. Click the ground to move.");
        } else if (scene.cameraRig.pickGround(x, y, scene.session.map.grid, hit) && scene.session.moveSelected(hit)) {
            feedback.accept("Destination set.");
        } else {
            feedback.accept("No clear route to that spot.");
        }
        return true;
    }
}
