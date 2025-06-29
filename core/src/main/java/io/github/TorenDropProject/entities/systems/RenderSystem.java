package io.github.TorenDropProject.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.TorenDropProject.entities.PlayerEntityFactory;

public class RenderSystem extends IteratingSystem {
    private SpriteBatch batch;
    Sprite playerSprite;


    public RenderSystem(SpriteBatch batch) {
        super(Family.all(PlayerEntityFactory.PositionComponent.class).get());
        this.batch = batch;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerEntityFactory.PositionComponent position = entity.getComponent(PlayerEntityFactory.PositionComponent.class);

        PlayerEntityFactory.TextureComponent textureComponent = entity.getComponent(PlayerEntityFactory.TextureComponent.class);
        if(playerSprite==null){
            playerSprite = new Sprite(textureComponent.texture);
            playerSprite.setSize(1.5f,3f);
        }

        playerSprite.setX(position.x);
        playerSprite.setY(position.y);
        playerSprite.draw(batch);

    }
}
