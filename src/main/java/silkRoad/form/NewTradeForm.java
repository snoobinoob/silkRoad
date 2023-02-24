package silkRoad.form;

import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import silkRoad.SilkRoad;
import silkRoad.Trade;
import silkRoad.tradingPost.TradingPostContainer;

public class NewTradeForm extends Form {
    private static final int WIDTH = 156;
    private static final int HEIGHT = 198;

    public FormLocalTextButton acceptButton;
    public FormLocalTextButton cancelButton;

    private TradingPostContainer container;
    private TradeItemEditComponent exportComponent;
    private TradeItemEditComponent importComponent;

    public NewTradeForm(Client client, TradingPostContainer container) {
        super(WIDTH, HEIGHT);
        this.container = container;

        addComponent(new FormLocalLabel("ui", "addtrade", new FontOptions(20),
                FormLocalLabel.ALIGN_MID, WIDTH / 2, 4, WIDTH - 8));

        exportComponent =
                addComponent(new TradeItemEditComponent(0, 35, client, SilkRoad.exportTooltips));
        addComponent(new ArrowComponent(4, 67, 180));
        importComponent =
                addComponent(new TradeItemEditComponent(0, 99, client, SilkRoad.importTooltips));

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
        StringTooltips tooltips = new StringTooltips();
        if (!container.canAddOutgoing()) {
            tooltips.add(Localization.translate("ui", "notradespace"));
        }
        if (exportComponent.item == null && importComponent.item == null) {
            tooltips.add(Localization.translate("ui", "emptytrade"));
        }
        if (exportComponent.item != null && exportComponent.amount == 0) {
            tooltips.add(Localization.translate("ui", "exportzero"));
        }
        if (importComponent.item != null && importComponent.amount == 0) {
            tooltips.add(Localization.translate("ui", "importzero"));
        }
        acceptButton.setActive(tooltips.getSize() == 0);
        if (acceptButton.isHovering() && tooltips.getSize() > 0) {
            Screen.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
        super.draw(tickManager, perspective, renderBox);
    }
}
