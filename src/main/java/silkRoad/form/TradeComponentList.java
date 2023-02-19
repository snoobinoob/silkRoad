package silkRoad.form;

import java.awt.Rectangle;
import java.util.function.Consumer;
import necesse.gfx.forms.components.FormContentBox;
import silkRoad.Trade;
import silkRoad.TradeRegistry;
import silkRoad.utils.ListenerList;

public class TradeComponentList extends FormContentBox {
    private ListenerList<Integer> tradeIds;
    private TradeComponent.Type tradeType;
    private Consumer<Integer> tradeRemovalListener;

    public TradeComponentList(ListenerList<Integer> tradeIds, int x, int y, int width, int height,
            TradeComponent.Type tradeType, Consumer<Integer> tradeRemovalListener) {
        this(tradeIds, x, y, width, height, tradeType, tradeRemovalListener,
                new Rectangle(0, 0, width, height));
    }

    public TradeComponentList(ListenerList<Integer> tradeIds, int x, int y, int width, int height,
            TradeComponent.Type tradeType, Consumer<Integer> tradeRemovalListener,
            Rectangle contentRect) {
        super(x, y, width, height, null, contentRect);
        this.tradeIds = tradeIds;
        this.tradeType = tradeType;
        this.tradeRemovalListener = tradeRemovalListener;
        alwaysShowVerticalScrollBar = true;
        updateTradeComponents();
        tradeIds.addListener(() -> updateTradeComponents());
    }

    private void updateTradeComponents() {
        clearComponents();
        int i = 0;
        for (int tradeId : tradeIds) {
            Trade trade = TradeRegistry.getTrade(tradeId);
            if (trade != null) {
                TradeComponent tradeComponent =
                        addComponent(new TradeComponent(0, 36 * i, trade, tradeType));
                tradeComponent.addCancelButton(comp -> {
                    tradeRemovalListener.accept(tradeId);
                });
                i++;
            }
        }
        fitContentBoxToComponents();
    }
}
