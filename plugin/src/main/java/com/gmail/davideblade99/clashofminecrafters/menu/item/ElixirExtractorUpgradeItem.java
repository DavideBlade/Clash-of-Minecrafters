/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.setting.ConfiguredBuilding;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Represents the item used to upgrade elixir extractors
 *
 * @since 3.1.2
 */
public final class ElixirExtractorUpgradeItem extends UpgradeMenuItem {

    /**
     * {@inheritDoc}
     */
    public ElixirExtractorUpgradeItem(@Nonnull final CoM plugin, @Nonnull final ConfiguredBuilding nextBuilding, final byte slot) {
        super(plugin, nextBuilding, slot);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException If the player does not own a village (and therefore there is no place to paste the
     *                               building)
     */
    @Override
    public void onClick(@Nonnull final CoM plugin, @Nonnull final Player clicker) {
        plugin.getUpgradeManager().upgradeElixirExtractor(clicker, clicker.getLocation());
    }
}
