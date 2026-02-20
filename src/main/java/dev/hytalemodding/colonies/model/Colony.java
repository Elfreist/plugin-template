package dev.hytalemodding.colonies.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Aggregated colony model optimized for off-zone simulation in later versions.
 */
public class Colony {
    private final UUID id;
    private final Zone zone;
    private final String familyId;
    private int populationTotal;
    private final List<Group> groups;
    private final long createdAtEpochMs;

    public Colony(UUID id, Zone zone, String familyId, long createdAtEpochMs) {
        this(id, zone, familyId, 0, new ArrayList<>(), createdAtEpochMs);
    }

    public Colony(UUID id,
                  Zone zone,
                  String familyId,
                  int populationTotal,
                  List<Group> groups,
                  long createdAtEpochMs) {
        this.id = Objects.requireNonNull(id, "id");
        this.zone = Objects.requireNonNull(zone, "zone");
        this.familyId = Objects.requireNonNull(familyId, "familyId");
        this.populationTotal = populationTotal;
        this.groups = new ArrayList<>(groups);
        this.createdAtEpochMs = createdAtEpochMs;
    }

    public UUID getId() {
        return id;
    }

    public Zone getZone() {
        return zone;
    }

    public String getFamilyId() {
        return familyId;
    }

    public int getPopulationTotal() {
        return populationTotal;
    }

    public List<Group> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    public long getCreatedAtEpochMs() {
        return createdAtEpochMs;
    }

    public void addGroup(Group group) {
        this.groups.add(Objects.requireNonNull(group, "group"));
    }

    public void applyPopulationDelta(int delta) {
        int nextPopulation = populationTotal + delta;
        if (nextPopulation < 0) {
            throw new IllegalArgumentException("populationTotal cannot be negative");
        }
        this.populationTotal = nextPopulation;
    }

    public int computePopulationTotal() {
        return groups.stream().mapToInt(Group::getPopulation).sum();
    }

    public Optional<Group> getGroup(int generationId) {
        return groups.stream().filter(g -> g.getGenerationId() == generationId).findFirst();
    }

    public boolean isPopulationConsistent() {
        return populationTotal == computePopulationTotal();
    }
}
