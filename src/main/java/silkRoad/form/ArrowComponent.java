package silkRoad.form;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormCustomDraw;

public class ArrowComponent extends FormCustomDraw {
    private int rotation;

    public ArrowComponent(int x, int y, int rotation) {
        super(x, y, 32, 32);
        this.rotation = rotation;
    }

    public ArrowComponent(int x, int y) {
        this(x, y, 90);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Settings.UI.arrow.initDraw().color(Color.BLACK).rotate(rotation).draw(getX(), getY());
    }

}
