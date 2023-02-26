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
 * Represents the item used to upgrade gold extractors
 *
 * @since v3.1.2
 */
public final class GoldExtractorUpgradeItem extends UpgradeMenuItem {

    /**
     * {@inheritDoc}
     */
    public GoldExtractorUpgradeItem(@Nonnull final CoM plugin, @Nonnull final BuildingLevel nextBuilding, final byte slot) {
        super(plugin, nextBuilding, slot);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException If the gold extractor is already at the maximum level or if the player does
     *                               not own a village (and therefore there is no place to place the building)
     */
    @Override
    public void onClick(@Nonnull final CoM plugin, @Nonnull final Player clicker) {
        plugin.getUpgradeManager().upgrade(clicker, Buildings.GOLD_EXTRACTOR);
    }
}
