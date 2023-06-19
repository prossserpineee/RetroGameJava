package com.prossserpineee.retrotanki22bi.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.DialogService;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static javafx.scene.input.KeyCode.*;

/*
Главное меню игры
*/


public class GameMainMenu extends FXGLMenu {

    private final TranslateTransition translateTransition;
    private final Pane defaultPane;

    public GameMainMenu() {
        super(MenuType.MAIN_MENU);
        Texture texture = texture("ui/logo1.png");
        texture.setLayoutX(184);
        texture.setLayoutY(70);

        MainMenuButton newGameBtn = new MainMenuButton("START GAME", this::fireNewGame);

        MainMenuButton helpBtn = new MainMenuButton("HELP", this::instructions);
        MainMenuButton exitBtn = new MainMenuButton("EXIT", () -> getGameController().exit());
        ToggleGroup tg = new ToggleGroup();
        tg.getToggles().addAll(newGameBtn, helpBtn, exitBtn);
        newGameBtn.setSelected(true);
        VBox menuBox = new VBox(
                5,
                newGameBtn,
                helpBtn,
                exitBtn
        );
        menuBox.setAlignment(Pos.CENTER_LEFT);
        menuBox.setLayoutX(240);
        menuBox.setLayoutY(360);
        menuBox.setVisible(false);

        translateTransition = new TranslateTransition(Duration.seconds(2));
        translateTransition.setInterpolator(Interpolators.ELASTIC.EASE_OUT());
        translateTransition.setFromX(172);
        translateTransition.setFromY(252);
        translateTransition.setToX(374);
        translateTransition.setToY(252);
        translateTransition.setOnFinished(e -> menuBox.setVisible(true));

        Rectangle bgRect = new Rectangle(getAppWidth(), getAppHeight());
        Line line = new Line(30, 580, 770, 580);
        line.setStroke(Color.web("#B9380D"));
        line.setStrokeWidth(2);

        defaultPane = new Pane(bgRect, texture, menuBox, line);
        getContentRoot().getChildren().setAll(defaultPane);
    }

    @Override
    public void onCreate() {
        getContentRoot().getChildren().setAll(defaultPane);
        translateTransition.play();
    }



   /*
   Инструкция для игрока (Горячие клавиши)
   */
    private void instructions() {
        GridPane pane = new GridPane();
        pane.setHgap(20);
        pane.setVgap(15);
        KeyView kvW = new KeyView(W);
        kvW.setPrefWidth(38);
        TilePane tp1 = new TilePane(kvW, new KeyView(S), new KeyView(A), new KeyView(D));
        tp1.setPrefWidth(200);
        tp1.setHgap(2);
        tp1.setAlignment(Pos.CENTER_LEFT);

        pane.addRow(0, getUIFactoryService().newText("Movement"), tp1);
        pane.addRow(1, getUIFactoryService().newText("Shoot"), new KeyView(F));
        KeyView kvL = new KeyView(LEFT);
        kvL.setPrefWidth(38);
        TilePane tp2 = new TilePane(new KeyView(UP), new KeyView(DOWN), kvL, new KeyView(RIGHT));
        tp2.setPrefWidth(200);
        tp2.setHgap(2);
        tp2.setAlignment(Pos.CENTER_LEFT);
        pane.addRow(2, getUIFactoryService().newText("Movement"), tp2);
        pane.addRow(3, getUIFactoryService().newText("Shoot"), new KeyView(SPACE));
        DialogService dialogService = getDialogService();
        dialogService.showBox("Help", pane, getUIFactoryService().newButton("OK"));
    }

}
