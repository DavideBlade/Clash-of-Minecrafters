/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting;

import com.gmail.davideblade99.clashofminecrafters.menu.Icon;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Represents a purchasable building, configured in the config.yml
 *
 * @author DavideBlade
 * @since 3.2
 */
@Immutable
public abstract class ConfiguredBuilding implements Icon, Pasteable {

    public final int price;
    public final Currencies currency;
    private final String schematic;

    /**
     * Create a building with a cost ({@code price} and {@code currency}) and a ({@code schematic}) that will be pasted when
     * the building is purchased/unlocked
     *
     * @param price     Building price
     * @param currency  Price currency
     * @param schematic Name of the building schematic file for this building. {@code Null} if the building does not have a
     *                  schematic.
     */
    ConfiguredBuilding(final int price, @Nonnull final Currencies currency, @Nullable final String schematic) {
        this.price = price;
        this.currency = currency;
        this.schematic = schematic;
    }

    /**
     * Checks whether the user can afford to purchase the building
     *
     * @return True if he can buy it, otherwise false
     */
    public final boolean canBePurchased(@Nonnull final User user) {
        return user.getBalance(this.currency) >= this.price;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public final String getRelatedSchematic() {
        return this.schematic;
    }
}