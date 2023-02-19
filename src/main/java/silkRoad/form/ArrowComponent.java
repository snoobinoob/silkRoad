package silkRoad.form;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormCustomDraw;

public class ArrowComponent extends FormCustomDraw {
    public ArrowComponent(int x, int y) {
        super(x, y, 32, 32);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Settings.UI.arrow.initDraw().color(Color.BLACK).rotate(90).draw(getX(), getY());
    }

}
