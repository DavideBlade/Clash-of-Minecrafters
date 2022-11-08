package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query;

/**
 * Enumerate the mathematical functions of MySQL
 */
public enum Function {
    RAND("RAND()"),
    ABS("ABS()"),
    CEIL("CEIL()"),
    FLOOR("FLOOR()"),
    ROUND("ROUND()");

    private final String function;

    Function(final String function) {
        this.function = function;
    }

    @Override
    public String toString() {
        return function;
    }
}
