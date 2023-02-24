package silkRoad.form;

import necesse.engine.Settings;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.item.Item;

public class TradeItemEditComponent extends FormContentBox {
    private static final int WIDTH = 156;
    private static final int HEIGHT = 32;

    public int amount;
    public Item item;

    public TradeItemEditComponent(int x, int y, Client client) {
        super(x, y, WIDTH, HEIGHT);

        FormItemIconBackground itemIcon = addComponent(new FormItemIconBackground(4, 0,
                () -> item == null ? null : item.getDefaultItem(client.getPlayer(), amount)));
        FormTextInput amountInput =
                addComponent(new FormTextInput(42, 0, FormInputSize.SIZE_32, WIDTH - 72, 4));
        amountInput.setText(String.valueOf(amount));
        amountInput.onChange(e -> {
            String newValue = ((FormTextInput) e.from).getText();
            try {
                amount = Integer.parseInt("0" + newValue);
            } catch (NumberFormatException exception) {
                ((FormTextInput) e.from).setText(String.valueOf(amount));
            }
        });
        amountInput.rightClickToClear = true;
        addComponent(
                new FormContentIconButton(WIDTH - 24, 6, FormInputSize.SIZE_20, ButtonColor.BASE,
                        Settings.UI.button_escaped_20, new LocalMessage("ui", "clearitem")))
                                .onClicked(e -> {
                                    item = null;
                                    amount = 0;
                                    amountInput.setText("0");
                                });

        itemIcon.onClicked(e -> {
            ItemsFloatMenu menu = new ItemsFloatMenu(client, itemIcon);
            menu.onItemClicked = (i -> {
                if (item == null) {
                    item = i.item;
                } else {
                    item = i.item;
                }
            });
            getManager().openFloatMenu(menu);
        });
    }
}
