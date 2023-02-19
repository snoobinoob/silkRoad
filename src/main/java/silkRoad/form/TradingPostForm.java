package silkRoad.form;

import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import silkRoad.tradingPost.TradingPostContainer;

public class TradingPostForm extends ContainerFormList<TradingPostContainer> {
    private OEInventoryContainerForm<TradingPostContainer> containerForm;
    private InTradesForm inTradesForm;
    private OutTradesFormSwitcher outTradesForm;

    public TradingPostForm(Client client, TradingPostContainer container) {
        super(client, container);
        containerForm = addComponent(new OEInventoryContainerForm<>(client, container));
        inTradesForm = addComponent(new InTradesForm(client, container));
        outTradesForm = addComponent(new OutTradesFormSwitcher(client, container));
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }

    @Override
    public void onWindowResized() {
        super.onWindowResized();

        inTradesForm.setPosition(new FormRelativePosition(containerForm.inventoryForm,
                () -> -inTradesForm.getWidth() - Settings.UI.formSpacing, () -> 0));
        outTradesForm.setPosition(new FormRelativePosition(containerForm.inventoryForm,
                () -> containerForm.inventoryForm.getWidth() + Settings.UI.formSpacing, () -> 0));
    }
}
