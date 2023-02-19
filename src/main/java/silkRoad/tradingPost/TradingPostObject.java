package silkRoad.tradingPost;

import java.awt.Rectangle;
import java.util.List;
import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.SpelunkerUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.interfaces.OpenSound;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import silkRoad.SilkRoad;

public class TradingPostObject extends GameObject implements OpenSound {
    public GameTexture texture;
    private GameRandom random;

    public TradingPostObject() {
        super(new Rectangle(4, 4, 24, 24));
        toolType = ToolType.AXE;
        isLightTransparent = true;
        mapColor = new Color(150, 119, 70);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        texture = GameTexture.fromFile("objects/tradingpost");
    }

    @Override
    public void playOpenSound(Level level, int tileX, int tileY) {
        Screen.playSound(GameResources.chestopen,
                (SoundEffect) SoundEffect.effect((tileX * 32 + 16), (tileY * 32 + 16)));
    }

    @Override
    public void playCloseSound(Level level, int tileX, int tileY) {
        Screen.playSound(GameResources.chestclose,
                (SoundEffect) SoundEffect.effect((tileX * 32 + 16), (tileY * 32 + 16)));
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new TradingPostObjectEntity(level, x, y);
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList,
            Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera,
            PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int rotation = level.getObjectRotation(tileX, tileY) % texture.getWidth() / 32;
        boolean treasureHunter = (perspective != null
                && ((Boolean) perspective.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER))
                        .booleanValue());
        GameTexture usedTexture = texture;
        final SharedTextureDrawOptions draws = new SharedTextureDrawOptions(usedTexture);
        SharedTextureDrawOptions.Wrapper draw =
                draws.addSprite(rotation, 0, 32, texture.getHeight());
        SpelunkerUtils.applySpelunkerEffect(draw, treasureHunter, random, getID(), level, light,
                2500L, 0.2F).pos(drawX, drawY - texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY) {
            public int getSortY() {
                return 16;
            }

            public void draw(TickManager tickManager) {
                draws.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha,
            PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        rotation %= texture.getWidth() / 32;
        this.texture.initDraw().sprite(rotation, 0, 32, this.texture.getHeight()).alpha(alpha)
                .draw(drawX, drawY - texture.getHeight() + 32);
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
            OEInventoryContainer.openAndSendContainer(SilkRoad.TRADING_POST_CONTAINER,
                    player.getServerClient(), level, x, y);
    }
}
