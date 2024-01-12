/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

import javax.annotation.Nonnull;

/**
 * Builder of the WHERE clause for MySQL queries. You can concatenate multiple expressions that the WHERE clause in
 * the query will evaluate.
 *
 * In order to build a WHERE clause, first you must specify a column with the {@link #whereColumn(String)} method.
 * After that, you can construct its Boolean expression. After that, you can add further conditions with {@link
 * #orColumn(String)} or {@link #andColumn(String)}, which will add the logical {@code OR} and {@code AND} operator
 * followed by the column subject to the new Boolean expression to the WHERE clause, respectively. Once the
 * concatenation of expressions is finished, the complete WHERE clause (without the WHERE keyword) can be obtained
 * with {@link #toString()}.
 *
 * @author DavideBlade
 * @see QueryBuilder
 * @see ExpressionBuilder
 * @since 3.1
 */
public final class WhereClauseBuilder {

    private final StringBuilder builder = new StringBuilder();

    /**
     * Used to figure out whether the WHERE clause is in a consistent state. For example, if {@link
     * #orColumn(String)} is called multiple times without specifying the attached expression, it would not be in a
     * consistent state.
     *
     * It is false when the column has been specified but not the expression. It is true if the column and
     * expression have been specified and therefore the state is consistent.
     */
    private boolean valid = false;

    /**
     * Number of open parentheses without the corresponding closing parenthesis.
     *
     * It is used to check that the query is syntactically correct: before calling the {@link #toString()} method
     * this value must be 0, indicating that all open parentheses have also been closed. A value other than 0
     * indicates that there are more open parentheses than closed ones.
     */
    private byte openParentheses = 0;

    /**
     * Specifies the first column of the WHERE clause
     *
     * @param column Target column, eventually with the table identifier (i.e. table.column)
     *
     * @return A new Boolean expression builder
     *
     * @throws IllegalStateException If the first column of the WHERE clause has already been specified (i.e., this
     *                               method has already been invoked 1 time)
     * @see #andColumn(String)
     * @see #orColumn(String)
     */
    @Nonnull
    public ExpressionBuilder whereColumn(@Nonnull final String column) {
        if (builder.length() > 1 || builder.length() == 1 && builder.charAt(0) != '(')
            throw new IllegalStateException("The first column of WHERE clause has already been specified");

        valid = false;

        builder.append(getEscaped(column)).append(' ');
        return new ExpressionBuilder();
    }

    /**
     * Adds a Boolean expression to the WHERE clause, concatenating it to those already present with the logical
     * AND operator.
     *
     * @param column Target column, eventually with the table identifier (i.e. table.column)
     *
     * @return A new Boolean expression builder
     *
     * @throws IllegalStateException If the first column of the WHERE clause has not yet been specified (i.e., the
     *                               {@link #whereColumn(String)} method has never been called) or if you are
     *                               trying to call this method or {@link #orColumn(String)} again before you have
     *                               specified a Boolean expression
     * @see #whereColumn(String)
     * @see #orColumn(String)
     */
    @Nonnull
    public ExpressionBuilder andColumn(@Nonnull final String column) {
        if (builder.length() == 0)
            throw new IllegalStateException("The first column of the WHERE clause has not yet been specified");
        if (!valid)
            throw new IllegalStateException("You must first specify a Boolean expression for the current column");

        valid = false;

        builder.append(" AND ").append(getEscaped(column)).append(' ');
        return new ExpressionBuilder();
    }

    /**
     * Adds a Boolean expression to the WHERE clause, concatenating it to those already present with the logical OR
     * operator.
     *
     * @param column Target column, eventually with the table identifier (i.e. table.column)
     *
     * @return A new Boolean expression builder
     *
     * @throws IllegalStateException If the first column of the WHERE clause has not yet been specified (i.e., the
     *                               {@link #whereColumn(String)} method has never been called) or if you are
     *                               trying to call this method or {@link #andColumn(String)} again before you have
     *                               specified a Boolean expression
     * @see #whereColumn(String)
     * @see #andColumn(String)
     */
    @Nonnull
    public ExpressionBuilder orColumn(@Nonnull final String column) {
        if (builder.length() == 0)
            throw new IllegalStateException("The first column of the WHERE clause has not yet been specified");
        if (!valid)
            throw new IllegalStateException("You must first specify a Boolean expression for the current column");

        valid = false;

        builder.append(" OR ").append(getEscaped(column)).append(' ');
        return new ExpressionBuilder();
    }

