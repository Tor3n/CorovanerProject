package io.github.TorenDropProject.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/** The sole world projection. Fixed orientation keeps pre-rendered cutout art coherent. */
public final class WorldCameraRig {
    private final OrthographicCamera camera = new OrthographicCamera();
    // ref.jpg's standing figures occupy roughly 16% of the image height. A 2.2-unit
    // actor projects to 1.9 units at 30 degrees, so frame 12 vertical world units.
    // Match the reference aspect here; wider windows reveal more terrain horizontally.
    private final ExtendViewport viewport = new ExtendViewport(14.5f, 12f, camera);
    private final Vector3 offset = new Vector3(24f, 24f * (float)Math.sqrt(2f / 3f), 24f);
    private final Vector3 target = new Vector3();

    public WorldCameraRig() {
        camera.near = 0.1f;
        camera.far = 256f;
        follow(0, 0, 0);
    }
    public OrthographicCamera camera() { return camera; }
    public void follow(float x, float y, float z) {
        target.set(x, y + 0.8f, z);
        camera.position.set(target).add(offset);
        camera.up.set(Vector3.Y);
        camera.lookAt(target);
        camera.update();
    }
    public void resize(int width, int height) { viewport.update(Math.max(1, width), Math.max(1, height), false); }
    public void apply() { viewport.apply(); }
    public void zoom(float amount) {
        camera.zoom = Math.max(0.4f, Math.min(2f, camera.zoom + amount));
        camera.update();
    }
    public Ray pickRay(float screenX, float screenY) {
        return camera.getPickRay(screenX, screenY, viewport.getScreenX(), viewport.getScreenY(),
            viewport.getScreenWidth(), viewport.getScreenHeight());
    }
    public boolean pickGround(float screenX, float screenY, WorldGrid grid, Vector3 out) {
        Ray ray = pickRay(screenX, screenY);
        float closest = Float.POSITIVE_INFINITY;
        if (Math.abs(ray.direction.y) < 0.0001f) return false;
        // Exact per-cell height intersection; picking is event-driven, not a frame loop.
        for (int row = 0; row < grid.height; row++) {
            for (int col = 0; col < grid.width; col++) {
                if (!grid.hasGround(col, row)) continue;
                float t = (grid.elevation(col, row) - ray.origin.y) / ray.direction.y;
                if (t < 0 || t >= closest) continue;
                float x = ray.origin.x + t * ray.direction.x;
                float z = ray.origin.z + t * ray.direction.z;
                if (grid.column(x) == col && grid.row(z) == row) {
                    closest = t;
                    out.set(x, grid.elevation(col, row), z);
                }
            }
        }
        return closest < Float.POSITIVE_INFINITY;
    }
}
