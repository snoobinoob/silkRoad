package silkRoad.form;

import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import silkRoad.tradingPost.TradingPostContainer;

public class TradingPostContainerForm extends ContainerFormList<TradingPostContainer> {
    private final OEInventoryContainerForm<TradingPostContainer> invFormSwitcher;
    private final InTradesForm inTradesForm;
    private final OutTradesFormSwitcher outTradesForm;
    private final int uniqueSeed;

    public TradingPostContainerForm(Client client, TradingPostContainer container, int uniqueSeed) {
        super(client, container);
        invFormSwitcher = addComponent(new OEInventoryContainerForm<>(client, container));
        inTradesForm = addComponent(new InTradesForm(client, container));
        outTradesForm = addComponent(new OutTradesFormSwitcher(client, container));
        this.uniqueSeed = uniqueSeed;

        container.objectEntity.trades.onChanged(uniqueSeed, () -> {
            inTradesForm.update();
            outTradesForm.update();
        });
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);

        inTradesForm.setPosition(new FormRelativePosition(invFormSwitcher.inventoryForm,
                () -> -inTradesForm.getWidth() - Settings.UI.formSpacing, () -> 0));
        outTradesForm.setPosition(new FormRelativePosition(invFormSwitcher.inventoryForm,
                () -> invFormSwitcher.inventoryForm.getWidth() + Settings.UI.formSpacing, () -> 0));
    }

    @Override
    public void dispose() {
        super.dispose();
        container.objectEntity.trades.removeListener(uniqueSeed);
    }
}
