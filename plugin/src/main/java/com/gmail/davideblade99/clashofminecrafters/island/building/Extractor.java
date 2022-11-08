/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.island.building;

import com.gmail.davideblade99.clashofminecrafters.Currency;

import javax.annotation.Nonnull;

public abstract class Extractor extends Building {

    public final int production;
    public final int capacity;

    public Extractor(final int level, final int production, final int capacity, final int price, @Nonnull final Currency currency) {
        super(level, price, currency);

        this.production = production;
        this.capacity = capacity;
    }
}