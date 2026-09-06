package io.github.TorenDropProject.entities.components;
import com.badlogic.ashley.core.Component;
public final class CollisionComponent implements Component {
    public final float radius;
    public CollisionComponent(float radius) { this.radius = radius; }
}
