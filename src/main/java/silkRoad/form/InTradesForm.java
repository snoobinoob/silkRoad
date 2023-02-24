package silkRoad.form;

import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.ui.ButtonColor;
import silkRoad.tradingPost.TradingPostContainer;

public class InTradesForm extends Form {
    private FormLocalTextButton browseTradesButton;
    private TradeComponentList list;

    public InTradesForm(Client client, TradingPostContainer container) {
        super(156, 198);

        browseTradesButton = addComponent(new FormLocalTextButton("ui", "browsetrades", 4, 4,
                getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE));
        browseTradesButton.onClicked(e -> {
            AvailableTradesFloatMenu menu = new AvailableTradesFloatMenu(this, container);
            getManager().openFloatMenu(menu);
        });
        browseTradesButton.setCooldown(100);
        list = addComponent(new TradeComponentList(container.objectEntity.trades.incomingTrades, 4,
                32, getWidth() - 8, getHeight() - 36, TradeComponent.Type.INCOMING,
                tradeId -> container.unsubscribeAction.runAndSend(tradeId),
                Settings.UI.button_escaped_20,
                () -> container.settlementObjectManager.hasSettlementAccess,
                new LocalMessage("ui", "unsubscribetrade")));
    }

    public void update() {
        list.updateTradeComponents();
    }
}

