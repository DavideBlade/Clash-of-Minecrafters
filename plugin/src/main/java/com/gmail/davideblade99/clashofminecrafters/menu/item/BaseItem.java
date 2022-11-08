/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.item;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class BaseItem extends ItemStack implements ActionListener {

    private final byte slot;

    public BaseItem(@Nonnull final ItemStack item, final byte slot) {
        super(item);

        this.slot = slot;
    }

    public final byte getSlot() {
        return slot;
    }
}
