package com.prossserpineee.retrotanki22bi.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

//Флаг сбит, происходит замена png

public class FlagViewComponent extends Component {
    public void hitFlag() {
        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(FXGL.texture("map/flag_failed.png"));
    }
}
