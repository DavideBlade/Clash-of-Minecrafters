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
 * Represents a level of the gold extractor, configured in the config.yml
 *
 * @author DavideBlade
 * @since 3.1.4
 */
public final class GoldExtractorLevel extends ExtractorLevel {

    /**
     * Creates a new level for the gold extractor with the specified parameters and the default schematic
     * ("GoldExtractor.schematic"), for level 1 only.
     *
     * @param level      Level of the gold extractor, greater than 0
     * @param production Production of the gold extractor
     * @param capacity   Maximum capacity of gold extractor
     * @param price      Cost of building for this {@code level}
     * @param currency   {@code price} currency
     *
     * @see #GoldExtractorLevel(int, int, int, int, Currencies, String)
     */
    public GoldExtractorLevel(final int level, final int production, final int capacity, final int price, @Nonnull final Currencies currency) {
        this(level, production, capacity, price, currency, level == 1 ? "GoldExtractor" : null);
    }

    /**
     * Creates a new level for the gold extractor with the specified parameters
     *
     * @param level      Level of the gold extractor
     * @param production Production of the gold extractor
     * @param capacity   Maximum capacity of gold extractor
     * @param price      Cost of building for this {@code level}
     * @param currency   {@code price} currency
     * @param schematic  Schematic for this {@code level}, which will be pasted at the time of purchase. {@code Null} if this
     *                   {@code level} does not have a schematic.
     */
    public GoldExtractorLevel(final int level, final int production, final int capacity, final int price, @Nonnull final Currencies currency, @Nullable final String schematic) {
        super(level, production, capacity, price, currency, schematic);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public ItemStack getItem(@Nonnull final CoM plugin) {
        final ItemBuilder itemBuilder = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setName("&6&lGold extractor");

        itemBuilder.addLoreLine("&c&lLevel:&6 " + super.level);
        itemBuilder.addLoreLine("&c&lPrice:&6 " + super.price + " " + super.currency);
        itemBuilder.addLoreLine("&c&lProduction:&6 " + super.production + " units every hour.");
        itemBuilder.addLoreLine("&c&lCapacity:&6 " + super.capacity);

        return itemBuilder.build();
    }
}
