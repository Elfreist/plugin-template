package dev.hytalemodding.colonies.model;

import java.util.Objects;

/**
 * Minimal immutable 3D position for colony data in V1.
 */
public final class Location {
    private final String worldId;
    private final double x;
    private final double y;
    private final double z;

    public Location(String worldId, double x, double y, double z) {
        this.worldId = Objects.requireNonNull(worldId, "worldId");
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorldId() {
        return worldId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
