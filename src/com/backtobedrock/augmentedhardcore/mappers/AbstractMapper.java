package com.backtobedrock.augmentedhardcore.mappers;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Database;

public abstract class AbstractMapper {
    protected final AugmentedHardcore plugin;
    protected final Database database;

    public AbstractMapper(AugmentedHardcore plugin) {
        this.plugin = plugin;
        this.database = this.plugin.getConfigurations().getDataConfiguration().getDatabase();
    }
}
