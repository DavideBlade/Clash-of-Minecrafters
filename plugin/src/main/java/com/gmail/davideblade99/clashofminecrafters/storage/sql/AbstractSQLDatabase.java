/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.storage.PlayerDatabase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractSQLDatabase implements PlayerDatabase {

    protected final CoM plugin;
    private final ConnectionPoolManager pool;

    /**
     * @param plugin  Plugin instance
     * @param driver  JDBC driver to use for database connection
     * @param JDBCUrl JDBC url to connect to the database
     */
    public AbstractSQLDatabase(@Nonnull final CoM plugin, @Nonnull final String driver, @Nonnull final String JDBCUrl) {
        this(plugin, driver, JDBCUrl, null, null);
    }

    /**
     * @param plugin   Plugin instance
     * @param driver   JDBC driver to use for database connection
     * @param JDBCUrl  JDBC url to connect to the database
     * @param user     Username for database login
     * @param password Password for database login
     */
    public AbstractSQLDatabase(@Nonnull final CoM plugin, @Nonnull final String driver, @Nonnull final String JDBCUrl, @Nullable final String user, @Nullable final String password) {
        this.plugin = plugin;
        this.pool = new ConnectionPoolManager(driver, JDBCUrl, user, password);
    }

    /**
     * Closes all connections
     */
    public final void shutdown() {
        pool.closePool();
    }

    /**
     * @return A connection to the database, obtained from the pool
     *
     * @throws SQLException In case of problems with the connection
     */
    protected final Connection getConnection() throws SQLException {
        return pool.getConnection();
    }

    /**
     * @return The manager of connections in the pool
     *
     * @since 3.1
     */
    protected final ConnectionPoolManager getConnectionPool() {
        return pool;
    }
}
