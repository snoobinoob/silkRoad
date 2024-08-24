package silkRoad.form;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import silkRoad.SilkRoad;
import silkRoad.Trade;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TradeComponent extends FormContentBox {
    public FormItemIconBackground exportComponent;
    public FormItemIconBackground importComponent;

    private FormContentIconButton button;
    public Supplier<Boolean> canClickButton;

    public enum Type {
        INCOMING, OUTGOING
    }

    public TradeComponent(int x, int y, Trade trade) {
        this(x, y, trade, Type.OUTGOING);
    }

    public TradeComponent(int x, int y, Trade trade, Type type) {
        super(x, y, 148, 32);
        if (type == Type.OUTGOING) {
            exportComponent = addComponent(new FormItemIconBackground(0, 0, () -> trade.exportItem,
                    SilkRoad.exportTooltips));
            addComponent(new ArrowComponent(32, 0));
            importComponent = addComponent(new FormItemIconBackground(64, 0, () -> trade.importItem,
                    SilkRoad.importTooltips));
        } else {
            importComponent = addComponent(new FormItemIconBackground(0, 0, () -> trade.importItem,
                    SilkRoad.exportTooltips));
            addComponent(new ArrowComponent(32, 0));
            exportComponent = addComponent(new FormItemIconBackground(64, 0, () -> trade.exportItem,
                    SilkRoad.importTooltips));
        }
    }

    public void addButton(Consumer<TradeComponent> listener, ButtonIcon buttonIcon,
            GameMessage... tooltips) {
        button = addComponent(new FormContentIconButton(this.getWidth() - 42, 6,
                FormInputSize.SIZE_20, ButtonColor.BASE, buttonIcon, tooltips));
        button.onClicked(e -> listener.accept(this));
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (canClickButton != null) {
            button.setActive(canClickButton.get());
        }
        super.draw(tickManager, perspective, renderBox);
    }
}
