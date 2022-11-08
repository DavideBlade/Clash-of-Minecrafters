/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.bukkit;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class InventoryUtil {

    private InventoryUtil() {
        throw new IllegalAccessError();
    }

    /**
     * Returns the first slot in the inventory containing an ItemStack. This will only match a slot if the type match.
     *
     * @param inv  - The Inventory where specified item should be sought
     * @param item - The ItemStack to match into specified inventory
     *
     * @return The slot index of the given ItemStack or -1 if not found
     */
    public static int first(final Inventory inv, final ItemStack item) {
        final ItemStack[] inventory = inv.getContents();
        int i = 0;

        while (true) {
            if (i >= inventory.length)
                return -1;

            if (inventory[i] != null && item.isSimilar(inventory[i]))
                break;

            i++;
        }

        return i;
    }
}