package com.prossserpineee.retrotanki22bi;

//import com.almasb.fxgl.core.collection.PropertyMap;
//import com.almasb.fxgl.dsl.FXGL;
import javafx.util.Duration;

public class GameConfig {

    private GameConfig() {
    }

    //private static final PropertyMap map;

    //В конфигурационном файле есть подробные комментарии
    /*static {
        map = FXGL.getAssetLoader().loadPropertyMap("properties/game.properties");
    }*/

    public static final int MAX_LEVEL = 3;//map.getInt("maxLevel");

    //Первоклассные пули могут крушить деревья и попадать в каменные стены
    public static final int PLAYER_BULLET_MAX_LEVEL = 3;//map.getInt("bulletMaxLevel");

    //Количество вражеских танков
    public static final int ENEMY_AMOUNT = 20;//map.getInt("enemyAmount");

    //Здоровье игрока

    public static final int PLAYER_HEALTH = 5;//map.getInt("playerHealth");

    //Скорость пули игрока (базовая скорость + уровень пули *60)
    public static final int PLAYER_BULLET_SPEED = 420;//map.getInt("playerBulletSpeed");

    //Скорость вражеской пули

    public static final int ENEMY_BULLET_SPEED = 450;//map.getInt("enemyBulletSpeed");

    //Интервал стрельбы игрока

    public static final Duration PLAYER_SHOOT_DELAY = Duration.seconds(0.3);

    //Интервал стрельбы противника
    public static final Duration ENEMY_SHOOT_DELAY = Duration.seconds(0.35);



    //Временной реквизит.Время врагу прекратить действовать
    public static final Duration STOP_MOVE_TIME = Duration.seconds(10.0);

    //Когда появится реквизит
    public static final Duration ITEM_SHOW_TIME = Duration.seconds(17.0);

    //Время с момента появления элемента до мигающего напоминания
    public static final Duration ITEM_NORMAL_SHOW_TIME = Duration.seconds(12);

    //Лопата защищает основание
    public static final Duration SPADE_TIME = Duration.seconds(15.0);

    //Лопата вот-вот закончится, и мигающий запрос продлится 15 секунд -12 секунд = 3 секунды.
    // В течение последних 3 секунд мигающий запрос будет выполняться вокруг основания.
    public static final Duration SPADE_NORMAL_TIME = Duration.seconds(12.0);

    //Интервал между генерацией локальных резервуаров
    public static final Duration SPAWN_ENEMY_TIME = Duration.seconds(6.0);

    //Доля сгенерированного реквизита

    public static final double SPAWN_ITEM_PRO = 0.8;

    //Скорость передвижения игрока

    public static final int PLAYER_SPEED = 100;

    //Скорость передвижения противника
    public static final int ENEMY_SPEED = 80;

}
