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
 * Abstract class representing the level of an upgradable building (via the /upgrade command)
 *
 * @author DavideBlade
 * @since 3.2
 */
@Immutable
public abstract class BuildingLevel extends ConfiguredBuilding {

    /** Building level */
    public final int level;

    /**
     * Create a building with a {@code level}, a cost ({@code price} and {@code currency}) and a ({@code schematic}) that
     * will be pasted when the level is purchased/unlocked
     *
     * @param level     Building level, greater than 0
     * @param price     Building price
     * @param currency  {@code price} currency
     * @param schematic Name of the building schematic file for this {@code level}. {@code Null} if the current level does
     *                  not have a schematic.
     *
     * @throws IllegalArgumentException @throws IllegalArgumentException If the level is less than or equal to 0
     */
    BuildingLevel(final int level, final int price, @Nonnull final Currencies currency, @Nullable final String schematic) {
        super(price, currency, schematic);

        if (level <= 0)
            throw new IllegalArgumentException("Level '" + level + "' is invalid: it must be positive");

        this.level = level;
    }
}
