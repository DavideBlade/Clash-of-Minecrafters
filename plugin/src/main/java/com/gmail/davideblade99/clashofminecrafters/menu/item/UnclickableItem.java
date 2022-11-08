/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * Item that does not perform any action on click.
 * In other words, it is only aesthetic.
 */
public final class UnclickableItem extends BaseItem {

    public UnclickableItem(@Nonnull final ItemStack item, final byte slot) {
        super(item, slot);
    }

    @Override
    public void onClick(@Nonnull final CoM plugin, @Nonnull final Player clicker) {}
}
