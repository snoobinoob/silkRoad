package silkRoad.form;

import java.util.function.Consumer;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import silkRoad.Trade;

public class TradeComponent extends FormContentBox {
    public FormItemIconBackground exportComponent;
    public FormItemIconBackground importComponent;

    public enum Type {
        INCOMING, OUTGOING
    }

    public TradeComponent(int x, int y, Trade trade) {
        this(x, y, trade, Type.OUTGOING);
    }

    public TradeComponent(int x, int y, Trade trade, Type type) {
        super(x, y, 148, 32);
        exportComponent = addComponent(new FormItemIconBackground(type == Type.OUTGOING ? 0 : 64, 0,
                () -> trade.exportItem));
        addComponent(new ArrowComponent(32, 0));
        importComponent = addComponent(new FormItemIconBackground(type == Type.OUTGOING ? 64 : 0, 0,
                () -> trade.importItem));
    }

    public void setTrade(Trade trade) {
        exportComponent.setItemSupplier(() -> trade.exportItem);
        importComponent.setItemSupplier(() -> trade.importItem);
    }

    public void addButton(Consumer<TradeComponent> listener, ButtonIcon buttonIcon,
            GameMessage... tooltips) {
        FormContentIconButton cancelButton =
                addComponent(new FormContentIconButton(this.getWidth() - 42, 6,
                        FormInputSize.SIZE_20, ButtonColor.BASE, buttonIcon, tooltips));
        cancelButton.onClicked(e -> listener.accept(this));
    }
}
