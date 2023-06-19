package com.prossserpineee.retrotanki22bi.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityGroup;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.time.LocalTimer;
import com.prossserpineee.retrotanki22bi.GameConfig;
import com.prossserpineee.retrotanki22bi.GameType;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.prossserpineee.retrotanki22bi.GameType.*;


//Поведение игрока, его передвижение и стрельба

public class PlayerComponent extends Component {

    // Для того, чтобы предотвратить движение под углом наклона вверх, наклона вниз и т.д.

    private boolean movedThisFrame = false;
    private double speed = 0;
    private Vec2 velocity = new Vec2();
    private BoundingBoxComponent bbox;

    private LazyValue<EntityGroup> blocksAll = new LazyValue<>(() -> entity.getWorld().getGroup(BRICK, FLAG, STONE, ENEMY, BORDER_WALL));
    private LazyValue<EntityGroup> blocks = new LazyValue<>(() -> entity.getWorld().getGroup(BRICK, FLAG, STONE, ENEMY, BORDER_WALL));
    private LocalTimer shootTimer = FXGL.newLocalTimer();
    private Dir moveDir = Dir.UP;

    @Override
    public void onUpdate(double tpf) {
        speed = tpf * GameConfig.PLAYER_SPEED;
        movedThisFrame = false;
    }

    public void right() {
        if (movedThisFrame) {
            return;
        }
        movedThisFrame = true;
        getEntity().setRotation(90);
        moveDir = Dir.RIGHT;
        move();
    }

    public void left() {
        if (movedThisFrame) {
            return;
        }
        movedThisFrame = true;
        getEntity().setRotation(270);
        moveDir = Dir.LEFT;
        move();

    }

    public void down() {
        if (movedThisFrame) {
            return;
        }
        movedThisFrame = true;
        getEntity().setRotation(180);
        moveDir = Dir.DOWN;
        move();
    }

    public void up() {
        if (movedThisFrame) {
            return;
        }
        movedThisFrame = true;
        getEntity().setRotation(0);
        moveDir = Dir.UP;
        move();
    }
    //Движения игрока по карте
    private void move() {
        if (!getEntity().isActive()) {
            return;
        }
        velocity.set((float) (moveDir.getVector().getX() * speed), (float) (moveDir.getVector().getY() * speed));
        int length = Math.round(velocity.length());
        velocity.normalizeLocal();
        List<Entity> blockList;
        blockList = blocksAll.get().getEntitiesCopy();
        for (int i = 0; i < length; i++) {
            entity.translate(velocity.x, velocity.y);
            boolean collision = false;
            for (int j = 0; j < blockList.size(); j++) {
                if (blockList.get(j).getBoundingBoxComponent().isCollidingWith(bbox)) {
                    collision = true;
                    break;
                }
            }


            //Разворачивайтесь при столкновении с препятствиями
            if (collision) {
                entity.translate(-velocity.x, -velocity.y);
                break;
            }
        }
    }

    //Выстрел игрока
    public void shoot() {
        if (!shootTimer.elapsed(GameConfig.PLAYER_SHOOT_DELAY)) {
            return;
        }
        spawn("bullet", new SpawnData(getEntity().getCenter().add(-4, -4.5)).put("direction", moveDir.getVector()).put("owner", entity));
        shootTimer.capture();
    }

}