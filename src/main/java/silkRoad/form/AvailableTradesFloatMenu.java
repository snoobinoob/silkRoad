package silkRoad.form;

import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import silkRoad.SilkRoad;
import silkRoad.TradeRegistry;
import silkRoad.tradingPost.TradingPostContainer;

public class AvailableTradesFloatMenu extends FormFloatMenu {
    private static final int WIDTH = 156;
    private static final int HEIGHT = 250;

    private TradingPostContainer container;

    public AvailableTradesFloatMenu(InTradesForm parent, TradingPostContainer container) {
        super(parent);
        this.container = container;

        Form form = new Form(WIDTH, HEIGHT);

        FormTextInput filterInput = form.addComponent(
                new FormTextInput(4, 4, FormInputSize.SIZE_24, form.getWidth() - 8, 50), 1000);
        filterInput.placeHolder = new LocalMessage("ui", "searchtip");
        filterInput.rightClickToClear = true;

        TradeComponentList list = form.addComponent(
                new TradeComponentList(TradeRegistry.getAvailableTrades(container.objectEntity), 4,
                        32, WIDTH - 8, HEIGHT - 36, TradeComponent.Type.INCOMING, tradeId -> {
                            container.subscribeAction.runAndSend(tradeId);
                            remove();
                        }, SilkRoad.addButtonIcon, new LocalMessage("ui", "subscribetrade")));

        container.objectEntity.trades.onChanged(hashCode(),
                () -> list.updateTradeComponents(filterInput.getText()));
        filterInput.onChange(e -> list.updateTradeComponents(filterInput.getText()));

        setForm(form);
    }

    @Override
    public void dispose() {
        super.dispose();
        container.objectEntity.trades.removeListener(hashCode());
    }
}