    /**
     * Adds an open round bracket '(', useful for grouping multiple expressions together
     *
     * @return This builder
     *
     * @see #closeParenthesis()
     */
    @Nonnull
    public WhereClauseBuilder openParenthesis() {
        openParentheses++;

        builder.append("(");
        return this;
    }

    /**
     * Adds a closed round bracket ')', useful for grouping multiple expressions together
     *
     * @return This builder
     *
     * @throws IllegalStateException If you call this method without any open parentheses (with the {@link
     *                               #openParenthesis()} method)
     * @see #openParenthesis()
     */
    @Nonnull
    public WhereClauseBuilder closeParenthesis() {
        if (openParentheses == 0)
            throw new IllegalStateException("You cannot insert a closed parenthesis without there being at least one open parenthesis");

        openParentheses--;

        builder.append(")");
        return this;
    }

    /**
     * Reset WHERE clause
     *
     * @return This builder
     */
    @Nonnull
    public WhereClauseBuilder clear() {
        builder.setLength(0);
        valid = false;
        openParentheses = 0;
        return this;
    }

    /**
     * @return The final condition (without the keyword WHERE)
     *
     * @throws IllegalStateException This exception is raised in the following cases:
     *                               <ol>
     *                                  <li>The WHERE clause is not complete</li>
     *                                  <li>If not all open parentheses have been closed</li>
     *                               </ol>
     *
     *                               <p>The first case can happen if you have not specified even a column (i.e.,
     *                               you have never called the {@link #whereColumn(String)} method) or if you have
     *                               not specified the Boolean expression related to a column (i.e., when you
     *                               called the {@link #whereColumn(String)}, {@link #andColumn(String)} or {@link
     *                               #orColumn(String)} methods but no method on the {@link ExpressionBuilder}
     *                               returned by these 3 methods).</p>
     *                               <p>The second case can occur if you call more {@link #openParenthesis()}
     *                               than {@link #closeParenthesis()}.</p>
     */
    @Override
    public String toString() {
        if (!valid)
            throw new IllegalStateException("WHERE clause incomplete");
        if (openParentheses != 0)
            throw new IllegalStateException("Incorrect number of open and closed parentheses.");

        return builder.toString();
    }

    /**
     * @param item String to add escape characters to
     *
     * @return The string passed with escape characters at the beginning and end
     *
     * @see QueryBuilderBase#ESCAPE_CHARACTER
     */
    @Nonnull
    private String getEscaped(@Nonnull final String item) {
        return QueryBuilderBase.ESCAPE_CHARACTER + item + QueryBuilderBase.ESCAPE_CHARACTER;
    }

    /**
     * Represents a column that is the target of a Boolean expression that the WHERE clause will evaluate. To
     * instantiate this class you have to pass from the methods of {@link WhereClauseBuilder}.
     *
     * The use of this class should be as follows:
     * <ol>
     *     <li>Specify a target column with one of these 3 methods: {@link #whereColumn(String)}, {@link #orColumn(String)}, {@link #andColumn(String)}</li>
     *     <li>Specify the Boolean expression related to the target column by calling a method of {@code this} class</li>
     * </ol>
     * Trying to call the methods of {@code this} class multiple times without first specifying a column results in
     * an {@link IllegalStateException}.
     */
    public final class ExpressionBuilder {

        /**
         * To instantiate this class you have to pass from the methods of {@link WhereClauseBuilder}
         */
        private ExpressionBuilder() { }

