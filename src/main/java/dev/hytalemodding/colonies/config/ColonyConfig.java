package dev.hytalemodding.colonies.config;

/**
 * Step-2 config for zone wand + colony creation command.
 */
public class ColonyConfig {
    private final String wandItemId;
    private final int minSizeXZ;
    private final boolean opOnly;
    private final String permissionNode;

    public ColonyConfig(String wandItemId, int minSizeXZ, boolean opOnly, String permissionNode) {
        this.wandItemId = wandItemId;
        this.minSizeXZ = minSizeXZ;
        this.opOnly = opOnly;
        this.permissionNode = permissionNode;
    }

    public static ColonyConfig defaults() {
        return new ColonyConfig("WOODEN_AXE", 16, true, "colony.admin");
    }

    public String getWandItemId() {
        return wandItemId;
    }

    public int getMinSizeXZ() {
        return minSizeXZ;
    }

    public boolean isOpOnly() {
        return opOnly;
    }

    public String getPermissionNode() {
        return permissionNode;
    }
}
