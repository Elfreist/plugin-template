package dev.hytalemodding.colonies.service;

import dev.hytalemodding.colonies.model.Location2D;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SelectionManager {
    private final ConcurrentMap<UUID, ZoneSelection> selections = new ConcurrentHashMap<>();

    public void setPosA(UUID playerId, Location2D loc) {
        selections.compute(playerId, (id, current) -> {
            ZoneSelection next = current == null ? new ZoneSelection() : current;
            next.a = loc;
            return next;
        });
    }

    public void setPosB(UUID playerId, Location2D loc) {
        selections.compute(playerId, (id, current) -> {
            ZoneSelection next = current == null ? new ZoneSelection() : current;
            next.b = loc;
            return next;
        });
    }

    public Optional<ZoneSelection> getSelection(UUID playerId) {
        return Optional.ofNullable(selections.get(playerId));
    }

    public void clearSelection(UUID playerId) {
        selections.remove(playerId);
    }

    public static final class ZoneSelection {
        private Location2D a;
        private Location2D b;

        public Location2D getA() {
            return a;
        }

        public Location2D getB() {
            return b;
        }

        public boolean isComplete() {
            return a != null && b != null;
        }
    }
}
