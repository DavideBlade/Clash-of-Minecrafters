/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

import javax.annotation.Nonnull;

/**
 * Builder of MySQL query. From here you can access 6 types of queries that can be executed: SELECT, INSERT,
 * DELETE, UPDATE, CREATE TABLE, ALTER TABLE
 *
 * @author DavideBlade
 * @see QueryBuilderSelect
 * @see QueryBuilderInsert
 * @see QueryBuilderUpdate
 * @see QueryBuilderDelete
 * @see QueryBuilderCreateTable
 * @see QueryBuilderAlterTable
 * @since v3.1
 */
public final class QueryBuilder {

    private QueryBuilder() {
        throw new IllegalAccessError();
    }

    /**
     * @param columns Columns that the query will select. Use * to select all of them.
     *
     * @return A new SELECT query builder
     *
     * @see QueryBuilderSelect
     */
    @Nonnull
    public static QueryBuilderSelect select(@Nonnull final String... columns) {
        return new QueryBuilderSelect(columns.length == 0 ? new String[]{"*"} : columns); // Selects all columns if none is specified
    }

    /**
     * @param table Table into which to insert new records
     *
     * @return A new INSERT query builder
     *
     * @see QueryBuilderInsert
     */
    @Nonnull
    public static QueryBuilderInsert insert(@Nonnull final String table) {
        return new QueryBuilderInsert(table);
    }

    /**
     * @param table Table to be updated
     *
     * @return A new UPDATE query builder
     *
     * @see QueryBuilderUpdate
     */
    @Nonnull
    public static QueryBuilderUpdate update(@Nonnull final String table) {
        return new QueryBuilderUpdate(table);
    }

    /**
     * @param table Table from which to remove existing records
     *
     * @return A new DELETE query builder
     *
     * @see QueryBuilderDelete
     */
    @Nonnull
    public static QueryBuilderDelete delete(@Nonnull final String table) {
        return new QueryBuilderDelete(table);
    }

    /**
     * @param table Table to create
     *
     * @return A new CREATE TABLE query builder
     *
     * @see QueryBuilderCreateTable
     */
    @Nonnull
    public static QueryBuilderCreateTable createTable(@Nonnull final String table) {
        return new QueryBuilderCreateTable(table);
    }

    /**
     * @param table Table to edit
     *
     * @return A new ALTER TABLE query builder
     *
     * @see QueryBuilderAlterTable
     */
    @Nonnull
    public static QueryBuilderAlterTable alterTable(@Nonnull final String table) {
        return new QueryBuilderAlterTable(table);
    }
}
