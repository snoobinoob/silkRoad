package silkRoad.form;

import java.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameMath;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonIcon;
import silkRoad.Trade;

public class TradeComponentList extends FormContentBox {
    private List<Trade> trades;
    private TradeComponent.Type tradeType;
    private Consumer<Integer> tradeRemovalListener;
    private ButtonIcon buttonIcon;
    private Supplier<Boolean> canClickButton;
    private GameMessage[] removeButtonTooltips;

    public TradeComponentList(List<Trade> trades, int x, int y, int width, int height,
            TradeComponent.Type tradeType, Consumer<Integer> tradeRemovalListener,
            ButtonIcon buttonIcon, Supplier<Boolean> canClickButton,
            GameMessage... removeButtonTooltips) {
        this(trades, x, y, width, height, tradeType, tradeRemovalListener,
                new Rectangle(0, 0, width, height), buttonIcon, canClickButton,
                removeButtonTooltips);
    }

    public TradeComponentList(List<Trade> trades, int x, int y, int width, int height,
            TradeComponent.Type tradeType, Consumer<Integer> tradeRemovalListener,
            Rectangle contentRect, ButtonIcon buttonIcon, Supplier<Boolean> canClickButton,
            GameMessage... removeButtonTooltips) {
        super(x, y, width, height, null, contentRect);
        this.trades = trades;
        this.tradeType = tradeType;
        this.tradeRemovalListener = tradeRemovalListener;
        this.buttonIcon = buttonIcon;
        this.canClickButton = canClickButton;
        this.removeButtonTooltips = removeButtonTooltips;
        alwaysShowVerticalScrollBar = true;
        updateTradeComponents();
    }

    public void updateTradeComponents() {
        updateTradeComponents("");
    }

    public void updateTradeComponents(String filter) {
        clearComponents();
        int i = 0;
        for (Trade trade : trades) {
            if (!trade.matchesFilter(filter)) {
                continue;
            }
            TradeComponent tradeComponent =
                    addComponent(new TradeComponent(0, 36 * i, trade, tradeType));
            tradeComponent.addButton(comp -> {
                tradeRemovalListener.accept(trade.id);
            }, buttonIcon, removeButtonTooltips);
            tradeComponent.canClickButton = canClickButton;
            i++;
        }
        Rectangle rect = getContentBox();
        rect.height = GameMath.max(36 * i - 4, getHeight());
        setContentBox(rect);
        if (trades.size() == 0) {
            addComponent(new FormLocalLabel("ui", "empty", new FontOptions(36).color(0, 0, 0, 32),
                    FormLabel.ALIGN_MID, getWidth() / 2, getHeight() / 2 - 22, getWidth()));
        }
    }
}
