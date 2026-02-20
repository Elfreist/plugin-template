package dev.hytalemodding.colonies.persistence;

import dev.hytalemodding.colonies.model.Colony;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ColonyRepository {
    void save(Colony colony);

    Optional<Colony> load(UUID id);

    List<Colony> loadAll();

    void delete(UUID id);
}
