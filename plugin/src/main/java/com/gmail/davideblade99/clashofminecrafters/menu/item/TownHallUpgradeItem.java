/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.Buildings;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.setting.BuildingLevel;
import com.gmail.davideblade99.clashofminecrafters.setting.TownHallLevel;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
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
        final TownHallLevel nextTownHall = plugin.getConfig().getTownHall(nextBuilding.level);
        if (nextTownHall == null)
            throw new IllegalStateException("The town hall is already at the highest level. It cannot be upgraded further.");

        final User user = plugin.getUser(clicker);

        // Check if player has money to upgrade
        if (!nextTownHall.canBePurchased(user)) {
            final String currencyTranslation = user.getBalance().getCurrencyTranslation(nextBuilding.currency);

            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            return;
        }

        user.upgradeBuilding(Buildings.TOWN_HALL);
    }
}
