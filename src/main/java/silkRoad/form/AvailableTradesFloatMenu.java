package silkRoad.form;

import necesse.gfx.forms.Form;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import silkRoad.tradingPost.TradingPostContainer;

public class AvailableTradesFloatMenu extends FormFloatMenu {
    private static final int WIDTH = 156;
    private static final int HEIGHT = 140;

    private TradingPostContainer container;

    public AvailableTradesFloatMenu(InTradesForm parent, TradingPostContainer container) {
        super(parent);
        Form form = new Form(WIDTH, HEIGHT);

        this.container = container;

        TradeComponentList list = form
                .addComponent(new TradeComponentList(container.objectEntity.trades.availableTrades,
                        0, 0, WIDTH, HEIGHT, TradeComponent.Type.INCOMING, tradeId -> {
                            container.subscribeAction.runAndSend(tradeId);
                            remove();
                        }));

        container.objectEntity.trades.onChanged(hashCode(), () -> list.updateTradeComponents());

        setForm(form);
    }

    @Override
    public void dispose() {
        super.dispose();
        container.objectEntity.trades.removeListener(hashCode());
    }
}
