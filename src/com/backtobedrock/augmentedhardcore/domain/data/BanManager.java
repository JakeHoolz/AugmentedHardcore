package com.backtobedrock.augmentedhardcore.domain.data;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import org.javatuples.Pair;

import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Simple container around the player's death bans.
 */
public class BanManager {
    private final NavigableMap<Integer, Ban> bans = new TreeMap<>();

    public BanManager() {
    }

    public BanManager(NavigableMap<Integer, Ban> existing) {
        if (existing != null) {
            this.bans.putAll(existing);
        }
    }

    public NavigableMap<Integer, Ban> getBans() {
        return Collections.unmodifiableNavigableMap(this.bans);
    }

    public int getBanCount() {
        return bans.size();
    }

    public Pair<Integer, Ban> addBan(Ban ban) {
        int key = (bans.isEmpty() ? 0 : bans.lastKey()) + 1;
        bans.put(key, ban);
        return new Pair<>(key, ban);
    }

    public void clearBans() {
        this.bans.clear();
    }

    public Ban getLastDeathBan() {
        Map.Entry<Integer, Ban> entry = bans.lastEntry();
        return entry == null ? null : entry.getValue();
    }
}
