package dev.hytalemodding.colonies.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SelectionManager {
    private final ConcurrentMap<UUID, Selection> selections = new ConcurrentHashMap<>();

    public void setA(UUID playerId, String worldId, int x, int z) {
        selections.compute(playerId, (id, current) -> {
            Selection next = current == null ? new Selection() : current;
            next.worldId = worldId;
            next.ax = x;
            next.az = z;
            return next;
        });
    }

    public void setB(UUID playerId, String worldId, int x, int z) {
        selections.compute(playerId, (id, current) -> {
            Selection next = current == null ? new Selection() : current;
            if (next.worldId != null && !next.worldId.equals(worldId)) {
                throw new IllegalArgumentException("PosA et PosB doivent être dans le même monde.");
            }
            next.worldId = worldId;
            next.bx = x;
            next.bz = z;
            return next;
        });
    }

    public Optional<Selection> get(UUID playerId) {
        return Optional.ofNullable(selections.get(playerId));
    }

    public void clear(UUID playerId) {
        selections.remove(playerId);
    }

    public static final class Selection {
        private String worldId;
        private Integer ax;
        private Integer az;
        private Integer bx;
        private Integer bz;

        public String getWorldId() { return worldId; }
        public Integer getAx() { return ax; }
        public Integer getAz() { return az; }
        public Integer getBx() { return bx; }
        public Integer getBz() { return bz; }

        public boolean isComplete() {
            return worldId != null && ax != null && az != null && bx != null && bz != null;
        }
    }
}
