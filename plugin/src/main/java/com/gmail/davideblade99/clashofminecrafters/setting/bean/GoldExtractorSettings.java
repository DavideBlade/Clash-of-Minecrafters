/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting.bean;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.Buildings;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * JavaBean that contains gold extractor settings retrieved from config.yml.
 *
 * @author DavideBlade
 * @since v3.1.2
 */
public final class GoldExtractorSettings extends ExtractorSettings {

    /**
     * Creates a new level for the gold extractor with the specified parameters and the default schematic
     * ("GoldExtractor.schematic"), for level 1 only.
     *
     * @param level      Level of the gold extractor
     * @param production Production of the gold extractor
     * @param capacity   Maximum capacity of gold extractor
     * @param price      Cost of building for this {@code level}
     * @param currency   {@code price} currency
     *
     * @see #GoldExtractorSettings(int, int, int, int, Currencies, String)
     */
    public GoldExtractorSettings(final int level, final int production, final int capacity, final int price, @Nonnull final Currencies currency) {
        this(level, production, capacity, price, currency, level == Buildings.GOLD_EXTRACTOR.firstLevel ? "GoldExtractor" : null);
    }

    /**
     * Creates a new level for the gold extractor with the specified parameters
     *
     * @param level      Level of the gold extractor
     * @param production Production of the gold extractor
     * @param capacity   Maximum capacity of gold extractor
     * @param price      Cost of building for this {@code level}
     * @param currency   {@code price} currency
     * @param schematic  Schematic for this {@code level}, which will be pasted at the time of purchase. {@code
     *                   Null} if this {@code level} does not have a schematic.
     *
     * @throws IllegalArgumentException If the level is less than {@link Buildings#firstLevel}
     * @since v3.1.4
     */
    public GoldExtractorSettings(final int level, final int production, final int capacity, final int price, @Nonnull final Currencies currency, @Nullable final String schematic) {
        super(level, production, capacity, price, currency, schematic);

        if (level < Buildings.GOLD_EXTRACTOR.firstLevel)
            throw new IllegalArgumentException("Invalid level: must be greater than or equal to " + Buildings.GOLD_EXTRACTOR.firstLevel);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFirstLevel() {
        return super.level == Buildings.GOLD_EXTRACTOR.firstLevel;
    }
}
