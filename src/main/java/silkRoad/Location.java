package silkRoad;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.objectEntity.ObjectEntity;

import java.awt.*;

public class Location {
    private final Point islandPoint;
    private final Point tilePoint;

    public Location(ObjectEntity oe) {
        this.islandPoint = new Point(oe.getLevel().getIslandX(), oe.getLevel().getIslandY());
        this.tilePoint = new Point(oe.getTileX(), oe.getTileY());
    }

    private Location(Point island, Point tile) {
        islandPoint = island;
        tilePoint = tile;
    }

    public Point getIslandPoint() {
        return islandPoint;
    }

    public int getTileX() {
        return tilePoint.x;
    }

    public int getTileY() {
        return tilePoint.y;
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextInt(islandPoint.x);
        writer.putNextInt(islandPoint.y);
        writer.putNextInt(tilePoint.x);
        writer.putNextInt(tilePoint.y);
    }

    public static Location readPacket(PacketReader reader) {
        int islandX = reader.getNextInt();
        int islandY = reader.getNextInt();
        int tileX = reader.getNextInt();
        int tileY = reader.getNextInt();
        return new Location(new Point(islandX, islandY), new Point(tileX, tileY));
    }

    public void addSaveData(SaveData save) {
        save.addPoint("island", islandPoint);
        save.addPoint("tile", tilePoint);
    }

    public static Location fromLoadData(LoadData save) {
        Point island = save.getPoint("island");
        Point tile = save.getPoint("tile");
        return new Location(island, tile);
    }

    public int distanceTo(Location other) {
        return Math.abs(islandPoint.x - other.islandPoint.x)
                + Math.abs(islandPoint.y - other.islandPoint.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Location)) {
            return false;
        }
        Location other = (Location) obj;
        return islandPoint.equals(other.islandPoint) && tilePoint.equals(other.tilePoint);
    }
}
