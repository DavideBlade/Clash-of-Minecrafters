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
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * JavaBean that contains town hall settings retrieved from config.yml.
 *
 * @author DavideBlade
 * @since v3.1.2
 */
public final class TownHallSettings extends BuildingSettings {

    private final String command; //TODO: questo comando privato come fa ad essere eseguito?

    /* Guardian stats */

    /**
     * Number of guardian hearts
     */
    public final byte hearts;

    /**
     * Material of the guardian's helmet
     */
    public final Material helmet;

    /**
     * Material of the guardian's chestplate
     */
    public final Material chestplate;

    /**
     * Material of the guardian's leggings
     */
    public final Material leggings;

    /**
     * Material of the guardian's boots
     */
    public final Material boots;

    /**
     * (Immutable) list of effects to be applied to the guardian
     */
    public final List<PotionEffectType> potions;

    /**
     * Creates a new level for the town hall with the specified parameters with the default guardian (without extra
     * equipment or hearts) and without any schematic
     *
     * @param level    Level of the town hall
     * @param price    Cost of building for this {@code level}
     * @param currency {@code price} currency
     * @param command  Command to be executed when {@code this} level is reached
     *
     * @see #TownHallSettings(int, int, Currencies, String, byte, Material, Material, Material, Material, List)
     */
    public TownHallSettings(final int level, final int price, @Nonnull final Currencies currency, @Nullable final String command) {
        this(level, price, currency, command, (byte) 10, null, null, null, null, null);
    }

    /**
     * Creates a new level for the town hall with the specified parameters and without any schematic
     *
     * @param level      Level of the town hall
     * @param price      Cost of building for this {@code level}
     * @param currency   {@code price} currency
     * @param command    Command to be executed when {@code this} level is reached
     * @param hearts     Number of hearts of the island guardian
     * @param helmet     Helmet of the island guardian
     * @param chestplate Chestplate of the island guardian
     * @param leggings   Leggings of the island guardian
     * @param boots      Boots of the island guardian
     * @param potions    Effects to apply to the island guardian
     *
     * @see #TownHallSettings(int, int, Currencies, String, byte, Material, Material, Material, Material, List,
     * String)
     * @since v3.1.4
     */
    public TownHallSettings(final int level, final int price, @Nonnull final Currencies currency, @Nullable final String command, final byte hearts, @Nullable final Material helmet, @Nullable final Material chestplate, @Nullable final Material leggings, @Nullable final Material boots, @Nullable final List<PotionEffectType> potions) {
        this(level, price, currency, command, hearts, helmet, chestplate, leggings, boots, potions, null);
    }

    /**
     * Creates a new level for the town hall with the specified parameters
     *
     * @param level      Level of the town hall
     * @param price      Cost of building for this {@code level}
     * @param currency   {@code price} currency
     * @param command    Command to be executed when {@code this} level is reached
     * @param hearts     Number of hearts of the island guardian
     * @param helmet     Helmet of the island guardian
     * @param chestplate Chestplate of the island guardian
     * @param leggings   Leggings of the island guardian
     * @param boots      Boots of the island guardian
     * @param potions    Effects to apply to the island guardian
     * @param schematic  Schematic for this {@code level}, which will be pasted at the time of purchase. {@code
     *                   Null} if this {@code level} does not have a schematic.
     *
     * @throws IllegalArgumentException If the level is less than {@link Buildings#firstLevel} or if the number of
     *                                  hearts is not positive
     * @since v3.1.4
     */
    public TownHallSettings(final int level, final int price, @Nonnull final Currencies currency, @Nullable final String command, final byte hearts, @Nullable final Material helmet, @Nullable final Material chestplate, @Nullable final Material leggings, @Nullable final Material boots, @Nullable final List<PotionEffectType> potions, @Nullable final String schematic) {
        super(level, price, currency, schematic);

        if (level < Buildings.TOWN_HALL.firstLevel)
            throw new IllegalArgumentException("Invalid level: must be greater than or equal to " + Buildings.TOWN_HALL.firstLevel);
        if (hearts <= 0)
            throw new IllegalArgumentException("Invalid health: must be a positive number");

        this.command = command;
        this.hearts = hearts;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.potions = potions == null ? null : ImmutableList.copyOf(potions);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFirstLevel() {
        return super.level == Buildings.TOWN_HALL.firstLevel;
    }
}
