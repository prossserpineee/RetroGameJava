package com.prossserpineee.retrotanki22bi.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import com.prossserpineee.retrotanki22bi.GameType;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/*
Сцена после поражения
*/
public class FailedScene extends SubScene {

    private final TranslateTransition translateTransition;

    public FailedScene() {
        Texture gameOverTexture = texture("ui/GameOver.png");
        gameOverTexture.setScaleX(2);
        gameOverTexture.setScaleY(2);
        gameOverTexture.setTranslateY(getAppHeight() - gameOverTexture.getHeight() + 24);
        gameOverTexture.setTranslateX(28*24 / 2.0
                - gameOverTexture.getWidth() / 2.0);
        translateTransition = new TranslateTransition(Duration.seconds(3.8), gameOverTexture);
        translateTransition.setInterpolator(Interpolators.ELASTIC.EASE_OUT());
        translateTransition.setToY(getAppHeight() / 2.0 - gameOverTexture.getHeight() / 2);
        translateTransition.setOnFinished(e -> {
            FXGL.getSceneService().popSubScene();
            gameOverTexture.setTranslateY(getAppHeight() - gameOverTexture.getHeight() + 24);
            //Происходит очитска уровня
            getGameWorld().getEntitiesByType(GameType.BULLET, GameType.ENEMY, GameType.PLAYER).forEach(Entity::removeFromWorld);
            getGameController().gotoMainMenu();
        });
        getContentRoot().getChildren().add(gameOverTexture);

    }

    @Override
    public void onCreate() {
        translateTransition.play();
    }


}
