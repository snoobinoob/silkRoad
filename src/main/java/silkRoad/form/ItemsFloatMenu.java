package silkRoad.form;

import java.util.function.Consumer;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormDebugItemList;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;

public class ItemsFloatMenu extends FormFloatMenu {
    private static final int WIDTH = 156;
    private static final int HEIGHT = 250;

    private InventoryItem item;

    public ItemsFloatMenu(Client client, FormComponent parent, InventoryItem item,
            Consumer<InventoryItem> selectionHandler) {
        super(parent);
        this.item = item.copy();
        Form form = new Form(WIDTH, HEIGHT);

        FormTextInput filterInput = form.addComponent(
                new FormTextInput(4, 4, FormInputSize.SIZE_24, form.getWidth() - 8, 50), 1000);
        filterInput.placeHolder = new LocalMessage("ui", "searchtip");
        filterInput.rightClickToClear = true;

        FormDebugItemList list = form.addComponent(new FormDebugItemList(0,
                4 + filterInput.getBoundingBox().height, WIDTH, 170, client) {
            @Override
            public void onItemClicked(InventoryItem item, InputEvent event) {
                ItemsFloatMenu.this.item = item.copy(ItemsFloatMenu.this.item.getAmount());
                playTickSound();
            }
        });
        list.populateIfNotAlready();
        filterInput.onChange(e -> list.setFilter(((FormTextInput) e.from).getText()));

        FormLocalLabel amountLabel = form.addComponent(new FormLocalLabel("ui", "amount",
                new FontOptions(20), FormLabel.ALIGN_LEFT, 4, HEIGHT - 56));

        int labelWidth = amountLabel.getBoundingBox().width;
        FormTextInput amountInput = form.addComponent(new FormTextInput(8 + labelWidth, HEIGHT - 56,
                FormInputSize.SIZE_24, WIDTH - 12 - labelWidth, 4));
        amountInput.setText(String.valueOf(item.getAmount()));
        amountInput.onChange(e -> {
            String newValue = ((FormTextInput) e.from).getText();
            try {
                ItemsFloatMenu.this.item.setAmount(Integer.parseInt("0" + newValue));
            } catch (NumberFormatException exception) {
                ((FormTextInput) e.from)
                        .setText(String.valueOf(ItemsFloatMenu.this.item.getAmount()));
            }
        });
        amountInput.rightClickToClear = true;

        FormLocalTextButton acceptButton =
                form.addComponent(new FormLocalTextButton("ui", "acceptbutton", 4, HEIGHT - 28,
                        WIDTH - 8, FormInputSize.SIZE_24, ButtonColor.BASE));
        acceptButton.onClicked(e -> {
            selectionHandler.accept(ItemsFloatMenu.this.item);
            remove();
        });

        setForm(form);
    }
}
