package io.github.TorenDropProject.world;

/** CPU-only terrain/collision data. libGDX Tiled rows increase toward negative world Z. */
public final class WorldGrid {
    public final int width;
    public final int height;
    public final float cellSize;
    private final float[] elevations;
    private final boolean[] ground;
    private final boolean[] blocked;

    public WorldGrid(int width, int height, float cellSize) {
        if (width <= 0 || height <= 0 || !Float.isFinite(cellSize) || cellSize <= 0) {
            throw new IllegalArgumentException("Invalid grid size");
        }
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
        elevations = new float[width * height];
        ground = new boolean[elevations.length];
        blocked = new boolean[elevations.length];
    }

    public int column(float x) { return (int)Math.floor(x / cellSize); }
    public int row(float z) { return (int)Math.floor(-z / cellSize); }
    public float centerX(int column) { return (column + 0.5f) * cellSize; }
    public float centerZ(int row) { return -(row + 0.5f) * cellSize; }
    public boolean contains(int column, int row) {
        return column >= 0 && row >= 0 && column < width && row < height;
    }
    public void setGround(int column, int row, float elevation) {
        ground[row * width + column] = true;
        elevations[row * width + column] = elevation;
    }
    public void block(int column, int row) { blocked[row * width + column] = true; }
    public float elevation(int column, int row) { return elevations[row * width + column]; }
    public boolean hasGround(int column, int row) {
        return contains(column, row) && ground[row * width + column];
    }
    public boolean walkable(int column, int row) {
        return contains(column, row) && ground[row * width + column] && !blocked[row * width + column];
    }
    public boolean canStand(float x, float z, float radius, float fromHeight) {
        if (!Float.isFinite(x) || !Float.isFinite(z) || !Float.isFinite(radius)
            || !Float.isFinite(fromHeight) || radius < 0) return false;
        for (int row = row(z + radius); row <= row(z - radius); row++) {
            for (int col = column(x - radius); col <= column(x + radius); col++) {
                if (!walkable(col, row) || Math.abs(elevation(col, row) - fromHeight) > 0.25f) return false;
            }
        }
        return true;
    }
}
