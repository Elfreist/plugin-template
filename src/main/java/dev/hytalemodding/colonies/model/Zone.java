package dev.hytalemodding.colonies.model;

import java.util.Objects;

/**
 * Colony zone boundaries and precomputed center.
 */
public final class Zone {
    private final Location pos1;
    private final Location pos2;
    private final Location center;

    public Zone(Location pos1, Location pos2, Location center) {
        this.pos1 = Objects.requireNonNull(pos1, "pos1");
        this.pos2 = Objects.requireNonNull(pos2, "pos2");
        this.center = Objects.requireNonNull(center, "center");
    }

    public static Zone from(Location a, Location b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        if (!a.getWorldId().equals(b.getWorldId())) {
            throw new IllegalArgumentException("Zone corners must be in the same world");
        }

        double minX = Math.min(a.getX(), b.getX());
        double minY = Math.min(a.getY(), b.getY());
        double minZ = Math.min(a.getZ(), b.getZ());
        double maxX = Math.max(a.getX(), b.getX());
        double maxY = Math.max(a.getY(), b.getY());
        double maxZ = Math.max(a.getZ(), b.getZ());

        Location pos1 = new Location(a.getWorldId(), minX, minY, minZ);
        Location pos2 = new Location(a.getWorldId(), maxX, maxY, maxZ);
        Location center = new Location(
                a.getWorldId(),
                (minX + maxX) / 2.0,
                (minY + maxY) / 2.0,
                (minZ + maxZ) / 2.0
        );

        return new Zone(pos1, pos2, center);
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public Location getCenter() {
        return center;
    }
}
