package dev.hytalemodding.colonies.service;

import dev.hytalemodding.colonies.model.Location2D;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectionManagerTest {

    @Test
    void storesAndClearsSelectionPerPlayer() {
        SelectionManager manager = new SelectionManager();
        UUID playerId = UUID.randomUUID();

        manager.setPosA(playerId, new Location2D("world", 1, 2));
        manager.setPosB(playerId, new Location2D("world", 20, 30));

        SelectionManager.ZoneSelection selection = manager.getSelection(playerId).orElseThrow();
        assertTrue(selection.isComplete());
        assertEquals(1, selection.getA().getX());
        assertEquals(30, selection.getB().getZ());

        manager.clearSelection(playerId);
        assertTrue(manager.getSelection(playerId).isEmpty());
    }
}
