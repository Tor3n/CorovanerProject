package io.github.TorenDropProject.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import io.github.TorenDropProject.entities.components.*;
import io.github.TorenDropProject.entities.systems.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class NavigationTest {
    private WorldGrid grid() {
        WorldGrid grid = new WorldGrid(12, 12, 1);
        for (int r = 0; r < 12; r++) for (int c = 0; c < 12; c++) grid.setGround(c, r, 0);
        return grid;
    }
    @Test public void routeWalksAroundWallAndArrivesWithoutOvershooting() {
        WorldGrid grid = grid();
        for (int r = 0; r < 9; r++) grid.block(5, r);
        Array<Vector3> points = new GridPathfinder(grid).find(2.3f, 0, -2.2f, 8.75f, -2.3f, .18f);
        assertTrue(points.notEmpty());
        assertTrue(points.toString().contains("-9.5"));
        NavigationComponent route = new NavigationComponent(); route.waypoints.addAll(points);
        WorldTransformComponent position = new WorldTransformComponent(2.3f, 0, -2.2f);
        Entity actor = new Entity().add(position).add(route).add(new MovementIntentComponent())
            .add(new VelocityComponent()).add(new CollisionComponent(.18f));
        Engine engine = new Engine(); engine.addSystem(new NavigationSystem()); engine.addSystem(new MovementSystem(grid)); engine.addEntity(actor);
        for (int i = 0; i < 1500; i++) {
            engine.update(i % 3 == 0 ? .1f : .016f);
            assertTrue(grid.canStand(position.x, position.z, .18f, position.y));
        }
        assertFalse(route.active());
        assertEquals(8.75f, position.x, .015f); assertEquals(-2.3f, position.z, .015f);
        assertEquals(0, actor.getComponent(VelocityComponent.class).x, 0);
    }
    @Test public void rejectsUnreachableBlockedAndRaisedDestinations() {
        WorldGrid grid = grid();
        GridPathfinder paths = new GridPathfinder(grid);
        for (int r = 0; r < 12; r++) grid.block(5, r);
        assertTrue(paths.find(2.5f, 0, -2.5f, 8.5f, -2.5f, .18f).isEmpty());
        assertTrue(paths.find(2.5f, 0, -2.5f, 5.5f, -2.5f, .18f).isEmpty());
        assertTrue(paths.find(2.5f, 0, -2.5f, -1, -2.5f, .18f).isEmpty());
        grid.setGround(3, 3, 2);
        assertTrue(paths.find(2.5f, 0, -2.5f, 3.5f, -3.5f, .18f).isEmpty());
    }
    @Test public void cannotSqueezeDiagonallyBetweenBlockedCorners() {
        WorldGrid grid = grid();
        grid.block(0, 1); grid.block(1, 0);
        assertTrue(new GridPathfinder(grid).find(.5f, 0, -.5f, 1.5f, -1.5f, .18f).isEmpty());
    }
    @Test public void nearbyClicksKeepTheExactDestination() {
        WorldGrid grid = grid();
        Array<Vector3> path = new GridPathfinder(grid).find(2.5f, 0, -2.5f, 2.65f, -2.6f, .18f);
        assertEquals(1, path.size);
        assertEquals(new Vector3(2.65f, 0, -2.6f), path.peek());
    }
    @Test public void keyboardOverridesOnlySelectedActorsRoute() {
        com.badlogic.gdx.Input previous = com.badlogic.gdx.Gdx.input;
        final boolean[] pressed = {false};
        com.badlogic.gdx.Gdx.input = (com.badlogic.gdx.Input)java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(),
            new Class<?>[] {com.badlogic.gdx.Input.class}, (proxy, method, args) -> {
                if (method.getName().equals("isKeyPressed")) return pressed[0] && (int)args[0] == com.badlogic.gdx.Input.Keys.W;
                return null;
            });
        try {
            Engine engine = new Engine(); engine.addSystem(new InputSystem());
            NavigationComponent selectedRoute = new NavigationComponent(), otherRoute = new NavigationComponent();
            selectedRoute.waypoints.add(new Vector3(2, 0, -2)); otherRoute.waypoints.add(new Vector3(4, 0, -4));
            Entity selected = new Entity().add(new SelectedComponent()).add(new MovementIntentComponent()).add(selectedRoute);
            Entity other = new Entity().add(new MovementIntentComponent()).add(otherRoute);
            engine.addEntity(selected); engine.addEntity(other);
            engine.update(.1f);
            assertTrue(selectedRoute.active()); assertTrue(otherRoute.active());
            pressed[0] = true; engine.update(.1f);
            assertFalse(selectedRoute.active()); assertTrue(otherRoute.active());
            assertTrue(selected.getComponent(MovementIntentComponent.class).x < 0);
        } finally { com.badlogic.gdx.Gdx.input = previous; }
    }

}
