package silkRoad.form;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.position.FormPosition;
import silkRoad.tradingPost.TradingPostContainer;

public class OutTradesFormSwitcher extends FormSwitcher {
    private OutTradesForm outTradesForm;
    private NewTradeForm newTradeForm;

    public OutTradesFormSwitcher(Client client, TradingPostContainer container) {
        outTradesForm = addComponent(new OutTradesForm(client, container));
        newTradeForm = addComponent(new NewTradeForm(client, container));
        makeCurrent(outTradesForm);

        outTradesForm.addTradeButton.onClicked(e -> makeCurrent(newTradeForm));
        outTradesForm.addTradeButton.setCooldown(500);

        // newTradeForm.cancelButton.onClicked(e -> makeCurrent(outTradesForm));
        newTradeForm.acceptButton.onClicked(e -> makeCurrent(outTradesForm));
    }

    public void setPosition(FormPosition position) {
        outTradesForm.setPosition(position);
        newTradeForm.setPosition(position);
    }
}
