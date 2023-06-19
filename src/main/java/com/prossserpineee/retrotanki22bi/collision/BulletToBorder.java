package com.prossserpineee.retrotanki22bi.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.prossserpineee.retrotanki22bi.GameType;

//Обнаружение столкновения: пуля сталкивается с границей, и пуля исчезает
public class BulletToBorder extends CollisionHandler {

    public BulletToBorder() {
        super(GameType.BULLET, GameType.BORDER_WALL);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity border) {
        bullet.removeFromWorld();
    }
}
