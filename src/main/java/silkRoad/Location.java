package silkRoad;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.objectEntity.ObjectEntity;

import java.awt.*;

public class Location {
    public final LevelIdentifier levelIdentifier;
    public final Point tilePoint;

    public Location(ObjectEntity oe) {
        levelIdentifier = oe.getLevel().getIdentifier();
        tilePoint = new Point(oe.tileX, oe.tileY);
    }

    private Location(LevelIdentifier levelIdentifier, Point tile) {
        this.levelIdentifier = levelIdentifier;
        tilePoint = tile;
    }

    public int getTileX() {
        return tilePoint.x;
    }

    public int getTileY() {
        return tilePoint.y;
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextString(levelIdentifier.stringID);
        writer.putNextInt(tilePoint.x);
        writer.putNextInt(tilePoint.y);
    }

    public static Location readPacket(PacketReader reader) {
        String levelIdentifierString = reader.getNextString();
        int tileX = reader.getNextInt();
        int tileY = reader.getNextInt();
        return new Location(new LevelIdentifier(levelIdentifierString), new Point(tileX, tileY));
    }

    public void addSaveData(SaveData save) {
        save.addSafeString("levelID", levelIdentifier.stringID);
        save.addPoint("tile", tilePoint);
    }

    public static Location fromLoadData(LoadData save) {
        String levelIdentifierString = save.getSafeString("levelID");
        Point tile = save.getPoint("tile");
        return new Location(new LevelIdentifier(levelIdentifierString), tile);
    }

    public int distanceTo(Location other) {
        return Math.abs(tilePoint.x - other.tilePoint.x)
            + Math.abs(tilePoint.y - other.tilePoint.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Location)) {
            return false;
        }
        Location other = (Location) obj;
        return levelIdentifier.equals(other.levelIdentifier) && tilePoint.equals(other.tilePoint);
    }
}
