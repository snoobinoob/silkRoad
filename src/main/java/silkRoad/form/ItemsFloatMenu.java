package silkRoad.form;

import java.util.function.Consumer;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormDebugItemList;
import necesse.gfx.forms.floatMenu.FormFloatMenu;
import necesse.inventory.InventoryItem;

public class ItemsFloatMenu extends FormFloatMenu {
    private static final int WIDTH = 156;
    private static final int HEIGHT = 250;

    public Consumer<InventoryItem> onItemClicked;

    public ItemsFloatMenu(Client client, FormComponent parent) {
        super(parent);
        Form form = new Form(WIDTH, HEIGHT);

        FormTextInput filterInput = form.addComponent(
                new FormTextInput(4, 4, FormInputSize.SIZE_24, form.getWidth() - 8, 50), 1000);
        filterInput.placeHolder = new LocalMessage("ui", "searchtip");
        filterInput.rightClickToClear = true;

        FormDebugItemList list =
                form.addComponent(new FormDebugItemList(0, 4 + filterInput.getBoundingBox().height,
                        WIDTH, HEIGHT - 8 - filterInput.getBoundingBox().height, client) {
                    @Override
                    public void onItemClicked(InventoryItem item, InputEvent event) {
                        if (onItemClicked != null) {
                            onItemClicked.accept(item);
                        }
                        playTickSound();
                        remove();
                    }
                });
        list.populateIfNotAlready();
        filterInput.onChange(e -> list.setFilter(((FormTextInput) e.from).getText()));

        setForm(form);
        filterInput.setTyping(true);
    }
}
