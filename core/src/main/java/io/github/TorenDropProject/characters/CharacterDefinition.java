package io.github.TorenDropProject.characters;

/** Appearance and gameplay dimensions are independent of factories and renderers. */
public final class CharacterDefinition {
    public static final CharacterDefinition VAULT_DWELLER = new CharacterDefinition(
        "vault_dweller", "models/vault_dweller/vault_dweller.obj", 1f, 2.2f, 0.18f);
    public final String id;
    public final String modelPath;
    public final float scale, height, collisionRadius;
    public CharacterDefinition(String id, String modelPath, float scale, float height, float collisionRadius) {
        this.id = id;
        this.modelPath = modelPath;
        this.scale = scale;
        this.height = height;
        this.collisionRadius = collisionRadius;
    }
}
