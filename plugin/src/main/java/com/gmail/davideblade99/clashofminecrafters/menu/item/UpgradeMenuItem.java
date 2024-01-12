/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.setting.ConfiguredBuilding;

import javax.annotation.Nonnull;

public abstract class UpgradeMenuItem extends BaseItem {

    final CoM plugin;
    final ConfiguredBuilding nextBuilding;

    /**
     * Creates a new instance of the class
     *
     * @param plugin       Plugin instance
     * @param nextBuilding Next level building (the one to be purchased)
     * @param slot         Slot in which to place the item
     *
     * @since 3.1.2
     */
    public UpgradeMenuItem(@Nonnull final CoM plugin, @Nonnull final ConfiguredBuilding nextBuilding, final byte slot) {
        super(nextBuilding.getItem(plugin), slot);

        this.plugin = plugin;
        this.nextBuilding = nextBuilding;
    }
}
