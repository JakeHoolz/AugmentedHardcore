package com.backtobedrock.augmentedhardcore.domain.data;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import org.javatuples.Pair;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe container around the player's death bans.
 * Uses a {@link ConcurrentSkipListMap} to support concurrent access and
 * maintain natural ordering of ban identifiers.
 */
public class BanManager {
    private final ConcurrentNavigableMap<Integer, Ban> bans = new ConcurrentSkipListMap<>();
    private final AtomicInteger nextKey = new AtomicInteger();

    public BanManager() {
    }

    public BanManager(NavigableMap<Integer, Ban> existing) {
        if (existing != null) {
            this.bans.putAll(existing);
            this.nextKey.set(this.bans.isEmpty() ? 0 : this.bans.lastKey());
        }
    }

    public NavigableMap<Integer, Ban> getBans() {
        return Collections.unmodifiableNavigableMap(this.bans);
    }

    public int getBanCount() {
        return bans.size();
    }

    public Pair<Integer, Ban> addBan(Ban ban) {
        int key = this.nextKey.incrementAndGet();
        bans.put(key, ban);
        return new Pair<>(key, ban);
    }

    public void clearBans() {
        this.bans.clear();
        this.nextKey.set(0);
    }

    public Ban getLastDeathBan() {
        Map.Entry<Integer, Ban> entry = bans.lastEntry();
        return entry == null ? null : entry.getValue();
    }
}
