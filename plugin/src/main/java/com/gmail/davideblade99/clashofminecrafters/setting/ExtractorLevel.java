/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting;

import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Represents a level of an extractor, configured in the config.yml
 *
 * @author DavideBlade
 * @since 3.1.4
 */
@Immutable
public abstract class ExtractorLevel extends BuildingLevel {

    public final int production;
    public final int capacity;

    /**
     * Creates a new level of an extractor with the specified parameters
     *
     * @param level      Level of the extractor, greater than 0
     * @param production Extractor production
     * @param capacity   Maximum capacity of the extractor
     * @param price      Cost of building for this {@code level}
     * @param currency   {@code price} currency
     * @param schematic  Schematic for this {@code level}, which will be pasted at the time of purchase. {@code Null} if this
     *                   {@code level} does not have a schematic.
     */
    public ExtractorLevel(final int level, final int production, final int capacity, final int price, @Nonnull final Currencies currency, @Nullable final String schematic) {
        super(level, price, currency, schematic);

        this.production = production;
        this.capacity = capacity;
    }
}