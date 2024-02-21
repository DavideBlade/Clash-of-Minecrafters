/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Represents a level of the town hall, configured in the config.yml
 *
 * @author DavideBlade
 * @since 3.1.4
 */
@Immutable
public final class TownHallLevel extends BuildingLevel {

    /** Command to be executed when the player purchases this level */
    public final String command;

    /* Guardian stats */

    /**
     * Number of guardian hearts
     */
    public final byte hearts;

    /**
     * Creates a new level for the town hall with the specified parameters with the default guardian (without extra equipment
     * or hearts) and without any schematic
     *
     * @param level    Level of the town hall, greater than 0
     * @param price    Cost of building for this {@code level}
     * @param currency {@code price} currency
     * @param command  Command to be executed when {@code this} level is reached
     *
     * @see #TownHallLevel(int, int, Currencies, String, byte)
     */
    public TownHallLevel(final int level, final int price, @Nonnull final Currencies currency, @Nullable final String command) {
        this(level, price, currency, command, (byte) 10);
    }

    /**
     * Creates a new level for the town hall with the specified parameters and without any schematic
     *
     * @param level    Level of the town hall, greater than 0
     * @param price    Cost of building for this {@code level}
     * @param currency {@code price} currency
     * @param command  Command to be executed when {@code this} level is reached
     * @param hearts   Number of hearts of the village guardian, greater than 0
     *
     * @see #TownHallLevel(int, int, Currencies, String, byte, String)
     * @since 3.2.2
     */
    public TownHallLevel(final int level, final int price, @Nonnull final Currencies currency, @Nullable final String command, final byte hearts) {
        this(level, price, currency, command, hearts, null);
    }

    /**
     * Creates a new level for the town hall with the specified parameters
     *
     * @param level     Level of the town hall, greater than 0
     * @param price     Cost of building for this {@code level}
     * @param currency  {@code price} currency
     * @param command   Command to be executed when {@code this} level is reached
     * @param hearts    Number of hearts of the village guardian, greater than 0
     * @param schematic Schematic for this {@code level}, which will be pasted at the time of purchase. {@code Null} if this
     *                  {@code level} does not have a schematic.
     *
     * @throws IllegalArgumentException If the number of hearts is less than or equal to 0
     * @since 3.2.2
     */
    public TownHallLevel(final int level, final int price, @Nonnull final Currencies currency, @Nullable final String command, final byte hearts, @Nullable final String schematic) {
        super(level, price, currency, schematic);

        if (hearts <= 0)
            throw new IllegalArgumentException("Invalid health: '" + hearts + "' is not a positive number");

        this.command = command;
        this.hearts = hearts;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public ItemStack getItem(@Nonnull final CoM plugin) {
        final ItemBuilder itemBuilder = new ItemBuilder(Material.EMERALD).setName("&4&lTown Hall");

        itemBuilder.addLoreLine("&7&lLevel:&4 " + super.level);
        itemBuilder.addLoreLine("&7&lPrice:&4 " + super.price + " " + super.currency);

        return itemBuilder.build();
    }
}
