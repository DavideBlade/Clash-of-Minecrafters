/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * MySQL ALTER TABLE query builder.
 *
 * Change at least one column in the table with {@link #addColumn(String, String)}, {@link #dropColumn(String)} or
 * {@link #modifyColumn(String, String)} is mandatory. If they are not set before calling {@link #toQuery()}, an
 * {@link IllegalStateException} will be thrown.
 *
 * This object can only be instantiated via {@link QueryBuilder}.
 *
 * @author DavideBlade
 * @since 3.1
 */
public final class QueryBuilderAlterTable extends QueryBuilderBase {

    private final Map<String, String> columnsToAdd = new HashMap<>(); // Column name <-> datatype
    private final List<String> columnsToDrop = new ArrayList<>();
    private final Map<String, String> columnsToModify = new HashMap<>(); // Column name <-> new datatype

    /**
     * Creates a new ALTER TABLE query builder for the specified table
     *
     * @param tableName Table to create
     */
    QueryBuilderAlterTable(@Nonnull final String tableName) {
        super(tableName);
    }

    /**
     * Adds a column to the ADD COLUMN clause
     *
     * @param column   Column name
     * @param datatype Types of data that the column will contain
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderAlterTable addColumn(@Nonnull final String column, @Nonnull final String datatype) {
        this.columnsToAdd.put(getEscaped(column), datatype);
        return this;
    }

    /**
     * Adds a column to the DROP COLUMN clause
     *
     * @param column Column name
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderAlterTable dropColumn(@Nonnull final String column) {
        this.columnsToDrop.add(getEscaped(column));
        return this;
    }

    /**
     * Adds a column to the MODIFY COLUMN clause
     *
     * @param column   Column name
     * @param datatype Types of data that the column will contain
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderAlterTable modifyColumn(@Nonnull final String column, @Nonnull final String datatype) {
        this.columnsToModify.put(getEscaped(column), datatype);
        return this;
    }

    /**
     * Builds the query
     *
     * @return String representation of the query
     *
     * @throws IllegalStateException If {@link #columnsToAdd}, {@link #columnsToDrop} or {@link #columnsToModify}
     *                               are empty (i.e., no columns were specified to be modified using the {@link
     *                               #addColumn(String, String)}, {@link #dropColumn(String)} or {@link
     *                               #modifyColumn(String, String)} methods)
     */
    @Nonnull
    @Override
    public String toQuery() {
        if (this.columnsToAdd.isEmpty() && this.columnsToDrop.isEmpty() && this.columnsToModify.isEmpty())
            throw new IllegalStateException("No column to change in the table");

        String sql = "ALTER TABLE " + getEscaped(super.table) + " ";

        final StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<String, String> entry : this.columnsToAdd.entrySet())
            joiner.add("ADD COLUMN " + entry.getKey() + " " + entry.getValue());

        for (String column : this.columnsToDrop)
            joiner.add("DROP COLUMN " + column);

        for (Map.Entry<String, String> entry : this.columnsToModify.entrySet())
            joiner.add("MODIFY COLUMN " + entry.getKey() + " " + entry.getValue());

        sql += joiner.toString();

        return sql + ";";
    }
}
