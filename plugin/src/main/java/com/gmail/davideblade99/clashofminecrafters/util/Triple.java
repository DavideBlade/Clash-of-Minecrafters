/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * A triple consisting of three elements.
 *
 * @param <A> the first element type
 * @param <B> the second element type
 * @param <C> the third element type
 */
public final class Triple<A, B, C> implements Serializable {

    private static final long serialVersionUID = -8348715848810446785L;

    private final A a;
    private final B b;
    private final C c;

    public Triple(@Nullable final A a, @Nullable final B b, @Nullable final C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Contract(pure = true)
    @Nullable
    public A getA() {
        return a;
    }

    @Contract(pure = true)
    @Nullable
    public B getB() {
        return b;
    }

    @Contract(pure = true)
    @Nullable
    public C getC() {
        return c;
    }

    /**
     * Return a {@code String} representation of this triple using the format ({@link #a},{@link #b},{@link #c}).
     *
     * @return a {@code String} describing this {@code Triple}
     */
    @Override
    public String toString() {
        return "(" + a + "," + b + "," + c + ")";
    }

    /**
     * Return an appropriate hash code, considering the order of the elements.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        /*
         * Multiply by the prime numbers 13 and 31 to distinguish cases
         * with equal elements but in different order.
         * Example: (a,a,b) != (a,b,a)
         */
        return (Objects.hashCode(a) * 31) ^ (Objects.hashCode(b) * 13) ^ Objects.hashCode(c);
    }

    /**
     * Compares this triple to another based on the three elements.
     * If the {@code Object} to be tested is not a {@code Triple} or is {@code null},
     * then this method returns {@code false}.
     * Two {@code Triple}s are considered equal if and only if all 3 elements are equal.
     *
     * @param obj the {@code Object} to compare to, {@code null} returns false
     *
     * @return {@code true} if the elements of the triple are equal, otherwise {@code false}
     */
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Triple))
            return false;

        final Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
        return Objects.equals(a, other.a) && Objects.equals(b, other.b) && Objects.equals(c, other.c);
    }
}