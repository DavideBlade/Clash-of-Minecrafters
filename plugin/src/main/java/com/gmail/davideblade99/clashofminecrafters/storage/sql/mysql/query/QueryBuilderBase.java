/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

import com.gmail.davideblade99.clashofminecrafters.storage.sql.ConnectionPoolManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;

/**
 * Base of MySQL query builders. Contains all general methods that can be used by all builders.
 *
 * Classes that extend this class can only be instantiated via {@link QueryBuilder}.
 *
 * @author DavideBlade
 * @see QueryBuilderSelect
 * @see QueryBuilderInsert
 * @see QueryBuilderUpdate
 * @see QueryBuilderDelete
 * @see QueryBuilderCreateTable
 * @see QueryBuilderAlterTable
 * @since 3.1
 */
abstract class QueryBuilderBase {
    final static char ESCAPE_CHARACTER = '`';
    final static char SINGLE_QUOTE_CHARACTER = '\'';

    String table;

    /**
     * Instantiates a new (empty) query builder
     */
    QueryBuilderBase() { }

    /**
     * Creates a new query builder for the specified table
     *
     * @param tableName Query target table
     */
    QueryBuilderBase(@Nonnull final String tableName) {
        this.table = tableName;
    }

    /**
     * After replacing the parameters, it executes the query with a prepared statement on a connection obtained
     * from the pool. The result set obtained will be passed to the converter, which will transform it into another
     * object, which will be returned to the caller at the end of operations.
     *
     * This method can only be used with queries that produce a result (like the SELECT query).
     *
     * @param connectionPool Pool from which to get a connection
     * @param converter      Converter that translates the {@link ResultSet} into another Object
     * @param params         Objects that replace parameters in the query. {@code Null} if there are no parameters
     *                       to replace.
     * @param <T>            Type of the object into which the result set is converted
     *
     * @return An object obtained by converting the result set obtained from the query or {@code null} if the
     * result set is empty
     *
     * @throws SQLException If a database access error occurs
     * @see ResultSetConverter
     * @see #executeUpdate(ConnectionPoolManager, QueryResult, Object...)
     * @see #execute(ConnectionPoolManager, QueryResult)
     * @see #execute(ConnectionPoolManager)
     */
    public final <T> T executeQuery(@Nonnull final ConnectionPoolManager connectionPool, @Nonnull final ResultSetConverter<T> converter, @Nullable final Object... params) throws SQLException {
        try (
                final Connection conn = connectionPool.getConnection();
                final PreparedStatement statement = conn.prepareStatement(toQuery())
        ) {
            // Set statement parameters
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    if (params[i] == null) // Check not strictly necessary, but improves compatibility
                        statement.setNull(i + 1, Types.NULL);
                    else
                        statement.setObject(i + 1, params[i]);
                }
            }

            /*
             * Convert ResultSet to another object to be able to close the ResultSet and deallocate resources
             * within the same method. This operation is needed as a best-practice: close the ResultSet instead
             * of leaving this task to the caller (easier handling and less chance of error).
             */
            final ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? converter.process(resultSet) : null;
        }
    }

    /**
     * After replacing the parameters, it executes the query with a prepared statement on a connection obtained
     * from the pool. The number of rows affected by the execution is passed to {@link QueryResult}: the {@link
     * QueryResult#failed(SQLException)} method will be called in case of an error and the {@link
     * QueryResult#completed(Object)} method on success.
     *
     * This method can only be used with queries that have no results (such as DDL queries).
     *
     * @param connectionPool Pool from which to get a connection
     * @param processor      Manager to whom the result of the execution is passed
     * @param params         Objects that replace parameters in the query. {@code Null} if there are no parameters
     *                       to replace.
     *
     * @see QueryResult
     * @see #executeQuery(ConnectionPoolManager, ResultSetConverter, Object...)
     * @see #execute(ConnectionPoolManager, QueryResult)
     * @see #execute(ConnectionPoolManager)
     */
    public final void executeUpdate(@Nonnull final ConnectionPoolManager connectionPool, @Nonnull final QueryResult processor, @Nullable final Object... params) {
        try (
                final Connection conn = connectionPool.getConnection();
                final PreparedStatement statement = conn.prepareStatement(toQuery())
        ) {
            // Set statement parameters
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    if (params[i] == null) // Check not strictly necessary, but improves compatibility
                        statement.setNull(i + 1, Types.NULL);
                    else
                        statement.setObject(i + 1, params[i]);
                }
            }

            processor.completed(statement.executeUpdate()); // Success
        } catch (final SQLException e) {
            processor.failed(e); // Failure
        }
    }

    /**
     * Executes the query on a connection obtained from the pool. The result of the execution is passed to {@link
     * QueryResult}: the {@link QueryResult#failed(SQLException)} method will be called in case of an error and the
     * {@link QueryResult#completed(Object)} method on success.
     *
     * @param connectionPool Pool from which to get a connection
     * @param processor      Manager to whom the result of the execution is passed
     *
     * @see QueryResult
     * @see #executeUpdate(ConnectionPoolManager, QueryResult, Object...)
     * @see #executeQuery(ConnectionPoolManager, ResultSetConverter, Object...)
     * @see #execute(ConnectionPoolManager)
     */
    public final void execute(@Nonnull final ConnectionPoolManager connectionPool, @Nonnull final QueryResult processor) {
        try (
                final Connection conn = connectionPool.getConnection();
                final Statement statement = conn.createStatement()
        ) {
            processor.completed(statement.execute(toQuery())); // Success
        } catch (final SQLException e) {
            processor.failed(e); // Failure
        }
    }

    /**
     * Executes the query on a connection obtained from the pool. The result of the execution is ignored and any
     * error is propagated externally.
     *
     * @param connectionPool Pool from which to get a connection
     *
     * @throws SQLException If a database access error occurs
     * @see #executeQuery(ConnectionPoolManager, ResultSetConverter, Object...)
     * @see #executeUpdate(ConnectionPoolManager, QueryResult, Object...)
     * @see #executeQuery(ConnectionPoolManager, ResultSetConverter, Object...)
     */
    public final void execute(@Nonnull final ConnectionPoolManager connectionPool) throws SQLException {
        try (
                final Connection conn = connectionPool.getConnection();
                final Statement statement = conn.createStatement()
        ) {
            statement.execute(toQuery());
        }
    }

    /**
     * @param item String to add escape characters to
     *
     * @return The string passed with escape characters at the beginning and end
     *
     * @see #ESCAPE_CHARACTER
     */
    @Nonnull
    final String getEscaped(@Nonnull final String item) {
        return ESCAPE_CHARACTER + item + ESCAPE_CHARACTER;
    }

    /**
     * @param item String to which single quote characters should be added
     *
     * @return The string passed with single quote character at the beginning and end.
     *
     * @see #SINGLE_QUOTE_CHARACTER
     */
    @Nonnull
    final String getQuoted(@Nullable final String item) {
        return SINGLE_QUOTE_CHARACTER + item + SINGLE_QUOTE_CHARACTER;
    }

    /**
     * Builds the query with all the information passed to the methods.
     *
     * @return String representation of the query
     */
    @Nonnull
    public abstract String toQuery();
}
