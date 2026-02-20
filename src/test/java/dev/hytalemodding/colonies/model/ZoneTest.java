package dev.hytalemodding.colonies.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZoneTest {

    @Test
    void computesBoundsAndCenterFromRawPoints() {
        Zone zone = Zone.from("world", 50, -10, 10, 30);

        assertEquals("world", zone.getWorldId());
        assertEquals(50, zone.getAx());
        assertEquals(-10, zone.getAz());
        assertEquals(10, zone.getBx());
        assertEquals(30, zone.getBz());
        assertEquals(10, zone.getMinX());
        assertEquals(50, zone.getMaxX());
        assertEquals(-10, zone.getMinZ());
        assertEquals(30, zone.getMaxZ());
        assertEquals(30, zone.getCenterX());
        assertEquals(10, zone.getCenterZ());
    }
}
