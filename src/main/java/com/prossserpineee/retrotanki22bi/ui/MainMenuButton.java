package com.prossserpineee.retrotanki22bi.ui;

import com.almasb.fxgl.texture.Texture;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.play;
import static com.almasb.fxgl.dsl.FXGL.texture;

/*
 * Переключение кнопок главного меню
 */
public class MainMenuButton extends RadioButton {

    public MainMenuButton(String text, Runnable action) {
        Texture texture = texture("ui/icon.png");
        texture.setRotate(180);
        texture.setVisible(false);
        setGraphic(texture);
        setGraphicTextGap(30);
        getStyleClass().add("main-menu-btn");
        setText(text);
        //Если выбран этот параметр, будет отображаться изображение танка спереди
        selectedProperty().addListener((ob, ov, nv) -> texture.setVisible(nv));
        //Нажмите Enter, чтобы выполнить метод, соответствующий этой кнопке
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                action.run();
            }
        });
        //Нажмите кнопку, чтобы выполнить метод, соответствующий этой кнопке
        setOnMouseClicked(event -> {
            action.run();
        });
        //Наведите курсор мыши, выберите кнопку
        setOnMouseEntered(e -> {
                    setSelected(true);
                }
        );
        //Выберите, когда вы получите фокус.
        focusedProperty().addListener((ob, ov, nv) -> {
            if (nv) {
                setSelected(true);
            }
        });
    }
}
