/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * A convenience class to represent name-value pairs.
 */
public final class Pair<K, V> implements Serializable {

    private static final long serialVersionUID = 7925337691181678465L;

    /**
     * Key of this {@code Pair}
     */
    private final K key;

    /**
     * Value of this {@code Pair}
     */
    private final V value;

    /**
     * Creates a new pair
     *
     * @param key   The key for this pair
     * @param value The value to use for this pair
     */
    public Pair(@Nullable final K key, @Nullable final V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Gets the key for this pair
     *
     * @return key for this pair
     */
    @Contract(pure = true)
    @Nullable
    public K getKey() {
        return key;
    }

    /**
     * Gets the value for this pair
     *
     * @return value for this pair
     */
    @Contract(pure = true)
    @Nullable
    public V getValue() {
        return value;
    }

    /**
     * {@code String} representation of this {@code Pair}. The default name/value delimiter '=' is used.
     *
     * @return {@code String} representation of this {@code Pair}
     */
    @Override
    public String toString() {
        return key + "=" + value;
    }

    /**
     * Generate a hash code for this {@code Pair}. The hash code is calculated using both the name and the value of
     * the {@code Pair}.
     *
     * @return hash code for this {@code Pair}
     */
    @Override
    public int hashCode() {
        // key's hashCode is multiplied by an arbitrary prime number (13)
        // in order to make sure there is a difference in the hashCode between
        // these two parameters:
        //  key: a  value: aa
        //  key: aa value: a
        return (key == null ? 0 : key.hashCode() * 13) + (value == null ? 0 : value.hashCode());
    }

    /**
     * Test this {@code Pair} for equality with another {@code Object}. If the {@code Object} to be tested is not a
     * {@code Pair} or is {@code null}, then this method returns {@code false}. Two {@code Pair}s are considered
     * equal if and only if both the keys and values are equal.
     *
     * @param o the {@code Object} to test for equality with this {@code Pair}
     *
     * @return {@code true} if the given {@code Object} is equal to this {@code Pair} else {@code false}
     */
    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Pair))
            return false;

        final Pair<?, ?> pair = (Pair<?, ?>) o;
        if (key == null ? pair.key != null : !key.equals(pair.key))
            return false;
        if (value == null ? pair.value != null : !value.equals(pair.value))
            return false;
        return true;
    }
}
