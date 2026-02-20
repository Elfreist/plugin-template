package dev.hytalemodding.colonies.model;

import java.util.Objects;

/**
 * 2D location for colony zone selection (X/Z only).
 */
public final class Location2D {
    private final String worldId;
    private final int x;
    private final int z;

    public Location2D(String worldId, int x, int z) {
        this.worldId = Objects.requireNonNull(worldId, "worldId");
        this.x = x;
        this.z = z;
    }

    public String getWorldId() {
        return worldId;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + z + ", world=" + worldId + ")";
    }
}
