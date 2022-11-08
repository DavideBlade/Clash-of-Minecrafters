/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.island.building;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public final class ElixirExtractor extends Extractor {

    public ElixirExtractor(final int level, final int production, final int capacity, final int price, @Nonnull final Currency currency) {
        super(level, production, capacity, price, currency);
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