        /**
         * Specifies that the Boolean expression consists of an equality (=)
         *
         * @param value Value to which the column should correspond
         *
         * @return WHERE clause builder
         *
         * @throws IllegalStateException When trying to insert a Boolean expression without a target column by
         *                               calling the {@link ExpressionBuilder} methods consecutively. To specify a
         *                               target column you must first call one of these methods: {@link
         *                               #whereColumn(String)}, {@link #orColumn(String)} or {@link
         *                               #andColumn(String)}
         */
        @Nonnull
        public WhereClauseBuilder equalsTo(@Nonnull final String value) {
            if (valid)
                throw new IllegalStateException("Before inserting the Boolean expression, the target column must be specified");

            builder.append("= ").append(getQuoted(value));
            valid = true;
            return WhereClauseBuilder.this;
        }

        /**
         * Converts {@code value} to {@code String} and calls the {@link #equalsTo(String)} method
         *
         * @see #equalsTo(String)
         */
        @Nonnull
        public WhereClauseBuilder equalsTo(final int value) {
            return equalsTo(Integer.toString(value));
        }

        /**
         * Specifies that the Boolean expression consists of a non-equality ({@code <>})
         *
         * @param value Value to which the column should not match
         *
         * @return WHERE clause builder
         *
         * @throws IllegalStateException When trying to insert a Boolean expression without a target column by
         *                               calling the {@link ExpressionBuilder} methods consecutively. To specify a
         *                               target column you must first call one of these methods: {@link
         *                               #whereColumn(String)}, {@link #orColumn(String)} or {@link
         *                               #andColumn(String)}
         */
        @Nonnull
        public WhereClauseBuilder differsFrom(@Nonnull final String value) {
            if (valid)
                throw new IllegalStateException("Before inserting the Boolean expression, the target column must be specified");

            builder.append("<> ").append(getQuoted(value));
            valid = true;
            return WhereClauseBuilder.this;
        }

        /**
         * Converts {@code value} to {@code String} and calls the {@link #differsFrom(String)} method
         *
         * @see #differsFrom(String)
         */
        @Nonnull
        public WhereClauseBuilder differsFrom(final int value) {
            return differsFrom(Integer.toString(value));
        }

        /**
         * Specifies that the Boolean expression consists of an inequality (with the relational symbol >)
         *
         * @param value Value with which to compare the column
         *
         * @return WHERE clause builder
         *
         * @throws IllegalStateException When trying to insert a Boolean expression without a target column by
         *                               calling the {@link ExpressionBuilder} methods consecutively. To specify a
         *                               target column you must first call one of these methods: {@link
         *                               #whereColumn(String)}, {@link #orColumn(String)} or {@link
         *                               #andColumn(String)}
         */
        @Nonnull
        public WhereClauseBuilder largerThan(@Nonnull final String value) {
            if (valid)
                throw new IllegalStateException("Before inserting the Boolean expression, the target column must be specified");

            builder.append("> ").append(getQuoted(value));
            valid = true;
            return WhereClauseBuilder.this;
        }

        /**
         * Converts {@code value} to {@code String} and calls the {@link #largerThan(String)} method
         *
         * @see #largerThan(String)
         */
        @Nonnull
        public WhereClauseBuilder largerThan(final int value) {
            return largerThan(Integer.toString(value));
        }

        /**
         * Specifies that the Boolean expression consists of an inequality (with the relational symbol <)
         *
         * @param value Value with which to compare the column
         *
         * @return WHERE clause builder
         *
         * @throws IllegalStateException When trying to insert a Boolean expression without a target column by
         *                               calling the {@link ExpressionBuilder} methods consecutively. To specify a
         *                               target column you must first call one of these methods: {@link
         *                               #whereColumn(String)}, {@link #orColumn(String)} or {@link
         *                               #andColumn(String)}
         */
        @Nonnull
        public WhereClauseBuilder lessThan(@Nonnull final String value) {
            if (valid)
                throw new IllegalStateException("Before inserting the Boolean expression, the target column must be specified");

            builder.append("< ").append(getQuoted(value));
            valid = true;
            return WhereClauseBuilder.this;
        }

