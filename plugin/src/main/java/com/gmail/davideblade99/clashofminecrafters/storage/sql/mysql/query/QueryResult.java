/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A handler to manage the result of a MySQL query.
 *
 * The query builders in this package allow you to specify a {@link QueryResult} to get the result of a query. The
 * {@link #completed(Object)} method is invoked when the query completes successfully. The {@link
 * #failed(SQLException)} method is invoked if the query fails.
 *
 * @author DavideBlade
 * @see QueryBuilder
 * @see QueryBuilderBase
 * @since 3.1
 */
public interface QueryResult {

    /**
     * Invoked when a query has been completed
     *
     * @param result The {@link ResultSet} of the query
     * @param <R>    The type of the query result
     */
    <R> void completed(@Nonnull final R result);

    /**
     * Invoked when a query fails
     *
     * @param ex The {@link SQLException} indicating why the query failed
     */
    void failed(@Nonnull final SQLException ex);
}
