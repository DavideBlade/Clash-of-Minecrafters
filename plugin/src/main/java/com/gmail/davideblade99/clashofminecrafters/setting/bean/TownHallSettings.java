/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting.bean;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
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
     *
     * @since v3.1
     */
    public final byte hearts;

    /**
     * Material of the guardian's helmet
     *
     * @since v3.1
     */
    public final Material helmet;

    /**
     * Material of the guardian's chestplate
     *
     * @since v3.1
     */
    public final Material chestplate;

    /**
     * Material of the guardian's leggings
     *
     * @since v3.1
     */
    public final Material leggings;

    /**
     * Material of the guardian's boots
     *
     * @since v3.1
     */
    public final Material boots;

    /**
     * (Immutable) list of effects to be applied to the guardian
     *
     * @since v3.1
     */
    public final List<PotionEffectType> potions;

    /**
     * {@inheritDoc}
     *
     * @param command Command to be executed when {@code this} level is reached
     *
     * @throws IllegalArgumentException If the level is less than 2 or if the number of hearts is not positive.
     */
    public TownHallSettings(final int level, final int price, @Nonnull final Currency currency, @Nullable final String command) {
        this(level, price, currency, command, (byte) 10, null, null, null, null, null);
    }

    /**
     * {@inheritDoc}
     *
     * @param command    Command to be executed when {@code this} level is reached
     * @param hearts     Number of hearts of the island guardian
     * @param helmet     Helmet of the island guardian
     * @param chestplate Chestplate of the island guardian
     * @param leggings   Leggings of the island guardian
     * @param boots      Boots of the island guardian
     * @param potions    Effects to apply to the island guardian
     *
     * @throws IllegalArgumentException If the level is less than 2 or if the number of hearts is not positive.
     * @since v3.1
     */
    public TownHallSettings(final int level, final int price, @Nonnull final Currency currency, @Nullable final String command, final byte hearts, @Nullable final Material helmet, @Nullable final Material chestplate, @Nullable final Material leggings, @Nullable final Material boots, @Nullable final List<PotionEffectType> potions) {
        super(level, price, currency);

        if (level < 2)
            throw new IllegalArgumentException("Invalid level: must be greater than or equal to 2");
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
}
