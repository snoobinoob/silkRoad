package silkRoad.form;

import java.awt.Rectangle;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.ui.ButtonColor;
import silkRoad.tradingPost.TradingPostContainer;

public class InTradesForm extends Form {
    private TradingPostContainer container;
    private FormLocalTextButton acceptTradeButton;

    public InTradesForm(Client client, TradingPostContainer container) {
        super(156, 198);
        this.container = container;

        acceptTradeButton = addComponent(new FormLocalTextButton("ui", "acceptTrade", 4, 4,
                getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE));
        acceptTradeButton.onClicked(e -> {
            AvailableTradesFloatMenu menu = new AvailableTradesFloatMenu(this, container);
            getManager().openFloatMenu(menu);
        });
        acceptTradeButton.setCooldown(100);
        addComponent(new TradeComponentList(container.objectEntity.getIncomingTradeIds(), 4, 32,
                getWidth() - 8, getHeight() - 36, TradeComponent.Type.INCOMING,
                tradeId -> container.unsubscribeTradeAction.runAndSend(tradeId)));
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        acceptTradeButton.setActive(container.settlementObjectManager.foundSettlement);
        super.draw(tickManager, perspective, renderBox);
    }
}

