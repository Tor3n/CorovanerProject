package io.github.TorenDropProject.world;

import com.badlogic.gdx.utils.Disposable;
import io.github.TorenDropProject.entities.components.WorldTransformComponent;

/** A single world scene, reused by battle and development preview screens. */
public final class WorldScene implements Disposable {
    public final BattleSession session;
    public final WorldCameraRig cameraRig = new WorldCameraRig();
    private final WorldRenderer renderer;
    public WorldScene(BattleSession session) {
        this.session = session;
        renderer = new WorldRenderer(cameraRig, session);
        follow();
    }
    public void render(float delta, boolean paused) {
        if (!paused) session.update(delta);
        follow();
        renderer.render(session.map);
    }
    private void follow() {
        WorldTransformComponent position = session.followed().getComponent(WorldTransformComponent.class);
        cameraRig.follow(position.x, position.y, position.z);
    }
    public void resize(int width, int height) { cameraRig.resize(width, height); }
    @Override public void dispose() { renderer.dispose(); session.clear(); }
}
