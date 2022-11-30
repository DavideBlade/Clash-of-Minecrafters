/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting.bean;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * JavaBean that contains archer tower settings retrieved from config.yml.
 *
 * @author DavideBlade
 * @since v3.1.2
 */
public final class ArcherTowerSettings extends BuildingSettings {

    public final double damage;

    public ArcherTowerSettings(final int level, final double damage, final int price, @Nonnull final Currencies currency) {
        super(level, price, currency);

        this.damage = damage;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public ItemStack getItem(@Nonnull final CoM plugin) {
        final ItemBuilder itemBuilder = new ItemBuilder(Material.BOW).setName("&c&lArcher tower");

        itemBuilder.addLoreLine("&7&lLevel:&c " + super.level);
        itemBuilder.addLoreLine("&7&lPrice:&c " + super.price + " " + super.currency);
        itemBuilder.addLoreLine("&7&lDamage:&c " + this.damage + " hearts.");

        return itemBuilder.build();
    }
}
