package com.prossserpineee.retrotanki22bi.collision;

import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.prossserpineee.retrotanki22bi.GameConfig;
import com.prossserpineee.retrotanki22bi.GameType;
import com.prossserpineee.retrotanki22bi.BonusType;
import com.prossserpineee.retrotanki22bi.GameApp;


import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

//* Игроки получают бонусы, и бонусы пропадают с поля
public class PlayerToItem extends CollisionHandler {

    public PlayerToItem() {
        super(GameType.PLAYER, GameType.ITEM);
    }

    protected void onCollisionBegin(Entity player, Entity item) {
        GameApp app = getAppCast();
        BonusType itemType = item.getObject("itemType");
        item.removeFromWorld();
        switch (itemType) {
            //Бонус: бомбы;
            // найдите все вражеские танки на карте и уничтожьте их
            case BOMB -> collisionBomb();

            //Бонус: Танк; игрок восстанавливает 1 здоровье
            case TANK -> collisionTank(player);

            //Бонус: Звезда;
            // если пуля игрока находится не на максимальном уровне, она будет улучшена
            case STAR -> collisionStar();

            //Бонус: лопата;
            //Появляется стена вокруг основания, переделанная в каменную
            case SPADE -> app.spadeBackUpBase();

            //Бонус: Сердце; здоровье игрока восстанавливается до максимального значения
            case HEART -> collisionHeart(player);

            //Бонус: Оружие;
            // пуля игрока поднимается до максимального уровня (эффект будет продолжаться до следующего уровня)
            case GUN -> set("playerBulletLevel", GameConfig.PLAYER_BULLET_MAX_LEVEL);

            default -> {
            }
        }
    }
    //Здоровье игрока
    private void collisionHeart(Entity player) {
        HealthIntComponent hp = player.getComponent(HealthIntComponent.class);
        hp.setValue(hp.getMaxValue());
    }
    //Уровень пули
    private void collisionStar() {
        if (geti("playerBulletLevel") < GameConfig.PLAYER_BULLET_MAX_LEVEL) {
            inc("playerBulletLevel", 1);
        }
    }
    //Урон по игроку
    private void collisionTank(Entity player) {
        HealthIntComponent hp = player.getComponent(HealthIntComponent.class);
        if (hp.getValue() < hp.getMaxValue()) {
            hp.damage(-1);
        }
    }
    //Бонус: бомба
    private void collisionBomb() {
        List<Entity> enemyList = getGameWorld().getEntitiesByType(GameType.ENEMY);
        for (Entity enemy : enemyList) {
            enemy.removeFromWorld();
            inc("destroyedEnemy", 1);
        }
    }

}
