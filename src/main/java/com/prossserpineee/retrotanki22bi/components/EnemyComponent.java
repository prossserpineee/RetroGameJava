package com.prossserpineee.retrotanki22bi.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityGroup;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import com.prossserpineee.retrotanki22bi.GameConfig;
import javafx.util.Duration;

import java.util.List;
import java.util.Random;

import static com.prossserpineee.retrotanki22bi.GameType.*;


//Поведение вражеских танков, движущихся случайным образом, наталкивающихся на препятствия:
//1. Увеличьте шанс выстрела; (помогите врагу открыть больше земляных стен)
//2. Увеличьте шанс поворота; (помогите врагу избежать камней)

public class EnemyComponent extends Component {
    private BoundingBoxComponent bbox;
    private LocalTimer shootTimer = FXGL.newLocalTimer();
    private Random random = new Random();
    private double speed = 0;
    private Dir moveDir;
    private LazyValue<EntityGroup> blocks = new LazyValue<>(() -> entity.getWorld().getGroup(BRICK, FLAG, STONE, ENEMY, PLAYER, BORDER_WALL));

    //Не может двигаться в течение 3 секунд, прежде чем войти в бой
    private boolean canMove;
    private static AnimationChannel animationChannel = new AnimationChannel(FXGL.image("tank/spawnTank.png"), Duration.seconds(0.4), 4);

    private static Dir[] dirs = Dir.values();

    @Override
    public void onUpdate(double tpf) {
        speed = tpf * GameConfig.ENEMY_SPEED;
        if (FXGL.getb("freezingEnemy") || !canMove) {
            return;
        }

        if (moveDir == Dir.UP && random.nextInt(1000) > 880) {
            moveDir = dirs[random.nextInt(4)];
        } else if (random.nextInt(1000) > 980) {
            moveDir = dirs[random.nextInt(4)];
        }
        setMoveDir(moveDir);
        if (random.nextInt(1000) > 980) {
            shoot();
        }
    }

    public void shoot() {
        if (!shootTimer.elapsed(GameConfig.ENEMY_SHOOT_DELAY)) {
            return;
        }
        FXGL.spawn("bullet", new SpawnData(getEntity().getCenter().add(-4, -4)).put("direction", moveDir.getVector()).put("owner", entity)
        );
        shootTimer.capture();
    }
    //Движения противника
    public void setMoveDir(Dir moveDir) {
        this.moveDir = moveDir;
        switch (moveDir) {
            case UP -> up();
            case DOWN -> down();
            case LEFT -> left();
            case RIGHT -> right();
            default -> {
            }
        }
    }

    private void right() {
        getEntity().setRotation(90);
        move();
    }

    private void left() {
        getEntity().setRotation(270);
        move();

    }

    private void down() {
        getEntity().setRotation(180);
        move();

    }

    private void up() {
        getEntity().setRotation(0);
        move();

    }
    //При появлении противника на карте
    @Override
    public void onAdded() {
        moveDir = Dir.DOWN;
        Texture texture = FXGL.texture(entity.getString("assentName"));
        AnimatedTexture animatedTexture = new AnimatedTexture(animationChannel).loop();
        entity.getViewComponent().addChild(animatedTexture);
        FXGL.runOnce(() -> {
            if (entity != null && entity.isActive()) {
                entity.getViewComponent().addChild(texture);
                entity.getViewComponent().removeChild(animatedTexture);
                canMove = true;
            }
        }, Duration.seconds(1));
    }

    private Vec2 velocity = new Vec2();



    //Движения противников по карте
    private void move() {
        if (!getEntity().isActive()) {
            return;
        }
        velocity.set((float) (moveDir.getVector().getX()*speed), (float) (moveDir.getVector().getY()*speed));
        int length = Math.round(velocity.length());
        velocity.normalizeLocal();

        List<Entity> blockList = blocks.get().getEntitiesCopy();
        for (int i = 0; i < length; i++) {
            entity.translate(velocity.x, velocity.y);
            boolean collision = false;
            Entity entityTemp;
            for (int j = 0; j < blockList.size(); j++) {
                entityTemp = blockList.get(j);
                if (entityTemp == entity) {
                    continue;
                }
                if (entityTemp.getBoundingBoxComponent().isCollidingWith(bbox)) {
                    collision = true;
                    break;
                }
            }
            if (collision) {
                entity.translate(-velocity.x, -velocity.y);
                //Увеличьте вероятность выстрела после столкновения
                if (FXGLMath.randomBoolean(0.6)) {
                    shoot();
                }
                //Увеличьте вероятность изменения направления после столкновения
                if (FXGLMath.randomBoolean(0.3)) {
                    setMoveDir(Dir.values()[random.nextInt(4)]);
                }

                break;
            }
        }
    }

}
