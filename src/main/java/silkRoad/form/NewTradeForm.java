package silkRoad.form;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import silkRoad.Trade;
import silkRoad.tradingPost.TradingPostContainer;

public class NewTradeForm extends Form {
    private static final int WIDTH = 156;
    private static final int HEIGHT = 100;

    private Trade trade;
    public FormLocalTextButton acceptButton;

    public NewTradeForm(Client client, TradingPostContainer container) {
        super(WIDTH, HEIGHT);
        trade = new Trade();

        addComponent(new FormLocalLabel("ui", "addtrade", new FontOptions(20),
                FormLocalLabel.ALIGN_LEFT, 4, 4, WIDTH - 8));

        TradeComponent tradeComponent = addComponent(new TradeComponent(4, 36, trade));
        tradeComponent.exportComponent.onClicked(e -> {
            getManager().openFloatMenu(
                    new ItemsFloatMenu(client, this, NewTradeForm.this.trade.exportItem, item -> {
                        NewTradeForm.this.trade.exportItem = item;
                    }));
        });
        tradeComponent.importComponent.onClicked(e -> {
            getManager().openFloatMenu(
                    new ItemsFloatMenu(client, this, NewTradeForm.this.trade.importItem, item -> {
                        NewTradeForm.this.trade.importItem = item;
                    }));
        });

        acceptButton = addComponent(new FormLocalTextButton("ui", "acceptbutton", 4, HEIGHT - 32,
                WIDTH - 8, FormInputSize.SIZE_20, ButtonColor.BASE));
        acceptButton.onClicked(e -> container.addTradeAction.runAndSend(NewTradeForm.this.trade));
    }

}
