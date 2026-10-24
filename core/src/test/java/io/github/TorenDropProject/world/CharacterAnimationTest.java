package io.github.TorenDropProject.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodeAnimation;
import com.badlogic.gdx.graphics.g3d.model.NodeKeyframe;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxNativesLoader;
import io.github.TorenDropProject.characters.CharacterDefinition;
import io.github.TorenDropProject.characters.CharacterFactory;
import io.github.TorenDropProject.entities.components.*;
import io.github.TorenDropProject.entities.systems.CharacterAnimationSystem;
import io.github.TorenDropProject.entities.systems.ModelTransformSystem;
import io.github.TorenDropProject.entities.systems.MovementSystem;
import io.github.TorenDropProject.entities.systems.InputSystem;
import org.junit.BeforeClass;
import org.junit.Test;
import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

public class CharacterAnimationTest {
    @BeforeClass public static void natives() { GdxNativesLoader.load(); }

    private Model model() {
        Model model = new Model();
        Node hand = new Node();
        hand.id = "hand_r";
        model.nodes.add(hand);
        for (String name : new String[]{"idle", "walk", "run"}) {
            Animation clip = new Animation();
            clip.id = name;
            clip.duration = 1f;
            NodeAnimation track = new NodeAnimation();
            track.node = hand;
            track.translation = new Array<>();
            track.translation.add(new NodeKeyframe<>(0, new Vector3(0, 1, 0)));
            track.translation.add(new NodeKeyframe<>(1, new Vector3(0, 2, 0)));
            clip.nodeAnimations.add(track);
            model.animations.add(clip);
        }
        return model;
    }

    @Test public void actorsHaveIndependentPlaybackAndPauseDoesNotAffectOtherActors() {
        Model shared = model();
        CharacterFactory factory = new CharacterFactory(CharacterDefinition.PROTAGONIST, shared);
        Entity a = factory.create(1, 0, -1, false), b = factory.create(3, 0, -3, false);
        AnimationComponent aa = a.getComponent(AnimationComponent.class), ab = b.getComponent(AnimationComponent.class);
        assertNotSame(aa.controller, ab.controller);
        aa.paused = true;
        Engine engine = new Engine();
        engine.addSystem(new CharacterAnimationSystem());
        engine.addEntity(a); engine.addEntity(b);
        engine.update(0.25f);
        assertEquals(0, aa.controller.current.time, 0);
        assertEquals(0.25f, ab.controller.current.time, 0.0001f);
        assertSame(shared, a.getComponent(ModelComponent.class).instance.model);
        assertNotSame(a.getComponent(ModelComponent.class).instance.getNode("hand_r"),
            b.getComponent(ModelComponent.class).instance.getNode("hand_r"));
        engine.removeAllEntities();
        shared.dispose();
    }

    @Test public void locomotionUsesActualCollisionVelocityAndScalesPlayback() {
        WorldGrid grid = new WorldGrid(8, 8, 1);
        for (int row = 0; row < 8; row++) for (int col = 0; col < 8; col++) grid.setGround(col, row, 0);
        Entity actor = new CharacterFactory(CharacterDefinition.PROTAGONIST, model()).create(2.5f, 0, -2.5f, false);
        Engine engine = new Engine();
        engine.addSystem(new MovementSystem(grid));
        engine.addSystem(new ModelTransformSystem());
        engine.addSystem(new CharacterAnimationSystem());
        engine.addEntity(actor);
        MovementIntentComponent intent = actor.getComponent(MovementIntentComponent.class);
        AnimationComponent animation = actor.getComponent(AnimationComponent.class);
        intent.x = 1; intent.speed = AnimationComponent.WALK_SPEED / 2;
        engine.update(0.1f);
        assertEquals("walk", animation.clip);
        assertEquals(0.5f, animation.controller.current.speed, 0.0001f);
        intent.speed = AnimationComponent.RUN_SPEED;
        engine.update(0.1f);
        assertEquals("run", animation.clip);
        WorldTransformComponent position = actor.getComponent(WorldTransformComponent.class);
        position.x = 2.81f;
        grid.block(3, 2);
        engine.update(0.1f);
        assertEquals(0, actor.getComponent(VelocityComponent.class).x, 0.0001f);
        assertEquals("idle", animation.clip);
    }

    @Test public void attachmentsCombineAnimatedBoneActorTransformAndGripOffset() {
        Entity actor = new CharacterFactory(CharacterDefinition.PROTAGONIST, model()).create(4, 0, -2, false);
        actor.getComponent(AnimationComponent.class).previewClip = "walk";
        Engine engine = new Engine();
        engine.addSystem(new ModelTransformSystem());
        engine.addSystem(new CharacterAnimationSystem());
        engine.addEntity(actor);
        engine.update(0.5f);
        ModelComponent appearance = actor.getComponent(ModelComponent.class);
        Vector3 socket = appearance.attachmentTransform("hand_r", new Matrix4().setToTranslation(0, 0, 0.3f),
            new Matrix4()).getTranslation(new Vector3());
        assertEquals(4, socket.x, 0.0001f);
        assertEquals(1.5f, socket.y, 0.0001f);
        assertEquals(-1.7f, socket.z, 0.0001f);
        assertTrue(appearance.worldBounds.contains(socket));
    }

    @Test public void releasingShiftRestoresWalkingWhileClickRouteRemainsActive() {
        Input previous = Gdx.input;
        final boolean[] shift = {true};
        Gdx.input = (Input)Proxy.newProxyInstance(Input.class.getClassLoader(), new Class[]{Input.class}, (proxy, method, args) -> {
            if (method.getName().equals("isKeyPressed")) return shift[0] && (int)args[0] == Input.Keys.SHIFT_LEFT;
            if (method.getReturnType() == boolean.class) return false;
            if (method.getReturnType() == int.class) return 0;
            return null;
        });
        try {
            Entity actor = new CharacterFactory(CharacterDefinition.PROTAGONIST, model()).create(2, 0, -2, true);
            NavigationComponent route = actor.getComponent(NavigationComponent.class);
            route.waypoints.add(new Vector3(5, 0, -5));
            Engine engine = new Engine();
            engine.addSystem(new InputSystem());
            engine.addEntity(actor);
            engine.update(0.1f);
            assertEquals(AnimationComponent.RUN_SPEED, actor.getComponent(MovementIntentComponent.class).speed, 0);
            shift[0] = false;
            engine.update(0.1f);
            assertEquals(AnimationComponent.WALK_SPEED, actor.getComponent(MovementIntentComponent.class).speed, 0);
            assertTrue(route.active());
        } finally { Gdx.input = previous; }
    }
}
