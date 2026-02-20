package dev.hytalemodding.colonies.model;

import java.util.Objects;

/**
 * Immutable key that identifies an aggregated bucket profile.
 */
public final class BucketKey {
    private final String roleId;
    private final int stageId;
    private final String evolutionId;

    public BucketKey(String roleId, int stageId, String evolutionId) {
        this.roleId = Objects.requireNonNull(roleId, "roleId");
        this.stageId = stageId;
        this.evolutionId = Objects.requireNonNull(evolutionId, "evolutionId");
    }

    public String getRoleId() {
        return roleId;
    }

    public int getStageId() {
        return stageId;
    }

    public String getEvolutionId() {
        return evolutionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BucketKey bucketKey)) {
            return false;
        }
        return stageId == bucketKey.stageId
                && roleId.equals(bucketKey.roleId)
                && evolutionId.equals(bucketKey.evolutionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, stageId, evolutionId);
    }
}
