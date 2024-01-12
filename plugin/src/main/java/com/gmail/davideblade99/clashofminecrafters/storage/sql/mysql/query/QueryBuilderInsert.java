/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * MySQL INSERT query builder.
 *
 * Set columns and values to be inserted with {@link #safeValue(String...)} or {@link #value(String, String)} is
 * mandatory. If they are not set before calling {@link #toQuery()}, an {@link IllegalStateException} will be
 * thrown.
 *
 * This object can only be instantiated via {@link QueryBuilder}.
 *
 * @author DavideBlade
 * @since 3.1
 */
public final class QueryBuilderInsert extends QueryBuilderBase {

    /*
     * For fields that may contain parametric values, it is necessary to use containers (lists, maps...) of
     * elements that maintain the insertion order. Indeed, if the order of the parameters were reversed, inverted
     * values would be set in the columns.
     *
     * Example:
     * map.put("X", "?");
     * map.put("Y", "?");
     *
     * PreparedStatement stmt = ...;
     * stmt.setInt(1, 1);
     * stmt.setInt(2, 2);
     *
     * // If the order is not maintained, we may have X <-> 2 and Y <-> 1, instead of X <-> 1 and Y <-> 2
     */

    /** List of columns in which to insert new values {@link #values} */
    private final List<String> columns = new ArrayList<>();

    /** List of values to be inserted within {@link #columns} */
    private final List<String> values = new ArrayList<>();

    /** Contains columns and values with which to update the record in case of duplicates */
    private final Map<String, String> onDuplicateKeyUpdate = new LinkedHashMap<>();

    /**
     * Creates a new INSERT query builder for the specified table
     *
     * @param tableName Table in which to insert new records
     */
    QueryBuilderInsert(@Nonnull final String tableName) {
        super(tableName);
    }

    /**
     * Adds columns in the INTO clause in which to insert a parametric value (question mark character), specified
     * in the VALUES clause
     *
     * @param columns Name of the columns in which to insert the parametric values
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderInsert safeValue(@Nonnull final String... columns) {
        for (String column : columns) {
            this.columns.add(getEscaped(column));
            this.values.add("?");
        }
        return this;
    }

    /**
     * Adds the column in which to insert the specified value
     *
     * @param column Target column name
     * @param value  Value to be inserted in the target column
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderInsert value(@Nonnull final String column, @Nullable final String value) {
        this.columns.add(getEscaped(column));
        this.values.add(value == null ? null : getQuoted(value));
        return this;
    }

    /**
     * Converts {@code value} to {@code String} and calls the {@link #value(String, String)} method
     *
     * @see #value(String, String)
     */
    @Nonnull
    public QueryBuilderInsert value(@Nonnull final String column, final int value) {
        return this.value(column, Integer.toString(value));
    }

    /**
     * Adds the column and a parametric value (question mark character) to the ON DUPLICATE KEY UPDATE clause
     *
     * @param column Column to be updated if a duplicate is found
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderInsert onDuplicateKeyUpdateSafe(@Nonnull final String column) {
        this.onDuplicateKeyUpdate.put(getEscaped(column), "?");
        return this;
    }

    /**
     * Adds the column and value to the ON DUPLICATE KEY UPDATE clause
     *
     * @param column Column to be updated if a duplicate is found
     * @param value  Value to update the column with
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderInsert onDuplicateKeyUpdate(@Nonnull final String column, @Nullable final String value) {
        this.onDuplicateKeyUpdate.put(getEscaped(column), value == null ? null : getQuoted(value));
        return this;
    }

    /**
     * Converts {@code value} to {@code String} and calls the {@link #onDuplicateKeyUpdate(String, String)} method
     *
     * @see #onDuplicateKeyUpdate(String, String)
     */
    @Nonnull
    public QueryBuilderInsert onDuplicateKeyUpdate(@Nonnull final String column, final int value) {
        return this.onDuplicateKeyUpdate(column, Integer.toString(value));
    }

    /**
     * Builds the query
     *
     * @return String representation of the query
     *
     * @throws IllegalStateException If {@link #columns} or {@link #values} are empty (i.e.,there are no columns in
     *                               the INTO clause or values in the VALUES clause)
     */
    @Override
    @Nonnull
    public String toQuery() {
        if (this.columns.size() == 0)
            throw new IllegalStateException("You cannot run an INSERT query without specifying the columns in which to insert values");
        if (this.values.size() == 0)
            throw new IllegalStateException("You cannot run an INSERT query without specifying the values to be inserted");

        String sql = "INSERT INTO " + getEscaped(super.table) + " (" + String.join(", ", this.columns) + ") VALUES (" + String.join(", ", this.values) + ")";
        if (!this.onDuplicateKeyUpdate.isEmpty()) {
            sql += " ON DUPLICATE KEY UPDATE ";

            final StringJoiner joiner = new StringJoiner(", ");
            for (Map.Entry<String, String> entry : onDuplicateKeyUpdate.entrySet())
                joiner.add(entry.getKey() + " = " + entry.getValue());

            sql += joiner.toString();
        }

        return sql + ";";
    }
}
