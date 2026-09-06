package io.github.TorenDropProject.entities.components;
import com.badlogic.ashley.core.Component;
public final class WorldTransformComponent implements Component {
    public float x, y, z, yawDegrees;
    public WorldTransformComponent(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
}
