package dev.hytalemodding.colonies.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Birth generation/cohort with aggregated buckets.
 */
public class Group {
    private final int generationId;
    private int ageCycles;
    private final Map<BucketKey, Bucket> buckets;

    public Group(int generationId) {
        this(generationId, 0, new HashMap<>());
    }

    public Group(int generationId, int ageCycles, Map<BucketKey, Bucket> buckets) {
        this.generationId = generationId;
        this.ageCycles = ageCycles;
        this.buckets = new HashMap<>(buckets);
    }

    public int getGenerationId() {
        return generationId;
    }

    public int getAgeCycles() {
        return ageCycles;
    }

    public void setAgeCycles(int ageCycles) {
        if (ageCycles < 0) {
            throw new IllegalArgumentException("ageCycles cannot be negative");
        }
        this.ageCycles = ageCycles;
    }

    public Map<BucketKey, Bucket> getBuckets() {
        return Collections.unmodifiableMap(buckets);
    }

    public int getPopulation() {
        return buckets.values().stream().mapToInt(Bucket::getCount).sum();
    }

    public void addToBucket(BucketKey key, int countDelta) {
        if (countDelta <= 0) {
            throw new IllegalArgumentException("countDelta must be positive");
        }
        Bucket bucket = getOrCreateBucket(key);
        bucket.addCount(countDelta);
    }

    public void removeFromBucket(BucketKey key, int countDelta) {
        if (countDelta <= 0) {
            throw new IllegalArgumentException("countDelta must be positive");
        }
        Bucket bucket = buckets.get(key);
        if (bucket == null) {
            throw new IllegalArgumentException("bucket does not exist for key");
        }
        bucket.addCount(-countDelta);
        if (bucket.getCount() == 0) {
            buckets.remove(key);
        }
    }

    public Bucket getOrCreateBucket(BucketKey key) {
        return buckets.computeIfAbsent(key, ignored -> new Bucket());
    }
}
