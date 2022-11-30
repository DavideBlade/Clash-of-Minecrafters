/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting.bean;

import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * JavaBean that contains the extractor shared settings retrieved from config.yml.
 *
 * @author DavideBlade
 * @since v3.1.2
 */
public abstract class ExtractorSettings extends BuildingSettings {

    public final int production;
    public final int capacity;

    /**
     * Creates a new level of an extractor with the specified parameters
     *
     * @param level      Level of the extractor
     * @param production Extractor production
     * @param capacity   Maximum capacity of the extractor
     * @param price      Cost of building for this {@code level}
     * @param currency   {@code price} currency
     * @param schematic  Schematic for this {@code level}, which will be pasted at the time of purchase. {@code
     *                   Null} if this {@code level} does not have a schematic.
     *
     * @since v3.1.4
     */
    public ExtractorSettings(final int level, final int production, final int capacity, final int price, @Nonnull final Currencies currency, @Nullable final String schematic) {
        super(level, price, currency, schematic);

        this.production = production;
        this.capacity = capacity;
    }
}