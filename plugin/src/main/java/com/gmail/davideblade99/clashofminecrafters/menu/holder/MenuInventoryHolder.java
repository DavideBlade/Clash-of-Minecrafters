/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.holder;

import com.gmail.davideblade99.clashofminecrafters.menu.Menu;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nonnull;

public final class MenuInventoryHolder implements InventoryHolder {

    private final Menu menu;

    public MenuInventoryHolder(@Nonnull final Menu menu) {
        this.menu = menu;
    }

    @Override
    public Inventory getInventory() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public Menu getShop() {
        return menu;
    }
}
