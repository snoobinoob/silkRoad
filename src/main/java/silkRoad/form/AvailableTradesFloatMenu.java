package silkRoad.form;

import java.awt.Point;
import java.util.stream.Collectors;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import silkRoad.TradeRegistry;
import silkRoad.tradingPost.TradingPostContainer;
import silkRoad.utils.ListenerList;

public class AvailableTradesFloatMenu extends FormFloatMenu {
    private static final int WIDTH = 156;
    private static final int HEIGHT = 140;

    private ListenerList<Integer> tradeIds;
    private Point location;

    public AvailableTradesFloatMenu(InTradesForm parent, TradingPostContainer container) {
        super(parent);
        location = container.getIslandLocation();
        Form form = new Form(WIDTH, HEIGHT);
        tradeIds = new ListenerList<>();
        updateTrades();
        TradeRegistry.onTradesModified(() -> updateTrades());

        form.addComponent(
                new TradeComponentList(tradeIds, 0, 0, WIDTH, HEIGHT, TradeComponent.Type.INCOMING,
                        tradeId -> container.subscribeTradeAction.runAndSend(tradeId)));

        setForm(form);
    }

    private void updateTrades() {
        tradeIds.clear(false);
        tradeIds.addAll(TradeRegistry.getAvailableTrades(location).map(trade -> trade.getId())
                .collect(Collectors.toList()));
    }
}
