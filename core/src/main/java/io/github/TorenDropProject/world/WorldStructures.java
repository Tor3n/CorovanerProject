package io.github.TorenDropProject.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;
import java.util.Map;

/** Descriptor-authored static structures. CPU validation/collision precede GL-thread mesh creation. */
public final class WorldStructures {
    private WorldStructures() { }
    public static Array<Structure> prepare(JsonValue definitions, WorldGrid grid) {
        Array<Structure> result = new Array<>();
        if (definitions == null) return result;
        for (JsonValue definition : definitions) {
            int column = definition.getInt("column"), row = grid.height - 1 - definition.getInt("row");
            require(grid.contains(column, row), "Structure anchor outside map");
            Structure structure = new Structure();
            structure.origin.set(grid.centerX(column), grid.elevation(column, row), grid.centerZ(row));
            float width = definition.getFloat("footprintWidth"), depth = definition.getFloat("footprintDepth");
            require(Float.isFinite(width) && Float.isFinite(depth) && width > 0 && depth > 0, "Invalid structure footprint");
            for (int r = grid.row(structure.origin.z + depth / 2); r <= grid.row(structure.origin.z - depth / 2); r++) {
                for (int c = grid.column(structure.origin.x - width / 2); c <= grid.column(structure.origin.x + width / 2); c++) {
                    require(grid.contains(c, r), "Structure footprint outside map");
                    grid.block(c, r);
                }
            }
            require(definition.has("parts"), "Structure needs parts");
            for (JsonValue value : definition.get("parts")) {
                Part part = new Part();
                part.shape = value.getString("shape", "box");
                require(part.shape.equals("box") || part.shape.equals("cylinder"), "Unknown structure shape");
                part.size.set(value.getFloat("width"), value.getFloat("height"), value.getFloat("depth"));
                part.position.set(value.getFloat("x", 0), value.getFloat("y", 0), value.getFloat("z", 0));
                part.rotation.set(value.getFloat("pitch", 0), value.getFloat("yaw", 0), value.getFloat("roll", 0));
                require(finite(part.size) && finite(part.position) && finite(part.rotation)
                    && part.size.x > 0 && part.size.y > 0 && part.size.z > 0, "Invalid structure part");
                part.color = Color.valueOf(value.getString("color"));
                structure.parts.add(part);
            }
            require(structure.parts.notEmpty(), "Empty structure");
            result.add(structure);
        }
        return result;
    }
    public static Model upload(Structure structure) {
        ModelBuilder builder = new ModelBuilder(); builder.begin();
        Map<Color, Array<Part>> surfaces = new HashMap<>();
        for (Part part : structure.parts) surfaces.computeIfAbsent(part.color, color -> new Array<>()).add(part);
        int index = 0;
        for (Map.Entry<Color, Array<Part>> surface : surfaces.entrySet()) {
            MeshPartBuilder mesh = builder.part("surface-" + index++, GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(surface.getKey())));
            for (Part part : surface.getValue()) {
                mesh.setVertexTransform(new Matrix4().setToTranslation(new Vector3(structure.origin).add(part.position))
                    .rotate(Vector3.X, part.rotation.x).rotate(Vector3.Y, part.rotation.y).rotate(Vector3.Z, part.rotation.z));
                if (part.shape.equals("cylinder")) mesh.cylinder(part.size.x, part.size.y, part.size.z, 20);
                else mesh.box(part.size.x, part.size.y, part.size.z);
            }
        }
        return builder.end();
    }
    private static boolean finite(Vector3 value) { return Float.isFinite(value.x) && Float.isFinite(value.y) && Float.isFinite(value.z); }
    private static void require(boolean condition, String message) { if (!condition) throw new GdxRuntimeException(message); }
    public static final class Structure {
        final Vector3 origin = new Vector3();
        final Array<Part> parts = new Array<>();
    }
    private static final class Part {
        String shape;
        Color color;
        final Vector3 position = new Vector3(), size = new Vector3(), rotation = new Vector3();
    }
}
