package silkRoad.tradingPost;

import java.awt.Rectangle;
import java.util.List;
import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;
import silkRoad.SilkRoad;

public class TradingPostObject extends GameObject {
    public GameTexture texture;

    protected int otherId;

    public TradingPostObject() {
        super(new Rectangle(4, 4, 24, 24));
        toolType = ToolType.AXE;
        isLightTransparent = true;
        mapColor = new Color(150, 119, 70);
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 1, 1, 2, rotation, true, new int[] {otherId, getID()});
    }

    @Override
    public int getPlaceRotation(Level level, int levelX, int levelY, PlayerMob player,
            int playerDir) {
        return Math.floorMod(super.getPlaceRotation(level, levelX, levelY, player, playerDir) - 1,
                4);
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0)
            return new Rectangle(x * 32 + 4, y * 32, 24, 26);
        if (rotation == 1)
            return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 20);
        if (rotation == 2)
            return new Rectangle(x * 32 + 4, y * 32 + 4, 24, 28);
        return new Rectangle(x * 32, y * 32 + 6, 26, 20);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        texture = GameTexture.fromFile("objects/tradingpost");
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new TradingPostObjectEntity(level, x, y);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList,
            Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera,
            PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int rotation = level.getObjectRotation(tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            options.add(texture.initDraw().sprite(1, 4, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 1) {
            options.add(texture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(0, 1, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 2) {
            options.add(texture.initDraw().sprite(0, 2, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(0, 3, 32).light(light).pos(drawX, drawY));
        } else {
            options.add(texture.initDraw().sprite(1, 0, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(1, 1, 32).light(light).pos(drawX, drawY));
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha,
            PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        if (rotation == 0) { // Right
            texture.initDraw().sprite(1, 2, 32).alpha(alpha).draw(drawX, drawY - 64);
            texture.initDraw().sprite(1, 3, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(1, 4, 32).alpha(alpha).draw(drawX, drawY);
        } else if (rotation == 1) { // Down
            texture.initDraw().sprite(0, 0, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(0, 1, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(1, 0, 32).alpha(alpha).draw(drawX + 32, drawY - 32);
            texture.initDraw().sprite(1, 1, 32).alpha(alpha).draw(drawX + 32, drawY);
        } else if (rotation == 2) { // Left
            texture.initDraw().sprite(0, 2, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(0, 3, 32).alpha(alpha).draw(drawX, drawY);
            texture.initDraw().sprite(0, 4, 32).alpha(alpha).draw(drawX, drawY + 32);
        } else { // Up
            texture.initDraw().sprite(0, 0, 32).alpha(alpha).draw(drawX - 32, drawY - 32);
            texture.initDraw().sprite(0, 1, 32).alpha(alpha).draw(drawX - 32, drawY);
            texture.initDraw().sprite(1, 0, 32).alpha(alpha).draw(drawX, drawY - 32);
            texture.initDraw().sprite(1, 1, 32).alpha(alpha).draw(drawX, drawY);
        }
    }

    public static int[] registerTradingPost() {
        TradingPostObject o1 = new TradingPostObject();
        TradingPost2Object o2 = new TradingPost2Object();
        o2.otherId = ObjectRegistry.registerObject("tradingpost", o1, 140.0F, true);
        o1.otherId = ObjectRegistry.registerObject("tradingpost2", o2, 0.0F, false);
        return new int[] {o2.otherId, o1.otherId};
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServerLevel())
            TradingPostContainer.openAndSendContainer(SilkRoad.TRADING_POST_CONTAINER,
                    player.getServerClient(), level, x, y);
    }
}
