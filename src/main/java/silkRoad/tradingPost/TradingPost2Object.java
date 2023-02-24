package silkRoad.tradingPost;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;
import silkRoad.SilkRoad;

class TradingPost2Object extends GameObject {
    protected int otherId;

    public GameTexture texture;

    protected TradingPost2Object() {
        super(new Rectangle(32, 32));
        toolType = ToolType.AXE;
        isLightTransparent = true;
        mapColor = new Color(150, 119, 70);
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 0, 1, 2, rotation, false, new int[] {getID(), otherId});
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        texture = GameTexture.fromFile("objects/tradingpost");
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0)
            return new Rectangle(x * 32 + 4, y * 32 + 4, 24, 28);
        if (rotation == 1)
            return new Rectangle(x * 32, y * 32 + 6, 26, 20);
        if (rotation == 2)
            return new Rectangle(x * 32 + 4, y * 32, 24, 26);
        return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 20);
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
            options.add(texture.initDraw().sprite(1, 2, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(1, 3, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 1) {
            options.add(texture.initDraw().sprite(1, 0, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(1, 1, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 2) {
            options.add(texture.initDraw().sprite(0, 4, 32).light(light).pos(drawX, drawY));
        } else {
            options.add(texture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY - 32));
            options.add(texture.initDraw().sprite(0, 1, 32).light(light).pos(drawX, drawY));
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
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServerLevel()) {
            LevelObject masterObject = getMultiTile(level.getObjectRotation(x, y))
                    .getMasterLevelObject(level, x, y).get();
            TradingPostContainer.openAndSendContainer(SilkRoad.TRADING_POST_CONTAINER,
                    player.getServerClient(), level, masterObject.tileX, masterObject.tileY);
        }
    }
}
