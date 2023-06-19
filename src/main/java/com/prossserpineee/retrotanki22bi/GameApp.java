package com.prossserpineee.retrotanki22bi;

import com.almasb.fxgl.app.CursorInfo;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.*;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.time.TimerAction;
import com.prossserpineee.retrotanki22bi.collision.*;
import com.prossserpineee.retrotanki22bi.components.PlayerComponent;
import com.prossserpineee.retrotanki22bi.ui.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GameApp extends GameApplication {

    private Entity player;
    private PlayerComponent playerComponent;
    private Random random = new Random();
    public LazyValue<FailedScene> failedSceneLazyValue = new LazyValue<>(FailedScene::new);
    private LazyValue<SuccessScene> successSceneLazyValue = new LazyValue<>(SuccessScene::new);

    //Три позиции спавна противников
    private int[] enemySpawnX = {30, 295 + 30, 589 + 20};

    //Действие таймера усиления основания
    private TimerAction spadeTimerAction;

    //Действие таймера счетчика замораживания противника
    private TimerAction freezingTimerAction;

    //Регулярный спавн вражеских танков

    private TimerAction spawnEnemyTimerAction;

    @Override
    protected void onPreInit() {
        getSettings().setGlobalSoundVolume(0.5);
        getSettings().setGlobalMusicVolume(0.5);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(28 * 24 + 6 * 24);
        settings.setHeight(28 * 24);
        settings.setTitle("Мир танков");
        settings.setAppIcon("ui/icon.png");
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.getCSSList().add("tankApp.css");
        settings.setDefaultCursor(new CursorInfo("ui/cursor.png", 0, 0));
        settings.getCSSList();


        settings.setSceneFactory(new SceneFactory() {
            @Override
            public StartupScene newStartup(int width, int height) {
                //Пользовательская сцена запуска
                return new GameStartupScene(width, height);
            }

            @Override
            public FXGLMenu newMainMenu() {
                //Сцена главного меню
                return new GameMainMenu();
            }

            @Override
            public LoadingScene newLoadingScene() {
                //Загрузка сцены перед игрой
                return new GameLoadingScene();
            }
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("level", 1);
        vars.put("playerBulletLevel", 1);
        vars.put("freezingEnemy", false);
        vars.put("destroyedEnemy", 0);
        vars.put("spawnedEnemy", 0);
        vars.put("gameOver", false);
    }
    //Кнопки управления
    @Override
    protected void initInput() {
        onKey(KeyCode.W, this::moveUpAction);
        onKey(KeyCode.UP, this::moveUpAction);

        onKey(KeyCode.S, this::moveDownAction);
        onKey(KeyCode.DOWN, this::moveDownAction);

        onKey(KeyCode.A, this::moveLeftAction);
        onKey(KeyCode.LEFT, this::moveLeftAction);

        onKey(KeyCode.D, this::moveRightAction);
        onKey(KeyCode.RIGHT, this::moveRightAction);

        onKey(KeyCode.SPACE, this::shootAction);
        onKey(KeyCode.F, this::shootAction);
    }

    //Движение танка, выстрелы, если жив
    private boolean tankIsReady() {
        return player != null && playerComponent != null && !getb("gameOver") && player.isActive();
    }

    private void shootAction() {
        if (tankIsReady()) {
            playerComponent.shoot();
        }
    }

    private void moveRightAction() {
        if (tankIsReady()) {
            playerComponent.right();
        }
    }

    private void moveLeftAction() {
        if (tankIsReady()) {
            playerComponent.left();
        }
    }

    private void moveDownAction() {
        if (tankIsReady()) {
            playerComponent.down();
        }
    }

    private void moveUpAction() {
        if (tankIsReady()) {
            playerComponent.up();
        }
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);
        getGameWorld().addEntityFactory(new GameEntityFactory());
        buildAndStartLevel();
        getip("destroyedEnemy").addListener((ob, ov, nv) -> {
            //Увеличение количества противников в зависимости от уровня
            if (nv.intValue() == GameConfig.ENEMY_AMOUNT + (5 * geti("level"))) {
                set("gameOver", true);
                runOnce(() -> getSceneService().pushSubScene(successSceneLazyValue.get()), Duration.seconds(1.5));
            }
        });
    }

    public void buildAndStartLevel() {
        //1. Происходит очистка остатков уровня
        getGameWorld().getEntitiesByType(GameType.BULLET, GameType.ENEMY, GameType.PLAYER).forEach(Entity::removeFromWorld);

        //2. Открывающая анимация
        Rectangle rect1 = new Rectangle(getAppWidth(), getAppHeight() / 2.0, Color.web("#333333"));
        Rectangle rect2 = new Rectangle(getAppWidth(), getAppHeight() / 2.0, Color.web("#333333"));
        rect2.setLayoutY(getAppHeight() / 2.0);
        Text text = new Text("STAGE " + geti("level"));
        text.setFill(Color.WHITE);
        text.setFont(new Font(35));
        text.setLayoutX(getAppWidth() / 2.0 - 80);
        text.setLayoutY(getAppHeight() / 2.0 - 5);
        Pane p1 = new Pane(rect1, rect2, text);

        addUINode(p1);

        Timeline timeLine = new Timeline(new KeyFrame(Duration.seconds(1.2), new KeyValue(rect1.translateYProperty(), -getAppHeight() / 2.0), new KeyValue(rect2.translateYProperty(), getAppHeight() / 2.0)));
        timeLine.setOnFinished(e -> removeUINode(p1));

        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1.5));
        pauseTransition.setOnFinished(e -> {
            text.setVisible(false);
            timeLine.play();
            //3. Начинается новый уровень
            startLevel();
        });
        pauseTransition.play();
    }

    private void startLevel() {
        if (spawnEnemyTimerAction != null) {
            spawnEnemyTimerAction.expire();
            spawnEnemyTimerAction = null;
        }
        set("gameOver", false);
        //Удалите эффекты оставшегося реквизита с предыдущего уровня
        set("freezingEnemy", false);
        //Восстановите количество уничтоженных противников
        set("destroyedEnemy", 0);
        //Восстановите количество сгенерированных противников
        set("spawnedEnemy", 0);

        expireAction(freezingTimerAction);
        expireAction(spadeTimerAction);


        setLevelFromMap("level" + geti("level") + ".tmx");

        player = null;
        player = spawn("player", 9 * 24 + 3, 25 * 24);
        playerComponent = player.getComponent(PlayerComponent.class);
        //Пользовательский интерфейс, отображающий информацию
        getGameScene().addGameView(new GameView(new InfoPane(), 100));
        //Сначала уничтожьте несколько вражеских танков, чтобы заспавнились новые
        for (int i = 0; i < enemySpawnX.length; i++) {
            spawn("enemy",
                    new SpawnData(enemySpawnX[i], 30).put("assentName", "tank/E" + FXGLMath.random(1, 12) + "U.png"));
            inc("spawnedEnemy", 1);
        }
        spawnEnemy();
    }

    private void spawnEnemy() {
        if (spawnEnemyTimerAction != null) {
            spawnEnemyTimerAction.expire();
            spawnEnemyTimerAction = null;
        }

        //Объект, используемый для обнаружения столкновений (чтобы избежать столкновения танков с другими танками,
        // как только он будет сгенерирован)
        Entity spawnBox = spawn("spawnBox", new SpawnData(-100, -100));

        //Таймер, используемый для генерации противника, регулярно пытается сгенерировать
        // вражеский танк, но если местоположение сгенерированного вражеского танка занято
        // другими существующими танками, то на этот раз вражеский танк сгенерирован не будет.
        spawnEnemyTimerAction = run(() -> {
            //Количество попыток сгенерировать вражеские танки; 2 или 3 раза
            int testTimes = FXGLMath.random(2, 3);
            for (int i = 0; i < testTimes; i++) {
                if (geti("spawnedEnemy") < GameConfig.ENEMY_AMOUNT + (5 * geti("level"))) {
                    boolean canGenerate = true;
                    //Случайным образом извлекаем координату x из массива
                    int x = enemySpawnX[random.nextInt(3)];
                    int y = 30;
                    spawnBox.setPosition(x, y);
                    List<Entity> tankList = getGameWorld().getEntitiesByType(GameType.ENEMY, GameType.PLAYER);
                    //Если позиция приближающегося вражеского танка противоречит
                    // текущей позиции существующего
                    // танка, то здесь танк сгенерирован не будет.
                    for (Entity tank : tankList) {
                        if (tank.isActive() && spawnBox.isColliding(tank)) {
                            canGenerate = false;
                            break;
                        }
                    }
                    //Если вражеский танк может быть сгенерирован, то он сгенерируется
                    if (canGenerate) {
                        inc("spawnedEnemy", 1);
                        spawn("enemy",
                                new SpawnData(x, y).put("assentName", "tank/E" + FXGLMath.random(1, 12) + "U.png"));
                    }
                    //Скрыть объект
                    spawnBox.setPosition(-100, -100);

                } else {
                    if (spawnEnemyTimerAction != null) {
                        spawnEnemyTimerAction.expire();
                    }
                }
            }
        }, GameConfig.SPAWN_ENEMY_TIME);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new BulletToEnemy());
        getPhysicsWorld().addCollisionHandler(new BulletToPlayer());
        BulletToBrick bulletBrickHandler = new BulletToBrick();
        getPhysicsWorld().addCollisionHandler(bulletBrickHandler);
        getPhysicsWorld().addCollisionHandler(bulletBrickHandler.copyFor(GameType.BULLET, GameType.STONE));
        getPhysicsWorld().addCollisionHandler(new BulletToFlag());
        getPhysicsWorld().addCollisionHandler(new BulletToBorder());
        getPhysicsWorld().addCollisionHandler(new BulletToBullet());
        getPhysicsWorld().addCollisionHandler(new PlayerToItem());
    }

    public void freezingEnemy() {
        expireAction(freezingTimerAction);
        set("freezingEnemy", true);
        freezingTimerAction = runOnce(() -> {
            set("freezingEnemy", false);
        }, GameConfig.STOP_MOVE_TIME);
    }

    public void spadeBackUpBase() {
        expireAction(spadeTimerAction);
        //База для модернизации окружена каменными стенами
        updateWall(true);
        spadeTimerAction = runOnce(() -> {
            //Стены вокруг основания восстановлены до кирпичных
            updateWall(false);
        }, GameConfig.SPADE_TIME);
    }
    //Оборона вокруг базы
    //Согласно правилам игры: по умолчанию используется кирпичная стена.
    //После того, как вы съедите лопату, она будет преобразована в каменную стену;
    private void updateWall(boolean isStone) {
        //Пройдите по кругу, чтобы найти стену, окружающую основание
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                if (row != 0 && (col == 1 || col == 2)) {
                    continue;
                }
                //Удалите старую стену
                List<Entity> entityTempList = getGameWorld().getEntitiesAt(new Point2D(288 + col * 24, 576 + row * 24));
                for (Entity entityTemp : entityTempList) {
                    Serializable type = entityTemp.getType();
                    //Если это карта, построенная самим игроком,
                    //то вам нужно определить, вода ли это, трава, снег и т.д.
                    if (type == GameType.STONE || type == GameType.BRICK) {
                        if (entityTemp.isActive()) {
                            entityTemp.removeFromWorld();
                        }
                    }
                }
                //Создайте новую стену
                if (isStone) {
                    spawn("itemStone", new SpawnData(288 + col * 24, 576 + row * 24));
                } else {
                    spawn("brick", new SpawnData(288 + col * 24, 576 + row * 24));
                }
            }
        }
    }

    //TimeAction
    public void expireAction(TimerAction action) {
        if (action == null) {
            return;
        }
        if (!action.isExpired()) {
            action.expire();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
