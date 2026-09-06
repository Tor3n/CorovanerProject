package io.github.TorenDropProject.world;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.utils.Disposable;
import io.github.TorenDropProject.entities.components.*;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector3;

/** All world renderables share this batch, camera, and uninterrupted depth buffer. */
public final class WorldRenderer implements Disposable {
    private final ModelBatch batch = new ModelBatch();
    private final Environment environment = new Environment();
    private final WorldCameraRig rig;
    private final BattleSession session;
    private final Model markerModel;
    private final ModelInstance selectionMarker, destinationMarker;
    private final ImmutableArray<Entity> renderables;
    public WorldRenderer(WorldCameraRig rig, BattleSession session) {
        this.rig = rig;
        this.session = session;
        markerModel = createMarker();
        selectionMarker = new ModelInstance(markerModel);
        destinationMarker = new ModelInstance(markerModel);
        destinationMarker.materials.first().set(ColorAttribute.createDiffuse(1f, 0.65f, 0.18f, 1f));
        renderables = session.engine.getEntitiesFor(Family.all(ModelComponent.class).get());
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.48f, 0.46f, 0.43f, 1f));
        environment.add(new DirectionalLight().set(0.85f, 0.81f, 0.72f, -0.6f, -1f, -0.8f));
    }
    public void render(WorldMap map) {
        rig.apply();
        // Scene2D may leave a different depth mask; establish the world pass explicitly.
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glClearColor(0.08f, 0.085f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin(rig.camera());
        for (WorldMap.Chunk chunk : map.chunks) {
            if (rig.camera().frustum.boundsInFrustum(chunk.bounds)) batch.render(chunk.instance, environment);
        }
        for (Entity entity : renderables) {
            ModelComponent model = entity.getComponent(ModelComponent.class);
            if (rig.camera().frustum.boundsInFrustum(model.worldBounds)) batch.render(model.instance, environment);
        }
        Entity selected = session.selected();
        if (selected != null) {
            WorldTransformComponent p = selected.getComponent(WorldTransformComponent.class);
            selectionMarker.transform.setToTranslation(p.x, p.y + 0.035f, p.z);
            batch.render(selectionMarker);
            NavigationComponent route = selected.getComponent(NavigationComponent.class);
            if (route.active()) {
                Vector3 target = route.waypoints.peek();
                destinationMarker.transform.setToTranslation(target.x, target.y + 0.04f, target.z);
                batch.render(destinationMarker);
            }
        }
        batch.end();
    }
    private Model createMarker() {
        ModelBuilder builder = new ModelBuilder();
        builder.begin();
        MeshPartBuilder mesh = builder.part("selection-ring", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position,
            new Material(ColorAttribute.createDiffuse(0.35f, 1f, 0.5f, 1f), IntAttribute.createCullFace(GL20.GL_NONE)));
        for (int i = 0; i < 40; i++) {
            double a = i * Math.PI / 20, b = (i + 1) * Math.PI / 20;
            Vector3 p = new Vector3((float)Math.cos(a), 0, (float)Math.sin(a));
            Vector3 q = new Vector3((float)Math.cos(b), 0, (float)Math.sin(b));
            mesh.rect(new Vector3(p).scl(0.42f), new Vector3(q).scl(0.42f),
                new Vector3(q).scl(0.48f), new Vector3(p).scl(0.48f), Vector3.Y);
        }
        return builder.end();
    }
    @Override public void dispose() { batch.dispose(); markerModel.dispose(); }
}
