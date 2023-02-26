/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting;

import com.gmail.davideblade99.clashofminecrafters.schematic.Pasteable;
import com.gmail.davideblade99.clashofminecrafters.building.Upgradeable;
import com.gmail.davideblade99.clashofminecrafters.menu.Icon;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a level of a building, configured in the config.yml
 *
 * @author DavideBlade
 * @since v3.1.4
 */
public abstract class BuildingLevel implements Icon, Upgradeable, Pasteable {

    public final int level;
    public final int price;
    public final Currencies currency;
    public final String schematic;

    /**
     * Create a building with a {@code level}, a cost ({@code price} and {@code currency}) and a ({@code
     * schematic}) that will be pasted when the building is purchased/unlocked
     *
     * @param level     Building level, greater than 0
     * @param price     Building price
     * @param currency  Price currency
     * @param schematic Name of the building schematic file for this {@code level}. {@code Null} if the current
     *                  level does not have a schematic.
     *
     * @throws IllegalArgumentException If the passed level is invalid (<= 0)
     */
    BuildingLevel(final int level, final int price, @Nonnull final Currencies currency, @Nullable final String schematic) {
        if (level < 1)
            throw new IllegalArgumentException("Invalid level: must be greater than or equal to 1");

        this.level = level;
        this.price = price;
        this.currency = currency;
        this.schematic = schematic;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public final String getRelatedSchematic() {
        return this.schematic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canBePurchased(@Nonnull final User user) {
        return user.getBalance(this.currency) >= this.price;
    }
}