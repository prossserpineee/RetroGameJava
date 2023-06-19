package com.prossserpineee.retrotanki22bi.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.scene.SubScene;
import com.prossserpineee.retrotanki22bi.GameConfig;
import com.prossserpineee.retrotanki22bi.GameType;
import com.prossserpineee.retrotanki22bi.GameApp;
import javafx.animation.PauseTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.inc;


/*
Сцена в случае успешного окончания уровня
*/
public class SuccessScene extends SubScene {
    private final PauseTransition pauseTransition;
    public SuccessScene() {
        Rectangle rect = new Rectangle(getAppWidth(), getAppHeight(), Color.web("#666666"));
        Text hiText = new Text("HI-SCORE");
        hiText.setFont(Font.font(30));
        hiText.setFill(Color.web("#B53021"));
        hiText.setLayoutY(260);
        hiText.setLayoutX(222);
        Text scoreText = new Text("20000");
        scoreText.setFont(Font.font(30));
        scoreText.setFill(Color.web("#EAA024"));
        scoreText.setLayoutY(260);
        scoreText.setLayoutX(472);
        Text levelText = new Text();
        levelText.setFont(Font.font(25));
        levelText.textProperty().bind(getip("level").asString("STAGE %d"));
        levelText.setFill(Color.web("#EAA024"));
        levelText.setLayoutY(360);
        levelText.setLayoutX(347);

        getContentRoot().getChildren().addAll(rect, hiText, scoreText, levelText);

        pauseTransition = new PauseTransition(Duration.seconds(2));
        pauseTransition.setOnFinished(event -> {
            if (geti("level") == GameConfig.MAX_LEVEL) {
                getDialogService().showConfirmationBox("WIN! Passed all levels. Continue?", result -> {
                    if (result) {
                        getGameController().gotoMainMenu();
                    } else {
                        getGameController().exit();
                    }
                });
            } else {
                FXGL.getSceneService().popSubScene();
                inc("level", 1);
                FXGL.<GameApp>getAppCast().buildAndStartLevel();
            }
        });
    }

    @Override
    public void onCreate() {
        //Происходит очистка остатков уровня
        getGameWorld().getEntitiesByType(GameType.BULLET, GameType.ENEMY, GameType.PLAYER).forEach(Entity::removeFromWorld);
        pauseTransition.play();
    }
}
