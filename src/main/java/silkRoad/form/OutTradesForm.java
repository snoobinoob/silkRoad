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
import silkRoad.tradingPost.TradingPostObjectEntity;

public class OutTradesForm extends Form {
        public TradingPostContainer container;
        public FormLocalTextButton addTradeButton;

        public OutTradesForm(Client client, TradingPostContainer container) {
                super(156, 198);
                this.container = container;

                addTradeButton = addComponent(new FormLocalTextButton("ui", "newtrade", 4, 4,
                                getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE));
                addComponent(new TradeComponentList(container.objectEntity.getOutgoingTradeIds(), 4,
                                32, getWidth() - 8, getHeight() - 36, TradeComponent.Type.OUTGOING,
                                tradeId -> {
                                        container.removeTradeAction.runAndSend(tradeId);
                                }));
        }

        @Override
        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                addTradeButton.setActive(container.objectEntity.getOutgoingTradeIds()
                                .size() < TradingPostObjectEntity.MAX_OUTGOING_TRADES
                                && container.settlementObjectManager.foundSettlement);
                super.draw(tickManager, perspective, renderBox);
        }
}