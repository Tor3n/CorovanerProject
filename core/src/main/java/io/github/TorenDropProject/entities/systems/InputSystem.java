package io.github.TorenDropProject.entities.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.TorenDropProject.entities.PlayerEntityFactory;

public class InputSystem extends IteratingSystem {
    private boolean isInputEntityRelated;

    public InputSystem() {
        super(Family.all(PlayerEntityFactory.PositionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        PlayerEntityFactory.VelocityComponent velocity = entity.getComponent(PlayerEntityFactory.VelocityComponent.class);
        velocity.dx = 0f;
        velocity.dy = 0f;

        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            velocity.dy=+10;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            velocity.dy=-10;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            velocity.dx=-10;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            velocity.dx=+10;
        }

        //mouseTouch
        if(Gdx.input.isTouched()){
            //Gdx.input.getX();
            //Gdx.input.getY();
            //touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            //main.viewport.unproject(touchPos);
            //playerTestSprite.setCenterX(touchPos.x);
            //playerTestSprite.setCenterY(touchPos.y);
        }
    }

    public void setInputEntityRelated(boolean inputEntityRelated) {
        this.isInputEntityRelated = inputEntityRelated;
    }

}
