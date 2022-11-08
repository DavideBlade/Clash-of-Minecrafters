/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * MySQL CREATE TABLE query builder.
 *
 * Set columns within the table with {@link #withColumn(String, String)} and set the primary key with {@link
 * #primaryKey(String...)} is mandatory. If they are not set before calling {@link #toQuery()}, an {@link
 * IllegalStateException} will be thrown.
 *
 * This object can only be instantiated via {@link QueryBuilder}.
 *
 * @author DavideBlade
 * @since v3.1
 */
public final class QueryBuilderCreateTable extends QueryBuilderBase {

    /** List of columns with relative datatype */
    private final Map<String, String> columns = new LinkedHashMap<>();

    /** List of primary key columns */
    private final List<String> primaryKey = new ArrayList<>();

    /** Whether the table should be created only if it does not already exist */
    private boolean ifNotExists;

    /** Table charset */
    private String charset;

    /** Table comment */
    private String comment;

    /**
     * Creates a new CREATE TABLE query builder for the specified table
     *
     * @param tableName Table to create
     */
    QueryBuilderCreateTable(@Nonnull final String tableName) {
        super(tableName);
    }

    /**
     * Adds the IF NOT EXISTS clause to the query
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderCreateTable ifNotExists() {
        this.ifNotExists = true;
        return this;
    }

    /**
     * Adds the column to the table
     *
     * @param column   Column name
     * @param datatype Types of data that the column will contain
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderCreateTable withColumn(@Nonnull final String column, @Nonnull final String datatype) {
        this.columns.put(getEscaped(column), datatype);
        return this;
    }

    /**
     * Sets the columns that compose the primary key
     *
     * @param columns Primary key columns name
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderCreateTable primaryKey(@Nonnull final String... columns) {
        for (String column : columns)
            this.primaryKey.add(getEscaped(column));

        return this;
    }

    /**
     * Sets the encoding with the CHARACTER SET clause
     *
     * @param charset Table charset to set
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderCreateTable setCharset(@Nonnull final String charset) {
        this.charset = charset;
        return this;
    }

    /**
     * Sets comments in the COMMENT clause
     *
     * @param comment Comment to add to the table
     *
     * @return This builder
     */
    @Nonnull
    public QueryBuilderCreateTable setComment(@Nonnull final String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * Builds the query
     *
     * @return String representation of the query
     *
     * @throws IllegalStateException If {@link #columns} or {@link #primaryKey} are empty (i.e., no column was
     *                               specified with {@link #withColumn(String, String)} and/or with {@link
     *                               #primaryKey(String...)}) or if the columns specified as primary keys do not
     *                               exist (i.e., the {@link #withColumn(String, String)} method has not been
     *                               called on them)
     */
    @Nonnull
    @Override
    public String toQuery() {
        if (this.columns.size() == 0)
            throw new IllegalStateException("You cannot run a CREATE TABLE query without specifying the columns within the table");
        if (this.primaryKey.size() == 0)
            throw new IllegalStateException("You cannot run a CREATE TABLE query without specifying the primary keys of the table");
        for (String primaryKey : this.primaryKey) {
            if (!this.columns.containsKey(primaryKey))
                throw new IllegalStateException("A primary key can only be set to an existing column");
        }

        String sql = "CREATE TABLE ";
        if (this.ifNotExists)
            sql += "IF NOT EXISTS ";
        sql += getEscaped(super.table) + " (";

        final StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<String, String> entry : this.columns.entrySet())
            joiner.add(entry.getKey() + " " + entry.getValue());
        sql += joiner;

        sql += ", PRIMARY KEY (" + String.join(", ", this.primaryKey) + ")";
        sql += ")";

        if (charset != null)
            sql += " CHARACTER SET = " + charset;
        if (comment != null)
            sql += " COMMENT " + getQuoted(comment);

        return sql + ";";
    }
}
