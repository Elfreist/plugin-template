package dev.hytalemodding.colonies.service;

import dev.hytalemodding.colonies.model.BucketKey;
import dev.hytalemodding.colonies.model.Colony;
import dev.hytalemodding.colonies.model.Group;
import dev.hytalemodding.colonies.model.Zone;
import dev.hytalemodding.colonies.persistence.ColonyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory colony registry with persistence bridge.
 *
 * <p>Prepared for future SimulationEngine/SpawnManager integration.
 */
public class ColonyRegistry {
    private final Map<UUID, Colony> cache = new ConcurrentHashMap<>();
    private final ColonyRepository repository;

    public ColonyRegistry(ColonyRepository repository) {
        this.repository = repository;
        for (Colony colony : repository.loadAll()) {
            cache.put(colony.getId(), colony);
        }
    }

    public Colony createColony(Zone zone, String familyId) {
        Colony colony = new Colony(UUID.randomUUID(), zone, familyId, System.currentTimeMillis());

        Group generationOne = new Group(1);
        generationOne.addToBucket(new BucketKey("worker", 1, "base"), 10);
        colony.addGroup(generationOne);
        colony.applyPopulationDelta(generationOne.getPopulation());

        cache.put(colony.getId(), colony);
        repository.save(colony);
        return colony;
    }

    public Optional<Colony> get(UUID id) {
        return Optional.ofNullable(cache.get(id));
    }

    public List<Colony> list() {
        return new ArrayList<>(cache.values());
    }

    public void saveAll() {
        cache.values().forEach(repository::save);
    }
}
