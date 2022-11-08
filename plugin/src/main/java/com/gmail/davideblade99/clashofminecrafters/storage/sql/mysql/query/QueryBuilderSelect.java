/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL SELECT query builder.
 *
 * Set a table with {@link #from(String)} is mandatory. If it is not set before calling {@link #toQuery()}, an
 * {@link IllegalStateException} will be thrown.
 *
 * This object can only be instantiated via {@link QueryBuilder}.
 *
 * @author DavideBlade
 * @since v3.1
 */
public final class QueryBuilderSelect extends WhereableQueryBuilder {

    private final List<String> columns = new ArrayList<>();
    private final List<String> orders = new ArrayList<>();
    private int limit = 0;

    /**
     * Creates a new SELECT query builder for the specified columns
     *
     * @param columns Query target columns. Use * to select them all.
     *
     * @throws IllegalArgumentException If no column is selected
     */
    QueryBuilderSelect(@Nonnull final String[] columns) {
        super();

        if (columns.length == 0)
            throw new IllegalArgumentException("At least one column must be selected in order to run the query");

        for (String column : columns)
            this.columns.add(column.equals("*") ? column : getEscaped(column));
    }

    /**
     * Sets the target table of the query
     *
     * @param table Table to be specified in the FROM clause of the query
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderSelect from(@Nonnull final String table) {
        super.table = table;
        return this;
    }

    /**
     * Adds an ORDER BY ascending clause on the specified column
     *
     * @param column Target column
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderSelect orderBy(@Nonnull final String column) {
        this.orders.add(getEscaped(column) + " ASC");
        return this;
    }

    /**
     * Adds an ORDER BY ascending clause using the specified function
     *
     * @param function Mathematical function to be used to sort query results
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderSelect orderBy(@Nonnull final Function function) {
        this.orders.add(function + " ASC");
        return this;
    }

    /**
     * Adds an ORDER BY descending clause on the specified column
     *
     * @param column Target column
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderSelect orderByDesc(@Nonnull final String column) {
        this.orders.add(getEscaped(column) + " DESC");
        return this;
    }

    /**
     * Set the LIMIT clause
     *
     * @param limit Maximum number of results to fetch. A negative number is equivalent to 0.
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderSelect limit(final int limit) {
        this.limit = Math.max(limit, 0); // Prevent negative numbers
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public QueryBuilderSelect whereSafe(@Nonnull final String column) {
        super.whereSafe(column);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public QueryBuilderSelect where(@Nonnull final String column, @Nonnull final String value) {
        super.where(column, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public QueryBuilderSelect where(@Nonnull final String column, @Nonnull final String condition, @Nonnull final String value) {
        super.where(column, condition, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public QueryBuilderSelect where(@Nonnull final WhereClauseBuilder whereBuilder) {
        super.where(whereBuilder);
        return this;
    }

    /**
     * Builds the query. WHERE clauses will be chained with the logical AND operator.
     *
     * @return String representation of the query
     *
     * @throws IllegalStateException If no target table was specified with {@link #from(String)}
     */
    @Override
    @Nonnull
    public String toQuery() {
        if (super.table == null)
            throw new IllegalStateException("You cannot run a SELECT query without specifying the target table");

        String sql = "SELECT " + String.join(", ", this.columns) + " FROM " + getEscaped(super.table);
        if (super.wheres.size() > 0)
            sql += " WHERE " + String.join(" AND ", super.wheres);
        if (this.orders.size() > 0)
            sql += " ORDER BY " + String.join(", ", this.orders);
        if (this.limit > 0)
            sql += " LIMIT " + this.limit;
        return sql + ";";
    }
}
