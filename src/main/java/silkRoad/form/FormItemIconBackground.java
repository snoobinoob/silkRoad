package silkRoad.form;

import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

import java.awt.*;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Supplier;

public class FormItemIconBackground extends FormButton implements FormPositionContainer {
    private static final GameTexture BACKGROUND = Settings.UI.inventoryslot_small.active;
    private static final GameTexture BACKGROUND_HOVER = Settings.UI.inventoryslot_small.highlighted;

    private FormPosition position;
    private final Supplier<InventoryItem> itemSupplier;
    private final ListGameTooltips tooltips;

    public FormItemIconBackground(int x, int y, Supplier<InventoryItem> itemSupplier, ListGameTooltips tooltips) {
        this.position = new FormFixedPosition(x, y);
        this.itemSupplier = itemSupplier;
        this.tooltips = tooltips;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        GameTexture background = isHovering() ? BACKGROUND_HOVER : BACKGROUND;

        background.initDraw().color(getDrawColor()).draw(getX(), getY());
        InventoryItem item = itemSupplier.get();
        if (isHovering()) {
            ListGameTooltips allTooltips = new ListGameTooltips(tooltips);
            if (item != null) {
                allTooltips.add(new SpacerGameTooltip(10));
                allTooltips.add(item.getTooltip(perspective, new GameBlackboard()));
            }
            GameTooltipManager.addTooltip(allTooltips, GameBackground.getItemTooltipBackground(),
                    TooltipLocation.FORM_FOCUS);
        }
        if (item == null)
            return;
        item.drawIcon(perspective, getX(), getY() + 2, 32);
        String amountStr;
        if (item.getAmount() > 9999) {
            amountStr = GameUtils.metricNumber(item.getAmount(), 2, true, RoundingMode.FLOOR, null);
        } else {
            amountStr = String.valueOf(item.getAmount());
        }
        int width = FontManager.bit.getWidthCeil(amountStr, Item.tipFontOptions);
        FontManager.bit.drawString((getX() + 30 - width), getY() + 2, amountStr,
                Item.tipFontOptions);
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return singleBox(new Rectangle(getX(), getY(), 32, 32));
    }

    @Override
    public FormPosition getPosition() {
        return position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }
}
