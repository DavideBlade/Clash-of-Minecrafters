/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.island.building;

import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.menu.Icon;

import javax.annotation.Nonnull;

public abstract class Building implements Icon {

    public final int level;
    public final int price;
    @Nonnull
    public final Currency currency;

    /**
     * Create a building with a {@code level} and a cost ({@code price} and {@code currency})
     *
     * @param level    Building level, greater than 0
     * @param price    Building price
     * @param currency Price currency
     *
     * @throws IllegalArgumentException If the passed level is invalid
     */
    Building(final int level, final int price, @Nonnull final Currency currency) {
        if (level < 1)
            throw new IllegalArgumentException("Invalid level: must be greater than or equal to 1");

        this.level = level;
        this.price = price;
        this.currency = currency;
    }
}