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
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.setting.BuildingLevel;
import com.gmail.davideblade99.clashofminecrafters.setting.GoldExtractorLevel;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.Location;
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
        final GoldExtractorLevel nextGoldExtractor = plugin.getConfig().getGoldExtractor(nextBuilding.level);
        if (nextGoldExtractor == null)
            throw new IllegalStateException("The gold extractor is already at the highest level. It cannot be upgraded further.");

        final Location origin = clicker.getLocation();
        final User user = plugin.getUser(clicker);

        // Check if player has money to upgrade
        if (!nextGoldExtractor.canBePurchased(user)) {
            final String currencyTranslation = user.getBalance().getCurrencyTranslation(nextBuilding.currency);

            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            return;
        }


        // If there is a schematic to paste
        final String schematicName = nextGoldExtractor.getRelatedSchematic();
        if (schematicName != null) {
            final Village village = user.getVillage();
            if (village == null)
                throw new IllegalStateException("User does not own a village, so he cannot purchase buildings");

            // Check if the player is into own island
            if (!village.isInsideVillage(clicker.getLocation())) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.EXTRACTOR_NOT_PLACED));
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

                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.EXTRACTOR_PLACED));
            });
        }

        /*
         * Before increasing the level, collect what the extractors were storing.
         * This prevents calculation of the extractor's production from the last collection
         * with the parameters of the next level.
         * Example:
         * the player collected 10 hours ago and his extractor produces 100 units per hour.
         * So it has produced 1000 units so far. The next level produces 200 units per hour.
         * If collecting is not done now, 2000 units would be the result when computing the resources produced.
         */
        user.collectExtractors();
        /*
         * Notifies resource collection only if the player already had an extractor
         * (if he is buying the first one, there is nothing to collect)
         */
        if (user.hasBuilding(Buildings.GOLD_EXTRACTOR))
            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.COLLECTED_RESOURCES));

        user.upgradeBuilding(Buildings.GOLD_EXTRACTOR);
    }
}
