package com.prossserpineee.retrotanki22bi;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.ui.ProgressBar;
import com.prossserpineee.retrotanki22bi.components.EnemyComponent;
import com.prossserpineee.retrotanki22bi.components.FlagViewComponent;
import com.prossserpineee.retrotanki22bi.components.PlayerComponent;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

//Класс инструментов для создания объектов
public class GameEntityFactory implements EntityFactory {

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        //Компонент здоровья
        HealthIntComponent hpComponent = new HealthIntComponent(GameConfig.PLAYER_HEALTH);
        //Индикатор выполнения (работоспособность)
        ProgressBar hpView = new ProgressBar(false);
        hpView.setFill(Color.LIGHTGREEN);
        hpView.setMaxValue(GameConfig.PLAYER_HEALTH);
        hpView.setWidth(35);
        hpView.setHeight(8);
        hpView.setTranslateY(42);
        //Значение индикатора выполнения привязано к значению компонента работоспособности
        hpView.currentValueProperty().bind(hpComponent.valueProperty());
        //Разные очки здоровья, разные цвета индикатора состояния здоровья
        hpComponent.valueProperty().addListener((ob, ov, nv) -> {
            int hpValue = nv.intValue();
            if (hpValue >= GameConfig.PLAYER_HEALTH * 0.7) {
                hpView.setFill(Color.LIGHTGREEN);
            } else if (hpValue >= GameConfig.PLAYER_HEALTH * 0.4) {
                hpView.setFill(Color.GOLD);
            } else {
                hpView.setFill(Color.RED);
            }
        });
        return FXGL.entityBuilder(data).type(GameType.PLAYER).bbox(BoundingShape.box(39, 39)).view("tank/H1U.png").view(hpView).with(new EffectComponent()).with(hpComponent).with(new PlayerComponent()).collidable().build();
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.ENEMY)
                .bbox(BoundingShape.box(39, 39))
                .with(new EnemyComponent())
                .collidable()
                .build();
    }

    @Spawns("flag")
    public Entity newFlag(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.FLAG)
                .bbox(BoundingShape.box(48, 48))
                .with(new FlagViewComponent())
                .collidable()
                .neverUpdated()
                .build();
    }

    @Spawns("brick")
    public Entity newBrick(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.BRICK)
                .viewWithBBox("map/brick.png")
                .bbox(BoundingShape.box(24, 24))
                .collidable()
                .neverUpdated()
                .build();
    }


    private static final List<Image> STONE_BRICK_FLASH_IMAGE_LIST = new ArrayList<>();
    private static final AnimationChannel STONE_BRICK_AC;

    static {
        STONE_BRICK_FLASH_IMAGE_LIST.add(FXGL.image("map/stone.png"));
        STONE_BRICK_FLASH_IMAGE_LIST.add(FXGL.image("map/brick.png"));
        STONE_BRICK_AC = new AnimationChannel(STONE_BRICK_FLASH_IMAGE_LIST, Duration.seconds(.5));
    }

    @Spawns("stone")
    public Entity newStone(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.STONE)
                .bbox(BoundingShape.box(24,24))
                .collidable()
                .neverUpdated()
                .build();
    }

    @Spawns("itemStone")
    public Entity newItemStone(SpawnData data) {
        AnimatedTexture animatedTexture = new AnimatedTexture(STONE_BRICK_AC);
        Entity entity = FXGL.entityBuilder(data)
                .type(GameType.STONE)
                .viewWithBBox(animatedTexture)
                .collidable()
                .build();
        //В течение последних нескольких секунд будет мигать вспышка,
        // предупреждающая игрока о том, что время защиты базы вот-вот истечет.
        return entity;
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        double speed;
        String textureStr;
        Entity owner = data.get("owner");
        CollidableComponent collidableComponent = new CollidableComponent(true);
        //Обнаруживать коллизии, игнорировать однотипные;
        collidableComponent.addIgnoredType(owner.getType());
        if (GameType.PLAYER == owner.getType()) {
            int bulletLevel = FXGL.geti("playerBulletLevel");
            if (bulletLevel < GameConfig.PLAYER_BULLET_MAX_LEVEL) {
                textureStr = "bullet/normal.png";
            } else {
                textureStr = "bullet/plus.png";
            }
            speed = GameConfig.PLAYER_BULLET_SPEED + bulletLevel * 60;
        } else {
            speed = GameConfig.ENEMY_BULLET_SPEED;
            textureStr = "bullet/normal.png";
        }
        return FXGL.entityBuilder(data)
                .type(GameType.BULLET)
                .viewWithBBox(textureStr)
                .with(collidableComponent)
                .with(new ProjectileComponent(data.get("direction"), speed))
                .build();
    }


    @Spawns("spawnBox")
    public Entity newSpawnBox(SpawnData data) {
        return FXGL.entityBuilder(data)
                .at(data.getX() - 5, data.getY() - 5)
                .bbox(BoundingShape.box(50, 50))
                .collidable()
                .neverUpdated()
                .build();
    }

    @Spawns("item")
    public Entity newItem(SpawnData data) {

        AnimationChannel ac = new AnimationChannel(FXGL.image("item/" + data.<BonusType>get("itemType").toString().toLowerCase() + ".png"), 1, 30, 28, Duration.seconds(.5), 0, 1);
        AnimatedTexture animatedTexture = new AnimatedTexture(ac);
        Entity entity = FXGL.entityBuilder(data)
                .type(GameType.ITEM)
                .viewWithBBox(animatedTexture)
                .scale(1.2, 1.2)
                .with(new ExpireCleanComponent(GameConfig.ITEM_SHOW_TIME))
                .collidable()
                .zIndex(200)
                .build();
        FXGL.runOnce(animatedTexture::loop, GameConfig.ITEM_NORMAL_SHOW_TIME);
        return entity;
    }

    @Spawns("borderWall")
    public Entity newBorderWall(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.BORDER_WALL)
                .viewWithBBox(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height"), Color.web("#666666")))
                .neverUpdated()
                .collidable()
                .build();
    }
}
