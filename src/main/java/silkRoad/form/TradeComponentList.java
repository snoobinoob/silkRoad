package silkRoad.form;

import java.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;
import necesse.gfx.forms.components.FormContentBox;
import silkRoad.Trade;

public class TradeComponentList extends FormContentBox {
    private List<Trade> trades;
    private TradeComponent.Type tradeType;
    private Consumer<Integer> tradeRemovalListener;

    public TradeComponentList(List<Trade> trades, int x, int y, int width, int height,
            TradeComponent.Type tradeType, Consumer<Integer> tradeRemovalListener) {
        this(trades, x, y, width, height, tradeType, tradeRemovalListener,
                new Rectangle(0, 0, width, height));
    }

    public TradeComponentList(List<Trade> trades, int x, int y, int width, int height,
            TradeComponent.Type tradeType, Consumer<Integer> tradeRemovalListener,
            Rectangle contentRect) {
        super(x, y, width, height, null, contentRect);
        this.trades = trades;
        this.tradeType = tradeType;
        this.tradeRemovalListener = tradeRemovalListener;
        alwaysShowVerticalScrollBar = true;
        updateTradeComponents();
    }

    public void updateTradeComponents() {
        clearComponents();
        int i = 0;
        for (Trade trade : trades) {
            TradeComponent tradeComponent =
                    addComponent(new TradeComponent(0, 36 * i, trade, tradeType));
            tradeComponent.addCancelButton(comp -> {
                tradeRemovalListener.accept(trade.id);
            });
            i++;
        }
        fitContentBoxToComponents();
    }
}
