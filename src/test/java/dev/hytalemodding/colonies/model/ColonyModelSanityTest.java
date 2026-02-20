package dev.hytalemodding.colonies.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ColonyModelSanityTest {

    @Test
    void populationCacheStaysConsistentWithBucketSum() {
        Colony colony = new Colony(
                UUID.randomUUID(),
                Zone.from("world", 0, 0, 10, 10),
                "family.base",
                System.currentTimeMillis()
        );

        Group group = new Group(1);
        BucketKey key = new BucketKey("worker", 1, "base");

        group.addToBucket(key, 10);
        colony.addGroup(group);
        colony.applyPopulationDelta(10);

        group.addToBucket(key, 5);
        colony.applyPopulationDelta(5);

        group.removeFromBucket(key, 3);
        colony.applyPopulationDelta(-3);

        assertEquals(colony.computePopulationTotal(), colony.getPopulationTotal());
        assertTrue(colony.isPopulationConsistent());
    }
}
