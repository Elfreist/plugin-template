package dev.hytalemodding.colonies.model;

import java.util.Objects;

/**
 * 2D zone (X/Z only) with precomputed bounds and center.
 */
public final class Zone {
    private final String worldId;
    private final int ax;
    private final int az;
    private final int bx;
    private final int bz;
    private final int minX;
    private final int maxX;
    private final int minZ;
    private final int maxZ;
    private final int centerX;
    private final int centerZ;

    private Zone(String worldId, int ax, int az, int bx, int bz,
                 int minX, int maxX, int minZ, int maxZ,
                 int centerX, int centerZ) {
        this.worldId = Objects.requireNonNull(worldId, "worldId");
        this.ax = ax;
        this.az = az;
        this.bx = bx;
        this.bz = bz;
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.centerX = centerX;
        this.centerZ = centerZ;
    }

    public static Zone from(String worldId, int ax, int az, int bx, int bz) {
        int minX = Math.min(ax, bx);
        int maxX = Math.max(ax, bx);
        int minZ = Math.min(az, bz);
        int maxZ = Math.max(az, bz);
        int centerX = (minX + maxX) / 2;
        int centerZ = (minZ + maxZ) / 2;
        return new Zone(worldId, ax, az, bx, bz, minX, maxX, minZ, maxZ, centerX, centerZ);
    }

    public String getWorldId() { return worldId; }
    public int getAx() { return ax; }
    public int getAz() { return az; }
    public int getBx() { return bx; }
    public int getBz() { return bz; }
    public int getMinX() { return minX; }
    public int getMaxX() { return maxX; }
    public int getMinZ() { return minZ; }
    public int getMaxZ() { return maxZ; }
    public int getCenterX() { return centerX; }
    public int getCenterZ() { return centerZ; }
}
