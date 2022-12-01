/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.player.currency;

import com.gmail.davideblade99.clashofminecrafters.util.EnumUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Enumerates all currencies
 *
 * @author DavideBlade
 * @since v3.1.4
 */
public enum Currencies {
    GEMS, GOLD, ELIXIR;

    private final int min;
    private final int max;

    /**
     * Initializes the currency with the maximum and minimum default values it can have ({@link Integer#MAX_VALUE}
     * & 0, respectively)
     */
    Currencies() {
        this.min = 0;
        this.max = Integer.MAX_VALUE;
    }

    /**
     * Initializes the currency with the relative maximums and minimums it can have
     *
     * @param lowerLimit Minimum value that the currency accepts
     * @param upperLimit Maximum value that the currency accepts
     */
    Currencies(final int lowerLimit, final int upperLimit) {
        this.min = lowerLimit;
        this.max = upperLimit;
    }

    /**
     * @return The maximum value that the currency accepts
     */
    public int getMax() {
        return this.max;
    }

    /**
     * @return The minimum value that the currency accepts
     */
    public int getMin() {
        return this.min;
    }

    @Override
    public String toString() {
        final String name = super.name();

        return name.charAt(0) + name.substring(1).toLowerCase(Locale.ENGLISH);
    }

    /**
     * Calculates the amount that can be added to a currency before reaching the upper limit
     *
     * @param currency      The currency of which to check the upper limit
     * @param currentAmount The current amount of the {@code currency}
     *
     * @return The amount that can be added to the specified currency before reaching its upper limit
     */
    public static int addableAmount(@Nonnull final Currencies currency, final int currentAmount) {
        if (currentAmount >= currency.max)
            return 0;

        return currency.max - currentAmount;
    }

    /**
     * Calculates the amount that can be removed to a currency before reaching the lower limit
     *
     * @param currency      The currency of which to check the lower limit
     * @param currentAmount The current amount of the {@code currency}
     *
     * @return The amount that can be removed to the specified currency before reaching its lower limit
     */
    public static int removableAmount(@Nonnull final Currencies currency, final int currentAmount) {
        if (currentAmount <= currency.min)
            return 0;

        return currentAmount - currency.min;
    }

    public static boolean isCorrectCurrency(@Nullable final String currency) {
        return EnumUtil.isInEnumIgnoreCase(currency, Currencies.class);
    }

    @Nullable
    public static Currencies matchCurrency(@Nullable final String currency) {
        return matchOrDefault(currency, null);
    }

    @Nullable
    private static Currencies matchOrDefault(@Nullable final String currency, @Nullable final Currencies def) {
        return EnumUtil.getEnumIgnoreCase(currency, Currencies.class, def);
    }
}
