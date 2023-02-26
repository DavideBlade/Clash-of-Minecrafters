/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.Buildings;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a level of the archer tower, configured in the config.yml
 *
 * @author DavideBlade
 * @since v3.1.4
 */
public final class ArcherTowerLevel extends BuildingLevel {

    public final double damage;

    /**
     * Creates a new level for the archer's tower with the specified parameters and the default schematic
     * ("ArcherTower.schematic"), for level 1 only.
     *
     * @param level    Level of the archer's tower
     * @param damage   Archer's damage
     * @param price    Cost of building for this {@code level}
     * @param currency {@code price} currency
     *
     * @see #ArcherTowerLevel(int, double, int, Currencies, String)
     */
    public ArcherTowerLevel(final int level, final double damage, final int price, @Nonnull final Currencies currency) {
        this(level, damage, price, currency, level == Buildings.ARCHER_TOWER.firstLevel+1 ? "ArcherTower" : null);
    }

    /**
     * Creates a new level for the archer's tower with the specified parameters
     *
     * @param level     Level of the archer's tower
     * @param damage    Archer's damage
     * @param price     Cost of building for this {@code level}
     * @param currency  {@code price} currency
     * @param schematic Schematic for this {@code level}, which will be pasted at the time of purchase. {@code
     *                  Null} if this {@code level} does not have a schematic.
     *
     * @throws IllegalArgumentException If the level is less than {@link Buildings#firstLevel}
     */
    public ArcherTowerLevel(final int level, final double damage, final int price, @Nonnull final Currencies currency, @Nullable final String schematic) {
        super(level, price, currency, schematic);

        if (level < Buildings.ARCHER_TOWER.firstLevel)
            throw new IllegalArgumentException("Invalid level: must be greater than or equal to " + Buildings.ARCHER_TOWER.firstLevel);

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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFirstLevel() {
        return super.level == Buildings.ARCHER_TOWER.firstLevel;
    }
}
