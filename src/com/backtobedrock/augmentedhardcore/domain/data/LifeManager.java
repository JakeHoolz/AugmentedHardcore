package com.backtobedrock.augmentedhardcore.domain.data;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;

/**
 * Handles life and life-part bookkeeping for a player.
 */
public class LifeManager {
    private final AugmentedHardcore plugin;
    private int lives;
    private int lifeParts;

    public LifeManager(AugmentedHardcore plugin, int lives, int lifeParts) {
        this.plugin = plugin;
        this.setLives(lives);
        this.setLifeParts(lifeParts);
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        int maxLives = plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLives();
        this.lives = Math.max(0, Math.min(lives, maxLives));
    }

    public void increaseLives(int amount) {
        setLives(this.lives + amount);
    }

    public void decreaseLives(int amount) {
        setLives(this.lives - amount);
    }

    public int getLifeParts() {
        return lifeParts;
    }

    public void setLifeParts(int lifeParts) {
        int lifePartsPerLife = plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsPerLife();
        int livesToAdd = Math.min(lifeParts / lifePartsPerLife,
                plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLives() - this.getLives());
        if (livesToAdd > 0) {
            increaseLives(livesToAdd);
        }
        int maxLifeParts = plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLifeParts();
        this.lifeParts = Math.min(maxLifeParts, Math.max(0, lifeParts - livesToAdd * lifePartsPerLife));
    }

    public void increaseLifeParts(int amount) {
        setLifeParts(this.lifeParts + amount);
    }

    public void decreaseLifeParts(int amount) {
        setLifeParts(this.lifeParts - amount);
    }
}
