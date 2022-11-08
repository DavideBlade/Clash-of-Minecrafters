/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu;

import com.gmail.davideblade99.clashofminecrafters.menu.holder.MenuInventoryHolder;
import com.gmail.davideblade99.clashofminecrafters.menu.item.BaseItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Menu {

    private final String title;
    private final byte size;
    private final List<BaseItem> items;

    public Menu(@Nullable final String title, final byte size, @Nullable final List<BaseItem> items) {
        this.title = title == null ? "" : title;
        this.size = size;
        this.items = items == null ? new ArrayList<>() : items;
    }

    @Nonnull
    public final List<BaseItem> getItems() {
        return items;
    }

    @Nonnull
    public final Inventory getInventory() {
        final Inventory inventory = Bukkit.createInventory(new MenuInventoryHolder(this), size, title);

        for (BaseItem item : items) {
            if (inventory.getItem(item.getSlot()) != null) // If more than one item has been set in the same slot, show only the first one
                continue;

            // If there are heads, with setItem() the server reaches the Mojang server and downloads the skin
            inventory.setItem(item.getSlot(), item);
        }

        return inventory;
    }
}
