package io.github.TorenDropProject.world;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.Arrays;
import java.util.PriorityQueue;

/** A* over terrain cells. Every edge checks actor clearance and elevation, including diagonal corners. */
public final class GridPathfinder {
    private final WorldGrid grid;
    public GridPathfinder(WorldGrid grid) { this.grid = grid; }

    public Array<Vector3> find(float x, float y, float z, float targetX, float targetZ, float radius) {
        Array<Vector3> path = new Array<>();
        if (!Float.isFinite(targetX) || !Float.isFinite(targetZ)) return path;
        int sc = grid.column(x), sr = grid.row(z), tc = grid.column(targetX), tr = grid.row(targetZ);
        if (!grid.canStand(x, z, radius, y) || !grid.walkable(tc, tr)) return path;
        float goalY = grid.elevation(tc, tr);
        if (!grid.canStand(targetX, targetZ, radius, goalY)) {
            targetX = grid.centerX(tc); targetZ = grid.centerZ(tr);
        }
        if (!grid.canStand(targetX, targetZ, radius, goalY)) return path;
        int start = sr * grid.width + sc, goal = tr * grid.width + tc;
        int[] parent = new int[grid.width * grid.height];
        float[] cost = new float[parent.length];
        Arrays.fill(parent, -1); Arrays.fill(cost, Float.POSITIVE_INFINITY);
        PriorityQueue<Node> open = new PriorityQueue<>();
        cost[start] = 0; open.add(new Node(start, 0, heuristic(sc, sr, tc, tr)));
        while (!open.isEmpty()) {
            Node node = open.remove();
            if (node.cost > cost[node.id]) continue;
            if (node.id == goal) {
                for (int at = goal; at != -1; at = parent[at]) {
                    int col = at % grid.width, row = at / grid.width;
                    path.add(new Vector3(grid.centerX(col), grid.elevation(col, row), grid.centerZ(row)));
                }
                path.reverse();
                // Skip a start-center detour only when the actor's actual position has clearance.
                if (path.size == 1 && clearSegment(x, z, targetX, targetZ, radius, y)) path.clear();
                else if (path.size > 1 && clearSegment(x, z, path.get(1).x, path.get(1).z, radius, y)) path.removeIndex(0);
                path.add(new Vector3(targetX, goalY, targetZ));
                return path;
            }
            int col = node.id % grid.width, row = node.id / grid.width;
            for (int dr = -1; dr <= 1; dr++) for (int dc = -1; dc <= 1; dc++) {
                if (dc == 0 && dr == 0) continue;
                int nc = col + dc, nr = row + dr;
                if (!grid.walkable(nc, nr) || !clearSegment(grid.centerX(col), grid.centerZ(row),
                    grid.centerX(nc), grid.centerZ(nr), radius, grid.elevation(col, row))) continue;
                int next = nr * grid.width + nc;
                float value = cost[node.id] + (dc == 0 || dr == 0 ? 1f : 1.41421356f);
                if (value >= cost[next]) continue;
                parent[next] = node.id; cost[next] = value;
                open.add(new Node(next, value, value + heuristic(nc, nr, tc, tr)));
            }
        }
        return path;
    }
    private boolean clearSegment(float x, float z, float tx, float tz, float radius, float height) {
        int steps = (int)Math.ceil(Math.hypot(tx - x, tz - z) / (grid.cellSize * 0.1f));
        for (int i = 0; i <= steps; i++) {
            float t = steps == 0 ? 0 : (float)i / steps;
            if (!grid.canStand(x + (tx - x) * t, z + (tz - z) * t, radius, height)) return false;
        }
        return true;
    }
    private float heuristic(int x, int y, int tx, int ty) { return (float)Math.hypot(tx - x, ty - y); }
    private static final class Node implements Comparable<Node> {
        final int id;
        final float cost, estimate;
        Node(int id, float cost, float estimate) { this.id = id; this.cost = cost; this.estimate = estimate; }
        @Override public int compareTo(Node other) { return Float.compare(estimate, other.estimate); }
    }
}
