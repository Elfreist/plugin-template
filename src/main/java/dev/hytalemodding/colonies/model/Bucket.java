package dev.hytalemodding.colonies.model;

import dev.hytalemodding.colonies.model.enums.XpType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Aggregated bucket of identical individuals.
 */
public class Bucket {
    private int count;
    private final EnumMap<XpType, Integer> xpPools;

    public Bucket() {
        this(0);
    }

    public Bucket(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative");
        }
        this.count = count;
        this.xpPools = new EnumMap<>(XpType.class);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative");
        }
        this.count = count;
    }

    public void addCount(int delta) {
        int next = this.count + delta;
        if (next < 0) {
            throw new IllegalArgumentException("bucket count cannot go negative");
        }
        this.count = next;
    }

    public void addXp(XpType type, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        xpPools.merge(type, amount, Integer::sum);
    }

    public int getXp(XpType type) {
        return xpPools.getOrDefault(type, 0);
    }

    public Map<XpType, Integer> getXpPoolsView() {
        return Collections.unmodifiableMap(xpPools);
    }
}
