/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.Buildings;
import com.gmail.davideblade99.clashofminecrafters.setting.BuildingLevel;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Represents the item used to upgrade town halls
 *
 * @since v3.1.2
 */
public final class TownHallUpgradeItem extends UpgradeMenuItem {

    /**
     * {@inheritDoc}
     */
    public TownHallUpgradeItem(@Nonnull final CoM plugin, @Nonnull final BuildingLevel nextBuilding, final byte slot) {
        super(plugin, nextBuilding, slot);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException If the town hall is already at the maximum level
     */
    @Override
    public void onClick(@Nonnull final CoM plugin, @Nonnull final Player clicker) {
        plugin.getUpgradeManager().upgrade(clicker, Buildings.TOWN_HALL);
    }
}
