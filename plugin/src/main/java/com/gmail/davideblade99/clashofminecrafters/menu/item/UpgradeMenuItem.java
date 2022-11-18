/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.setting.bean.BuildingSettings;

import javax.annotation.Nonnull;

public abstract class UpgradeMenuItem extends BaseItem {

    final CoM plugin;
    final BuildingSettings nextBuilding;

    /**
     * Creates a new instance of the class
     *
     * @param plugin       Plugin instance
     * @param nextBuilding Next level building (the one to be unlocked)
     * @param slot         Slot in which to place the item
     *
     * @since v3.1.2
     */
    public UpgradeMenuItem(@Nonnull final CoM plugin, @Nonnull final BuildingSettings nextBuilding, final byte slot) {
        super(nextBuilding.getItem(plugin), slot);

        this.plugin = plugin;
        this.nextBuilding = nextBuilding;
    }

    //TODO: interfaccia Upgradeable: shouldBePasted() -> true per il 1° livello; int firstLevel() -> restituisce il primo livello (es. 1 per town hall, 0 per gli altri)
}
