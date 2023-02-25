/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.Buildings;
import com.gmail.davideblade99.clashofminecrafters.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.setting.ArcherTowerLevel;
import com.gmail.davideblade99.clashofminecrafters.setting.BuildingLevel;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Represents the item used to upgrade archer towers
 *
 * @author DavideBlade
 * @since v3.1.2
 */
public final class ArcherTowerUpgradeItem extends UpgradeMenuItem {

    /**
     * {@inheritDoc}
     */
    public ArcherTowerUpgradeItem(@Nonnull final CoM plugin, @Nonnull final BuildingLevel nextBuilding, final byte slot) {
        super(plugin, nextBuilding, slot);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException If the archer's tower is already at the maximum level or if the player does
     *                               not own a village (and therefore there is no place to place the building)
     */
    @Override
    public void onClick(@Nonnull final CoM plugin, @Nonnull final Player clicker) {
        final ArcherTowerLevel nextArcherTower = plugin.getConfig().getArcherTower(nextBuilding.level);
        if (nextArcherTower == null)
            throw new IllegalStateException("The archer's tower is already at the highest level. It cannot be upgraded further.");

        final Location origin = clicker.getLocation();
        final User user = plugin.getUser(clicker);

        // Check if player has money to upgrade
        if (!nextArcherTower.canBePurchased(user)) {
            final String currencyTranslation = user.getBalance().getCurrencyTranslation(nextBuilding.currency);

            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            return;
        }


        // If there is a schematic to paste
        final String schematicName = nextArcherTower.getRelatedSchematic();
        if (schematicName != null) {
            final Village village = user.getVillage();
            if (village == null)
                throw new IllegalStateException("User does not own a village, so he cannot purchase buildings");

            // Check if the player is into own island
            if (!village.isInsideVillage(clicker.getLocation())) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.TOWER_NOT_PLACED));
                return;
            }

            final Schematic schematic = loadSchematic(clicker, schematicName);
            if (schematic == null)
                return; // In case of an error, messages to the player are already sent by #loadSchematic()

            // Paste schematic
            schematic.paste(origin, exception -> {
                if (exception != null) { // Paste failed
                    MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.PASTE_ERROR, schematicName));
                    return;
                }

                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.TOWER_PLACED));

                // Save tower position
                user.setArcherPos(new Vector(origin.getBlockX(), (origin.getBlockY() + schematic.getSize().getHeight()), origin.getBlockZ()));
            });
        }

        user.upgradeBuilding(Buildings.ARCHER_TOWER);
    }
}
