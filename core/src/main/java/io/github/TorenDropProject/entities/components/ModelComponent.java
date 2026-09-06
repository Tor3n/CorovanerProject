package io.github.TorenDropProject.entities.components;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
public final class ModelComponent implements Component {
    public final ModelInstance instance;
    public final float scale;
    public final BoundingBox localBounds = new BoundingBox();
    public final BoundingBox worldBounds = new BoundingBox();
    public ModelComponent(ModelInstance instance, float scale) {
        this.instance = instance;
        this.scale = scale;
        instance.calculateBoundingBox(localBounds);
    }
}
