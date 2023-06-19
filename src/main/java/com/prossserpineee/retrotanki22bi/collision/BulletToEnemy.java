package com.prossserpineee.retrotanki22bi.collision;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.CollisionHandler;
import com.prossserpineee.retrotanki22bi.GameConfig;
import com.prossserpineee.retrotanki22bi.GameType;
import com.prossserpineee.retrotanki22bi.BonusType;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;



//Столкновение между пулей и врагом
//Уничтожьте врага, а также уничтожьте пули

public class BulletToEnemy extends CollisionHandler {

    public BulletToEnemy() {
        super(GameType.BULLET, GameType.ENEMY);
    }

    protected void onCollisionBegin(Entity bullet, Entity enemy) {
        bullet.removeFromWorld();
        enemy.removeFromWorld();
        inc("destroyedEnemy", 1);
        //Шанс выпадения бонуса
        if (FXGLMath.randomBoolean(GameConfig.SPAWN_ITEM_PRO)) {
            spawn("item", new SpawnData(FXGLMath.random(50, getAppWidth() - 50 - 6 * 24), FXGLMath.random(50, getAppHeight() - 50)).put("itemType", FXGLMath.random(BonusType.values()).get()));
        }
    }
}
