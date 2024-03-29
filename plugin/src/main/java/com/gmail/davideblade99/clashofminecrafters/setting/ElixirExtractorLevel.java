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

/**
 * Represents a level of the elixir extractor, configured in the config.yml
 *
 * @author DavideBlade
 * @since 3.1.4
 */
public final class ElixirExtractorLevel extends ExtractorLevel {

    /**
     * Creates a new level for the elixir extractor with the specified parameters and the default schematic
     * ("ElixirExtractor.schematic"), for level 1 only.
     *
     * @param level      Level of the elixir extractor, greater than 0
     * @param production Production of the elixir extractor
     * @param capacity   Maximum capacity of elixir extractor
     * @param price      Cost of building for this {@code level}
     * @param currency   {@code price} currency
     *
     * @see #ElixirExtractorLevel(int, int, int, int, Currencies, String)
     */
    public ElixirExtractorLevel(final int level, final int production, final int capacity, final int price, @Nonnull final Currencies currency) {
        this(level, production, capacity, price, currency, level == 1 ? "ElixirExtractor" : null);
    }

    /**
     * Creates a new level for the elixir extractor with the specified parameters
     *
     * @param level      Level of the elixir extractor, greater than 0
     * @param production Production of the elixir extractor
     * @param capacity   Maximum capacity of elixir extractor
     * @param price      Cost of building for this {@code level}
     * @param currency   {@code price} currency
     * @param schematic  Schematic for this {@code level}, which will be pasted at the time of purchase. {@code Null} if this
     *                   {@code level} does not have a schematic.
     */
    public ElixirExtractorLevel(final int level, final int production, final int capacity, final int price, @Nonnull final Currencies currency, @Nullable final String schematic) {
        super(level, production, capacity, price, currency, schematic);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public ItemStack getItem(@Nonnull final CoM plugin) {
        final ItemBuilder itemBuilder = new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setName("&5&lElixir extractor");

        itemBuilder.addLoreLine("&3&lLevel:&5 " + super.level);
        itemBuilder.addLoreLine("&3&lPrice:&5 " + super.price + " " + super.currency);
        itemBuilder.addLoreLine("&3&lProduction:&5 " + super.production + " units every hour.");
        itemBuilder.addLoreLine("&3&lCapacity:&5 " + super.capacity);

        return itemBuilder.build();
    }
}
