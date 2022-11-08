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
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Class representing the town hall
 *
 * @author DavideBlade
 * @since v3.0.3
 */
public final class TownHall extends Building {

    private final String command; //TODO: questo comando privato come fa ad essere eseguito?

    /* Guardian stats */

    /**
     * Number of guardian hearts
     *
     * @since v3.1
     */
    private final byte hearts;

    /**
     * Material of the guardian's helmet
     *
     * @since v3.1
     */
    private final Material helmet;

    /**
     * Material of the guardian's chestplate
     *
     * @since v3.1
     */
    private final Material chestplate;

    /**
     * Material of the guardian's leggings
     *
     * @since v3.1
     */
    private final Material leggings;

    /**
     * Material of the guardian's boots
     *
     * @since v3.1
     */
    private final Material boots;

    /**
     * List of effects to be applied to the guardian
     *
     * @since v3.1
     */
    private final List<PotionEffectType> potions;

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
     */
    public TownHall(final int level, final int price, @Nonnull final Currency currency, @Nullable final String command, final byte hearts, @Nullable final Material helmet, @Nullable final Material chestplate, @Nullable final Material leggings, @Nullable final Material boots, @Nullable final List<PotionEffectType> potions) {
        super(level, price, currency);

        if (level < 2)
            throw new IllegalArgumentException("Invalid level: must be greater than or equal to 2");

        this.command = command;
        this.hearts = hearts;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.potions = potions;
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
