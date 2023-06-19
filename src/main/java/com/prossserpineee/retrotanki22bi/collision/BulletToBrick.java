package com.prossserpineee.retrotanki22bi.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.prossserpineee.retrotanki22bi.GameConfig;
import com.prossserpineee.retrotanki22bi.GameType;

import java.io.Serializable;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;


//Пули сталкиваются со стенами;
//Так как пуля может попасть в то место, где встречаются два объекта, нужно учесть эти случаи;
//в зависимости от объекта происходит различная обработка.
//Если пуля попадет в кирпичную стену, то и пуля, и стена исчезнут.
//Если вы попадете по камню, то пуля исчезнет
//Если это мощная пуля, то камень также исчезнет

public class BulletToBrick extends CollisionHandler {

    public BulletToBrick() {
        super(GameType.BULLET, GameType.BRICK);
    }


    //Нужно проверять все столкновения
    @Override
    protected void onCollision(Entity bullet, Entity brick) {
        Entity tank = bullet.getObject("owner");
        Serializable tankType = tank.getType();
        //Поиск сталкивающихся объектов (кирпичи и камни)
        List<Entity> list = getGameWorld().getEntitiesFiltered(tempE ->
                tempE.getBoundingBoxComponent().isCollidingWith(bullet.getBoundingBoxComponent()) && (tempE.isType(GameType.STONE) || tempE.isType(GameType.BRICK))
        );
        boolean removeBullet = false;
        for (Entity entity : list) {
            Serializable entityType = entity.getType();
            if (entityType == GameType.BRICK) {
                removeBullet = true;
                if (entity.isActive()) {
                    entity.removeFromWorld();
                }
            }
            else { //камень
                removeBullet = true;
                if (tankType == GameType.PLAYER && entity.isActive() && geti("playerBulletLevel") == GameConfig.PLAYER_BULLET_MAX_LEVEL) {
                    entity.removeFromWorld();
                }
            }
        }
        //Пуля исчезает
        if (removeBullet) {
            bullet.removeFromWorld();
        }
    }
}
