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
 * Represents MySQL query builders that can specify the WHERE clause
 *
 * @author DavideBlade
 * @see QueryBuilderSelect
 * @see QueryBuilderUpdate
 * @see QueryBuilderDelete
 * @since v3.1
 */
abstract class WhereableQueryBuilder extends QueryBuilderBase {

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

    /** Contains all Boolean expressions in the WHERE clause */
    final List<String> wheres = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    WhereableQueryBuilder() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    WhereableQueryBuilder(@Nonnull final String tableName) {
        super(tableName);
    }

    /**
     * Adds a parametric WHERE clause (question mark character) for the specified column
     *
     * @param column Target column
     *
     * @return This builder
     *
     * @implSpec This method should be overridden to return the type of the child builder so that you can continue
     * to chain the child's methods (not present in this class)
     */
    @Nonnull
    public WhereableQueryBuilder whereSafe(@Nonnull final String column) {
        this.wheres.add(getEscaped(column) + " = ?");
        return this;
    }

    /**
     * Adds a WHERE clause whereby the passed column must be equal to the passed value
     *
     * @param column Target column
     * @param value  Value to compare the column with
     *
     * @return This builder
     *
     * @implSpec This method should be overridden to return the type of the child builder so that you can continue
     * to chain the child's methods (not present in this class)
     */
    @Nonnull
    public WhereableQueryBuilder where(@Nonnull final String column, @Nonnull final String value) {
        this.wheres.add(getEscaped(column) + " = " + getQuoted(value));
        return this;
    }

    /**
     * Adds a WHERE clause built with the passed parameters
     *
     * @param column    Target column
     * @param condition Column and value comparison operator (e.g. >, <=, =, ...)
     * @param value     Value to compare the column with
     *
     * @return This builder
     *
     * @implSpec This method should be overridden to return the type of the child builder so that you can continue
     * to chain the child's methods (not present in this class)
     */
    @Nonnull
    public WhereableQueryBuilder where(@Nonnull final String column, @Nonnull final String condition, @Nonnull final String value) {
        this.wheres.add(getEscaped(column) + " " + condition + " " + getQuoted(value));
        return this;
    }

    /**
     * Adds a WHERE clause built with {@link WhereClauseBuilder}
     *
     * @param whereBuilder WHERE clause builder
     *
     * @return This builder
     *
     * @implSpec This method should be overridden to return the type of the child builder so that you can continue
     * to chain the child's methods (not present in this class)
     * @see WhereClauseBuilder
     */
    @Nonnull
    public WhereableQueryBuilder where(@Nonnull final WhereClauseBuilder whereBuilder) {
        this.wheres.add(whereBuilder.toString());
        return this;
    }
}
