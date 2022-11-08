/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL UPDATE query builder.
 *
 * Set the columns to be updated with {@link #setSafe(String)} or {@link #set(String, String)} is mandatory. If
 * they are not set before calling {@link #toQuery()}, an {@link IllegalStateException} will be thrown.
 *
 * This object can only be instantiated via {@link QueryBuilder}.
 *
 * @author DavideBlade
 * @since v3.1
 */
public final class QueryBuilderUpdate extends WhereableQueryBuilder {

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

    private final List<String> columns = new ArrayList<>();

    /**
     * Creates a new UPDATE query builder for the specified table
     *
     * @param tableName Table to update with the query
     */
    QueryBuilderUpdate(@Nonnull final String tableName) {
        super(tableName);
    }

    /**
     * Adds a column to update with a parametric value (question mark character) to the SET clause
     *
     * @param column Column to be updated
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderUpdate setSafe(@Nonnull final String column) {
        this.columns.add(getEscaped(column) + " = ?");
        return this;
    }

    /**
     * Adds a column to update with the passed value to the SET clause
     *
     * @param column Column to be updated
     * @param value  New value with which to update the column
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderUpdate set(@Nonnull final String column, @Nullable final String value) {
        this.columns.add(getEscaped(column) + " = " + getQuoted(value));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public QueryBuilderUpdate whereSafe(@Nonnull final String column) {
        super.whereSafe(column);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public QueryBuilderUpdate where(@Nonnull final String column, @Nonnull final String value) {
        super.where(column, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public QueryBuilderUpdate where(@Nonnull final String column, @Nonnull final String condition, @Nonnull final String value) {
        super.where(column, condition, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public QueryBuilderUpdate where(@Nonnull final WhereClauseBuilder whereBuilder) {
        super.where(whereBuilder);
        return this;
    }

    /**
     * Builds the query. WHERE clauses will be chained with the logical AND operator.
     *
     * @return String representation of the query
     *
     * @throws IllegalStateException If {@link #columns} is empty (i.e., there are no columns in the SET clause)
     */
    @Override
    @Nonnull
    public String toQuery() {
        if (this.columns.size() == 0)
            throw new IllegalStateException("You cannot run an UPDATE query without specifying the column(s) to be updated");

        String sql = "UPDATE " + getEscaped(super.table) + " SET " + String.join(", ", this.columns);
        if (super.wheres.size() > 0)
            sql += " WHERE " + String.join(" AND ", super.wheres);
        return sql + ";";
    }
}
