package io.github.TorenDropProject.entities;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import io.github.TorenDropProject.entities.textureLoaders.MsDOSFalloutLeatherJacketLoader;

public class PlayerEntityFactory {
    public Engine ashleyEngine;
    //public MsDOSFalloutLeatherJacketLoader loader;
    AssetManager assetManager;

    public PlayerEntityFactory(Engine ashleyEngine, AssetManager assetManager) {
        this.ashleyEngine = ashleyEngine;
        this.assetManager = assetManager;
    }

    public Entity createPlayer(){
        Entity player = new Entity();

        player.add(new PositionComponent());
        player.add(new VelocityComponent());
        player.add(new TextureComponent());

        PositionComponent position = player.getComponent(PositionComponent.class);
        position.x = 25;
        position.y = -10;

        TextureComponent textureComp = player.getComponent(TextureComponent.class);

        textureComp.texture = assetManager.get("char.png", Texture.class);

        ashleyEngine.addEntity(player);
        //loader = new MsDOSFalloutLeatherJacketLoader(assetManager);
        return player;
    }

    public void getComponents(Entity entity){
        ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
        ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

        PositionComponent position = pm.get(entity);
        VelocityComponent velocity = vm.get(entity);

        Family family = Family.all(PositionComponent.class, VelocityComponent.class).get();

        //Family family = Family.all(PositionComponent.class)
        //    .one(TextureComponent.class, ParticleComponent.class)
        //    .exclude(InvisibleComponent.class)
        //    .get();
    }

    public class PositionComponent implements Component {
        public float x = 0.0f;
        public float y = 0.0f;
    }

    public class VelocityComponent implements Component {
        public float dx = 0.0f;
        public float dy = 0.0f;
    }

    public class TextureComponent implements Component {
        public Texture texture;
    }
}
