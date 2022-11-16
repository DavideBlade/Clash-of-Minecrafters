/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting.bean;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * JavaBean that contains gold extractor settings retrieved from config.yml.
 *
 * @author DavideBlade
 * @since v3.1.2
 */
public final class GoldExtractorSettings extends ExtractorSettings {

    public GoldExtractorSettings(final int level, final int production, final int capacity, final int price, @Nonnull final Currency currency) {
        super(level, production, capacity, price, currency);
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
