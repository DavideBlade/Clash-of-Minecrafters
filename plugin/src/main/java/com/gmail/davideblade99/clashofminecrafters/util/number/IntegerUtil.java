/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.number;

import com.gmail.davideblade99.clashofminecrafters.exception.IntegerOutOfBoundary;

import java.math.BigInteger;

public final class IntegerUtil {

    private IntegerUtil() {
        throw new IllegalAccessError();
    }

    /**
     * Provides the same functionality as Integer.parseInt(String s),
     * but throws a custom exception for out-of-range inputs
     *
     * @param str String to parse
     *
     * @throws IntegerOutOfBoundary  If the string contains an integer which cannot be represented by an int
     * @throws NumberFormatException If the string does not contain a parsable integer
     */
    public static int parseIntWithOverflow(final String str) throws IntegerOutOfBoundary, NumberFormatException {
        int result;
        try {
            result = Integer.parseInt(str);
        }
        catch (final Exception e) {
            try {
                new BigInteger(str);
            }
            catch (final Exception ignored) {
                throw e; // re-throw, this was a formatting problem
            }

            // This point is reached if "str" represents a valid integer
            // that's outside of java.lang.Integer range
            throw new IntegerOutOfBoundary("Input is outside of Integer range!");
        }

        // Input parsed without problems
        return result;
    }

    /**
     * Performs a sum without exceeding {@link Integer#MAX_VALUE} and {@link Integer#MIN_VALUE}.
     *
     * @param a First addend
     * @param b Second addend
     *
     * @return the result of the sum, unless it underflows or overflows, in which case
     * {@link Integer#MIN_VALUE} or {@link Integer#MAX_VALUE} is returned, respectively.
     */
    public static int saturatedAdd(final int a, final int b) {
        final long sum = (long) a + b;
        if (sum > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        if (sum < Integer.MIN_VALUE)
            return Integer.MIN_VALUE;
        return (int) sum;
    }

    /**
     * Performs a subtraction without exceeding {@link Integer#MAX_VALUE} and {@link Integer#MIN_VALUE}.
     *
     * @param a Minuend
     * @param b Subtrahend
     *
     * @return the result of the difference, unless it would overflow or underflow, in which case
     * {@link Integer#MAX_VALUE} or {@link Integer#MIN_VALUE} is returned, respectively.
     */
    public static int saturatedSub(final int a, final int b) {
        final long sub = (long) a - b;
        if (sub > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        if (sub < Integer.MIN_VALUE)
            return Integer.MIN_VALUE;
        return (int) sub;
    }

    /**
     * Round up {@code num} to the nearest multiple of {@code multipleOf}
     *
     * @param num        integer to round up
     * @param multipleOf multiple to round up to
     *
     * @return the number rounded up
     */
    public static int roundUpToMultiple(final int num, final int multipleOf) {
        if (multipleOf == 0)
            return num;

        return num >= 0 ? ((num + (multipleOf - 1)) / multipleOf * multipleOf) : ((num / multipleOf) * multipleOf);
    }
}
