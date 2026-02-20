package dev.hytalemodding.colonies.model;

import java.util.Objects;

/**
 * Colony zone boundaries and precomputed 2D center (X/Z).
 */
public final class Zone {
    private final Location2D pos1;
    private final Location2D pos2;
    private final Location2D center;

    public Zone(Location2D pos1, Location2D pos2, Location2D center) {
        this.pos1 = Objects.requireNonNull(pos1, "pos1");
        this.pos2 = Objects.requireNonNull(pos2, "pos2");
        this.center = Objects.requireNonNull(center, "center");
    }

    public static Zone from(Location2D a, Location2D b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        if (!a.getWorldId().equals(b.getWorldId())) {
            throw new IllegalArgumentException("Zone corners must be in the same world");
        }

        int minX = Math.min(a.getX(), b.getX());
        int minZ = Math.min(a.getZ(), b.getZ());
        int maxX = Math.max(a.getX(), b.getX());
        int maxZ = Math.max(a.getZ(), b.getZ());

        Location2D pos1 = new Location2D(a.getWorldId(), minX, minZ);
        Location2D pos2 = new Location2D(a.getWorldId(), maxX, maxZ);
        Location2D center = new Location2D(
                a.getWorldId(),
                (minX + maxX) / 2,
                (minZ + maxZ) / 2
        );

        return new Zone(pos1, pos2, center);
    }

    public int getSizeX() {
        return Math.abs(pos2.getX() - pos1.getX());
    }

    public int getSizeZ() {
        return Math.abs(pos2.getZ() - pos1.getZ());
    }

    public Location2D getPos1() {
        return pos1;
    }

    public Location2D getPos2() {
        return pos2;
    }

    public Location2D getCenter() {
        return center;
    }
}
