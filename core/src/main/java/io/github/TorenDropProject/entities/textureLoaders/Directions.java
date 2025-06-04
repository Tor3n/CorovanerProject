package io.github.TorenDropProject.entities.textureLoaders;

import java.util.stream.Stream;

public enum Directions {

    RIGHT_UP(0, "right_up"),
    RIGHT(1, "right"),
    RIGHT_DOWN(2, "right_down"),
    LEFT_DOWN(3, "left_down"),
    LEFT(4, "left"),
    LEFT_UP(5, "left_up");

    private final int id;
    private final String direction;

    Directions(int id, String direction){
        this.id = id;
        this.direction = direction;
    }

    public static Directions of (Integer id) {
        return Stream.of(values())
            .filter(t -> t.getId() == id)
            .findAny()
            .orElse(null);
    }

    public int getId() {
        return this.id;
    }

}
