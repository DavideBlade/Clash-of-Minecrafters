/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

import javax.annotation.Nonnull;

/**
 * MySQL DELETE query builder.
 *
 * This object can only be instantiated via {@link QueryBuilder}.
 *
 * @author DavideBlade
 * @since 3.1
 */
public final class QueryBuilderDelete extends WhereableQueryBuilder {

    /**
     * Creates a new DELETE query builder for the specified table
     *
     * @param tableName Table from which to delete records
     */
    QueryBuilderDelete(@Nonnull final String tableName) {
        super(tableName);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public QueryBuilderDelete whereSafe(@Nonnull final String column) {
        super.whereSafe(column);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public QueryBuilderDelete where(@Nonnull final String column, @Nonnull final String value) {
        super.where(column, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public QueryBuilderDelete where(@Nonnull final String column, @Nonnull final String condition, @Nonnull final String value) {
        super.where(column, condition, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public QueryBuilderDelete where(@Nonnull final WhereClauseBuilder whereBuilder) {
        super.where(whereBuilder);
        return this;
    }

    /**
     * Builds the query. WHERE clauses will be chained with the logical AND operator.
     *
     * @return String representation of the query
     */
    @Override
    @Nonnull
    public String toQuery() {
        String sql = "DELETE FROM " + getEscaped(super.table);
        if (super.wheres.size() > 0)
            sql += " WHERE " + String.join(" AND ", super.wheres);
        return sql + ";";
    }
}
