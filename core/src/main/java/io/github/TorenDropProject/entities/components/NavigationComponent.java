package io.github.TorenDropProject.entities.components;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
/** World-space waypoints, owned by one actor. */
public final class NavigationComponent implements Component {
    public final Array<Vector3> waypoints = new Array<>();
    public int next;
    public boolean active() { return next < waypoints.size; }
    public void clear() { waypoints.clear(); next = 0; }
}
