package dev.hytalemodding.colonies.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZoneTest {

    @Test
    void normalizesCornersAndComputesCenterIn2D() {
        Zone zone = Zone.from(new Location2D("world", 50, -10), new Location2D("world", 10, 30));

        assertEquals(10, zone.getPos1().getX());
        assertEquals(-10, zone.getPos1().getZ());
        assertEquals(50, zone.getPos2().getX());
        assertEquals(30, zone.getPos2().getZ());
        assertEquals(30, zone.getCenter().getX());
        assertEquals(10, zone.getCenter().getZ());
    }
}
