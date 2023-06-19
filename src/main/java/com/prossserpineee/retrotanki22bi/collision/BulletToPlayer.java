package com.prossserpineee.retrotanki22bi.collision;

import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.prossserpineee.retrotanki22bi.GameType;
import com.prossserpineee.retrotanki22bi.GameApp;

import static com.almasb.fxgl.dsl.FXGL.*;


//Пули сталкиваются с игроками
//Пуля исчезает, и здоровье игрока снижается

public class BulletToPlayer extends CollisionHandler {

    public BulletToPlayer() {
        super(GameType.BULLET, GameType.PLAYER);
    }

    protected void onCollisionBegin(Entity bullet, Entity player) {
        bullet.removeFromWorld();
        HealthIntComponent hp = player.getComponent(HealthIntComponent.class);
        hp.damage(1);
        GameApp tankApp = getAppCast();
        if (hp.isZero()) {
            if (!getb("gameOver")) {
                player.removeFromWorld();
                set("gameOver", true);
                getSceneService().pushSubScene(tankApp.failedSceneLazyValue.get());
            }
        }
    }
}
