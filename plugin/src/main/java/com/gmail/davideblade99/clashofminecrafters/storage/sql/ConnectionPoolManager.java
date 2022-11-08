/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;

public final class ConnectionPoolManager {

    /** @see <a href="https://github.com/brettwooldridge/HikariCP">documentation</a> */
    private final HikariDataSource dataSource;

    public ConnectionPoolManager(@Nonnull final String driver, @Nonnull final String JDBCUrl, @Nullable final String user, @Nullable final String password) {
        final HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setJdbcUrl(JDBCUrl);

        // Some databases have no login mechanism
        if (user != null)
            config.setUsername(user);
        if (password != null)
            config.setPassword(password);

        config.setMaximumPoolSize(15);
        dataSource = new HikariDataSource(config);
    }

    /**
     * @return A connection to the database, obtained from the pool
     *
     * @throws SQLException In case of problems with the connection
     */
    @NotNull
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    void closePool() {
        if (!dataSource.isClosed())
            dataSource.close();
    }
}
