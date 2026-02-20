package dev.hytalemodding.colonies.service;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectionManagerTest {

    @Test
    void storesAndClearsSelectionPerPlayer() {
        SelectionManager manager = new SelectionManager();
        UUID playerId = UUID.randomUUID();

        manager.setA(playerId, "world", 1, 2);
        manager.setB(playerId, "world", 20, 30);

        SelectionManager.Selection selection = manager.get(playerId).orElseThrow();
        assertTrue(selection.isComplete());
        assertEquals(1, selection.getAx());
        assertEquals(30, selection.getBz());

        manager.clear(playerId);
        assertTrue(manager.get(playerId).isEmpty());
    }

    @Test
    void refusesPosBInDifferentWorld() {
        SelectionManager manager = new SelectionManager();
        UUID playerId = UUID.randomUUID();

        manager.setA(playerId, "world_a", 0, 0);
        assertThrows(IllegalArgumentException.class, () -> manager.setB(playerId, "world_b", 1, 1));
    }
}
