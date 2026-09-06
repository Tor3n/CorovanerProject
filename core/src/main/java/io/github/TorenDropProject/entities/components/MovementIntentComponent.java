package io.github.TorenDropProject.entities.components;
import com.badlogic.ashley.core.Component;
public final class MovementIntentComponent implements Component { public float x, z; public float speed = 3f; public float maxDistance = Float.POSITIVE_INFINITY; }