        /**
         * Converts {@code value} to {@code String} and calls the {@link #lessThan(String)} method
         *
         * @see #lessThan(String)
         */
        @Nonnull
        public WhereClauseBuilder lessThan(final int value) {
            return lessThan(Integer.toString(value));
        }

        /**
         * Specifies that the Boolean expression consists of an inequality (with the relational symbol >=)
         *
         * @param value Value with which to compare the column
         *
         * @return WHERE clause builder
         *
         * @throws IllegalStateException When trying to insert a Boolean expression without a target column by
         *                               calling the {@link ExpressionBuilder} methods consecutively. To specify a
         *                               target column you must first call one of these methods: {@link
         *                               #whereColumn(String)}, {@link #orColumn(String)} or {@link
         *                               #andColumn(String)}
         */
        @Nonnull
        public WhereClauseBuilder largerThanOrEqualTo(@Nonnull final String value) {
            if (valid)
                throw new IllegalStateException("Before inserting the Boolean expression, the target column must be specified");

            builder.append(">= ").append(getQuoted(value));
            valid = true;
            return WhereClauseBuilder.this;
        }

        /**
         * Converts {@code value} to {@code String} and calls the {@link #largerThanOrEqualTo(String)} method
         *
         * @see #largerThanOrEqualTo(String)
         */
        @Nonnull
        public WhereClauseBuilder largerThanOrEqualTo(final int value) {
            return largerThan(Integer.toString(value));
        }

        /**
         * Specifies that the Boolean expression consists of an inequality (with the relational symbol <=)
         *
         * @param value Value with which to compare the column
         *
         * @return WHERE clause builder
         *
         * @throws IllegalStateException When trying to insert a Boolean expression without a target column by
         *                               calling the {@link ExpressionBuilder} methods consecutively. To specify a
         *                               target column you must first call one of these methods: {@link
         *                               #whereColumn(String)}, {@link #orColumn(String)} or {@link
         *                               #andColumn(String)}
         */
        @Nonnull
        public WhereClauseBuilder lessThanOrEqualTo(@Nonnull final String value) {
            if (valid)
                throw new IllegalStateException("Before inserting the Boolean expression, the target column must be specified");

            builder.append("<= ").append(getQuoted(value));
            valid = true;
            return WhereClauseBuilder.this;
        }

        /**
         * Converts {@code value} to {@code String} and calls the {@link #lessThanOrEqualTo(String)} method
         *
         * @see #lessThanOrEqualTo(String)
         */
        @Nonnull
        public WhereClauseBuilder lessThanOrEqualTo(final int value) {
            return lessThanOrEqualTo(Integer.toString(value));
        }

        /**
         * Specifies that the Boolean expression consists of the non-nullity of the column (IS NOT NULL operator)
         *
         * @return WHERE clause builder
         *
         * @throws IllegalStateException When trying to insert a Boolean expression without a target column by
         *                               calling the {@link ExpressionBuilder} methods consecutively. To specify a
         *                               target column you must first call one of these methods: {@link
         *                               #whereColumn(String)}, {@link #orColumn(String)} or {@link
         *                               #andColumn(String)}
         */
        @Nonnull
        public WhereClauseBuilder isNotNull() {
            if (valid)
                throw new IllegalStateException("Before inserting the Boolean expression, the target column must be specified");

            builder.append("IS NOT NULL");
            valid = true;
            return WhereClauseBuilder.this;
        }

        /**
         * @param item String to which single quote characters should be added
         *
         * @return The string passed with single quote character at the beginning and end
         *
         * @see QueryBuilderBase#SINGLE_QUOTE_CHARACTER
         */
        @Nonnull
        private String getQuoted(@Nonnull final String item) {
            return QueryBuilderBase.SINGLE_QUOTE_CHARACTER + item + QueryBuilderBase.SINGLE_QUOTE_CHARACTER;
        }
    }
}
