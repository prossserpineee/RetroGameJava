package com.prossserpineee.retrotanki22bi.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.prossserpineee.retrotanki22bi.GameType;
import com.prossserpineee.retrotanki22bi.GameApp;
import com.prossserpineee.retrotanki22bi.components.FlagViewComponent;

import static com.almasb.fxgl.dsl.FXGL.*;


//Пуля не отличает базу от противника,
//как только она попадает во флаг, сразу поражение.

public class BulletToFlag extends CollisionHandler {

    public BulletToFlag() {
        super(GameType.BULLET, GameType.FLAG);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity flag) {
        if (!getb("gameOver")) {
            FlagViewComponent flagComponent = flag.getComponent(FlagViewComponent.class);
            flagComponent.hitFlag();
            bullet.removeFromWorld();
            GameApp app = getAppCast();
            if (!getb("gameOver")) {
                set("gameOver", true);
                getSceneService().pushSubScene(app.failedSceneLazyValue.get());
            }
        }
    }
}
