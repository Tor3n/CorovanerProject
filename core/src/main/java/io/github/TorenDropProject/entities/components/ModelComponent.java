package io.github.TorenDropProject.entities.components;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.g3d.model.Node;
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
    /** After the ECS update, combine an animated socket/bone with an item's local grip offset. */
    public Matrix4 attachmentTransform(String socket, Matrix4 gripOffset, Matrix4 out) {
        Node node = instance.getNode(socket, true);
        if (node == null) throw new IllegalArgumentException("Unknown character socket: " + socket);
        return out.set(instance.transform).mul(node.globalTransform).mul(gripOffset);
    }
}
