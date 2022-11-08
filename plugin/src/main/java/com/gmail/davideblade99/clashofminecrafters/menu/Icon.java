/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * Represents the elements that can be represented by an {@link ItemStack} shown in a {@link Menu}
 */
public interface Icon {

    /**
     * @param plugin Plugin instance from which to retrieve necessary information (for example, config.yml settings)
     *
     * @return the item representative of the object
     */
    @Nonnull
    ItemStack getItem(@Nonnull final CoM plugin);
}
