/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.setting.Config;
import com.gmail.davideblade99.clashofminecrafters.storage.file.YAMLDatabase;
import com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.MySQLDatabase;

import javax.annotation.Nonnull;
import java.sql.SQLException;

/**
 * This class represents a database factory for various types of databases.
 * You can use {@link #getInstance(CoM)} to obtain a new instance for the
 * database specified in the configuration file.
 *
 * @see DatabaseType
 */
public final class DatabaseFactory {

    private DatabaseFactory() {
        throw new IllegalAccessError();
    }

    /**
     * @param plugin Plugin instance
     *
     * @return A new database instance based on the configuration file
     *
     * @throws SQLException If database initialization errors occur
     */
    @Nonnull
    public static PlayerDatabase getInstance(@Nonnull final CoM plugin) throws SQLException {
        final Config settings = plugin.getConfig();
        switch (settings.getDatabaseType()) {
            case YAML:
                return new YAMLDatabase(plugin);
            case MYSQL:
                return new MySQLDatabase(plugin, settings.getHost(), settings.getPort(), settings.getDatabase(), settings.getUsername(), settings.getPassword());
            default:
                throw new IllegalStateException("Unexpected value: " + settings.getDatabaseType());
        }
    }
}
