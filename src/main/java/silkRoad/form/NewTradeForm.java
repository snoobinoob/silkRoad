package silkRoad.form;

import java.awt.Rectangle;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
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
    private static final int HEIGHT = 198;

    public FormLocalTextButton acceptButton;
    public FormLocalTextButton cancelButton;

    private TradeItemEditComponent exportComponent;
    private TradeItemEditComponent importComponent;

    public NewTradeForm(Client client, TradingPostContainer container) {
        super(WIDTH, HEIGHT);

        addComponent(new FormLocalLabel("ui", "addtrade", new FontOptions(20),
                FormLocalLabel.ALIGN_MID, WIDTH / 2, 4, WIDTH - 8));

        exportComponent = addComponent(new TradeItemEditComponent(0, 35, client));
        addComponent(new ArrowComponent(4, 67, 180));
        importComponent = addComponent(new TradeItemEditComponent(0, 99, client));

        acceptButton = addComponent(new FormLocalTextButton("ui", "create", 4, HEIGHT - 56,
                WIDTH - 8, FormInputSize.SIZE_24, ButtonColor.BASE));
        acceptButton.onClicked(e -> {
            Trade trade = new Trade(exportComponent.item, exportComponent.amount,
                    importComponent.item, importComponent.amount);
            container.addTradeAction.runAndSend(trade);
        });

        cancelButton = addComponent(new FormLocalTextButton("ui", "cancelbutton", 4, HEIGHT - 28,
                WIDTH - 8, FormInputSize.SIZE_24, ButtonColor.BASE));
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        boolean tradeValid = true;
        if (exportComponent.item == null && importComponent.item == null) {
            tradeValid = false;
        } else if ((exportComponent.item != null && exportComponent.amount == 0)
                || (importComponent.item != null && importComponent.amount == 0)) {
            tradeValid = false;
        }
        acceptButton.setActive(tradeValid);
        super.draw(tickManager, perspective, renderBox);
    }
}
