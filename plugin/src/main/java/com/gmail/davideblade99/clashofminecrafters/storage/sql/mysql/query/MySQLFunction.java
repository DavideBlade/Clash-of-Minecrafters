package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

import javax.annotation.Nonnull;

/**
 * Enumerate the mathematical functions of MySQL
 */
public enum MySQLFunction {
    RAND("RAND()"),
    ABS("ABS()"),
    CEIL("CEIL()"),
    FLOOR("FLOOR()"),
    ROUND("ROUND()");

    private final String function;

    MySQLFunction(@Nonnull final String function) {
        this.function = function;
    }

    @Override
    public String toString() {
        return function;
    }
}
