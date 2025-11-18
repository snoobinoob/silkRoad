package silkRoad.form;

import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import silkRoad.SilkRoad;
import silkRoad.tradingPost.TradingPostContainer;

import java.awt.*;

public class OutTradesForm extends Form {
    public TradingPostContainer container;
    public FormLocalTextButton addTradeButton;
    public TradeComponentList list;

    public OutTradesForm(TradingPostContainer container) {
        super(156, 198);
        this.container = container;

        addTradeButton = addComponent(new FormLocalTextButton(
            "ui",
            "newtrade",
            4,
            4,
            getWidth() - 8,
            FormInputSize.SIZE_24,
            ButtonColor.BASE
        ));
        list = addComponent(new TradeComponentList(
            container.objectEntity.trades.outgoingTrades,
            4,
            32,
            getWidth() - 8,
            getHeight() - 36,
            TradeComponent.Type.OUTGOING,
            tradeId -> container.removeTradeAction.runAndSend(tradeId),
            Settings.UI.button_escaped_20,
            container::canConfigureTrades,
            new LocalMessage("ui", "deletetrade")
        ));
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        addTradeButton.setActive(container.canAddOutgoing());
        if (addTradeButton.isHovering()) {
            GameTooltipManager.addTooltip(
                container.getSettlementAccessTooltip(
                    container.canAddOutgoing() ? null : SilkRoad.noSpaceTooltip
                ),
                TooltipLocation.FORM_FOCUS
            );
        }
        super.draw(tickManager, perspective, renderBox);

    }

    public void update() {
        list.updateTradeComponents();
    }
}
