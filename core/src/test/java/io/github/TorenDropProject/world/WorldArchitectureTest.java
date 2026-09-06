package io.github.TorenDropProject.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxNativesLoader;
import io.github.TorenDropProject.characters.*;
import io.github.TorenDropProject.entities.components.*;
import io.github.TorenDropProject.entities.systems.*;
import org.junit.BeforeClass;
import org.junit.Test;
import java.lang.reflect.Proxy;
import static org.junit.Assert.*;

public class WorldArchitectureTest {
    @BeforeClass public static void natives() { GdxNativesLoader.load(); }
    private WorldGrid flatGrid() {
        WorldGrid grid = new WorldGrid(12, 12, 1f);
        for (int row = 0; row < 12; row++) for (int col = 0; col < 12; col++) grid.setGround(col, row, 0);
        return grid;
    }
    private Entity actor(float x, float z) {
        return new Entity().add(new WorldTransformComponent(x, 0, z)).add(new MovementIntentComponent())
            .add(new VelocityComponent()).add(new CollisionComponent(0.18f));
    }
    @Test public void cellCentersRoundTripAndOutsideIsBlocked() {
        WorldGrid grid = flatGrid();
        for (int row = 0; row < 12; row++) for (int col = 0; col < 12; col++) {
            assertEquals(col, grid.column(grid.centerX(col)));
            assertEquals(row, grid.row(grid.centerZ(row)));
        }
        assertFalse(grid.canStand(-0.1f, -2f, 0.18f, 0));
        assertFalse(grid.canStand(2f, 0.1f, 0.18f, 0));
        assertFalse(grid.canStand(Float.NaN, -2f, 0.18f, 0));
    }
    @Test public void groundPickingSurvivesZoomAndResize() {
        WorldGrid grid = flatGrid();
        WorldCameraRig rig = new WorldCameraRig();
        rig.follow(5.5f, 0, -5.5f);
        com.badlogic.gdx.Graphics previousGraphics = Gdx.graphics;
        com.badlogic.gdx.graphics.GL20 previousGl = Gdx.gl;
        try {
            Gdx.gl = (com.badlogic.gdx.graphics.GL20)Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{com.badlogic.gdx.graphics.GL20.class}, (proxy, method, args) -> null);
            for (int[] size : new int[][]{{1100,800},{800,1100},{1600,600}}) {
                Gdx.graphics = (com.badlogic.gdx.Graphics)Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class[]{com.badlogic.gdx.Graphics.class}, (proxy, method, args) -> {
                        if (method.getName().equals("getWidth") || method.getName().equals("getBackBufferWidth")) return size[0];
                        if (method.getName().equals("getHeight") || method.getName().equals("getBackBufferHeight")) return size[1];
                        return null;
                    });
                rig.resize(size[0],size[1]);
                for (float zoom : new float[]{0.5f,1f,1.8f}) {
                    rig.camera().zoom = zoom;
                    rig.camera().update();
                    Vector3 world = new Vector3(5.5f, 0, -5.5f);
                    Vector3 screen = rig.camera().project(new Vector3(world),0,0,size[0],size[1]);
                    Vector3 hit = new Vector3();
                    assertTrue(rig.pickGround(screen.x,size[1]-screen.y,grid,hit));
                    assertEquals(world.x,hit.x,0.001f);
                    assertEquals(world.z,hit.z,0.001f);
                }
            }
        } finally { Gdx.graphics = previousGraphics; Gdx.gl = previousGl; }
    }
    @Test public void diagonalSpeedIsNormalizedAndCliffsStopMovement() {
        WorldGrid grid = flatGrid();
        Engine engine = new Engine();
        engine.addSystem(new MovementSystem(grid));
        Entity actor = actor(2.5f, -2.5f);
        engine.addEntity(actor);
        MovementIntentComponent intent = actor.getComponent(MovementIntentComponent.class);
        intent.x = intent.z = 1;
        engine.update(0.1f);
        WorldTransformComponent p = actor.getComponent(WorldTransformComponent.class);
        assertEquals(0.3f, new Vector3(p.x-2.5f,0,p.z+2.5f).len(), 0.0001f);
        grid.block(4, 2);
        p.x = 3.5f; p.z = -2.5f;
        intent.x = 1; intent.z = 0;
        engine.update(1f);
        assertTrue(p.x < 4f - 0.18f);
        assertEquals(0, actor.getComponent(VelocityComponent.class).z, 0);
        grid.setGround(3, 3, 2);
        assertFalse(grid.canStand(3.5f, -3.5f, 0.18f, 0));
    }
    @Test public void keyboardIntentOnlyChangesPlayerAndNpcMovesIndependently() {
        Input previous = Gdx.input;
        Gdx.input = (Input)Proxy.newProxyInstance(Input.class.getClassLoader(), new Class[]{Input.class}, (proxy, method, args) -> {
            if (method.getName().equals("isKeyPressed")) return (int)args[0] == Input.Keys.W;
            if (method.getReturnType() == boolean.class) return false;
            if (method.getReturnType() == int.class) return 0;
            return null;
        });
        try {
            Engine engine = new Engine();
            engine.addSystem(new InputSystem()); engine.addSystem(new MovementSystem(flatGrid()));
            Entity player = actor(3.5f,-3.5f).add(new PlayerControlledComponent()).add(new SelectedComponent());
            Entity npc = actor(6.5f,-6.5f);
            npc.getComponent(MovementIntentComponent.class).x = 1;
            engine.addEntity(player); engine.addEntity(npc); engine.update(0.1f);
            assertTrue(player.getComponent(WorldTransformComponent.class).x < 3.5f);
            assertEquals(6.8f,npc.getComponent(WorldTransformComponent.class).x,0.001f);
            assertEquals(-6.5f,npc.getComponent(WorldTransformComponent.class).z,0.001f);
        } finally { Gdx.input = previous; }
    }
    @Test public void instancesAreIndependentAndRemovingActorsDoesNotDisposeSharedModel() {
        final boolean[] disposed = {false};
        Model model = new Model() { @Override public void dispose() { disposed[0] = true; super.dispose(); } };
        CharacterFactory factory = new CharacterFactory(CharacterDefinition.VAULT_DWELLER, model);
        Entity a = factory.create(1,0,-1,true), b = factory.create(3,0,-3,false);
        ModelComponent ma = a.getComponent(ModelComponent.class), mb = b.getComponent(ModelComponent.class);
        assertSame(ma.instance.model,mb.instance.model);
        assertNotSame(ma.instance,mb.instance);
        ma.instance.transform.setToTranslation(7,0,0);
        assertEquals(0, mb.instance.transform.getTranslation(new Vector3()).x, 0);
        Engine engine = new Engine(); engine.addEntity(a); engine.addEntity(b); engine.removeAllEntities();
        assertFalse(disposed[0]);
        model.dispose(); assertTrue(disposed[0]);
    }
}
