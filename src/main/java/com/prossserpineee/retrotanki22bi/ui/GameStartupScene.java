package com.prossserpineee.retrotanki22bi.ui;

import com.almasb.fxgl.app.scene.StartupScene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/*
Стартовая сцена игры
*/
public class GameStartupScene extends StartupScene {
    public GameStartupScene(int appWidth, int appHeight) {
        super(appWidth, appHeight);
        StackPane pane = new StackPane(new ImageView(getClass().getResource("/assets/textures/ui/logo1.png").toExternalForm()));
        pane.setPrefSize(appWidth, appHeight);
        pane.setStyle("-fx-background-color: black");
        getContentRoot().getChildren().addAll(pane);
    }
}
