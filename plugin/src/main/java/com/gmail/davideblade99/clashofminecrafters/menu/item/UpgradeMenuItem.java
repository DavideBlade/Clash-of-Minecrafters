/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.configuration.Config;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.island.building.BuildingType;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public final class UpgradeMenuItem extends BaseItem {

    private final BuildingType type;

    public UpgradeMenuItem(@Nonnull final ItemStack item, final byte slot, @Nonnull final BuildingType type) {
        super(item, slot);

        this.type = type;
    }

    @Override
    public void onClick(@Nonnull final CoM plugin, @Nonnull final Player clicker) {
        final Config config = plugin.getConfig();
        final World islandWorld = Bukkit.getWorld("Islands");
        final Vector origin = new Vector(clicker.getLocation());
        final User user = plugin.getUser(clicker);

        /*
         * If any player has a level higher than the current maximum level
         * (e.g., some levels have been removed from the config.yml),
         * they are treated as if they have not purchased any upgrades
         * and must therefore start over.
         */
        int currentLevel = user.getBuildingLevel(type);
        if (currentLevel > config.getMaxLevel(type))
            currentLevel = 0;

        // Check if player have money to upgrade
        final int nextLevel = currentLevel + 1;
        if (!user.hasMoneyToUpgrade(nextLevel, type)) {
            final Currency currency = config.getBuilding(type, nextLevel).currency;
            final String currencyTranslation;
            switch (currency) {
                case GEMS:
                    currencyTranslation = Messages.getMessage(MessageKey.GEMS);
                    break;
                case GOLD:
                    currencyTranslation = Messages.getMessage(MessageKey.GOLD);
                    break;
                case ELIXIR:
                    currencyTranslation = Messages.getMessage(MessageKey.ELIXIR);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + currency);
            }

            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            return;
        }

        final String errorMessage;
        final boolean useWESchematic;
        final Schematic schematic;
        final String successMessage;
        final String schematicName;

        switch (type) {
            case ARCHER_TOWER: {
                errorMessage = Messages.getMessage(MessageKey.TOWER_NOT_PLACED);
                useWESchematic = config.useArcherSchematic();
                successMessage = Messages.getMessage(MessageKey.TOWER_PLACED);
                schematicName = Schematic.Schematics.ARCHER.getName();

                if (useWESchematic)
                    schematic = new Schematic(Schematic.WorldEdit.getClipboard(Schematic.Schematics.ARCHER));
                else
                    schematic = new Schematic(Schematic.load(Schematic.Schematics.ARCHER));
                break;
            }

            case GOLD_EXTRACTOR: {
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
                if (user.getBuilding(BuildingType.GOLD_EXTRACTOR) != null || user.getBuilding(BuildingType.ELIXIR_EXTRACTOR) != null)
                    MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.COLLECTED_RESOURCES));

                errorMessage = Messages.getMessage(MessageKey.EXTRACTOR_NOT_PLACED);
                useWESchematic = config.useGoldExtractorSchematic();
                successMessage = Messages.getMessage(MessageKey.EXTRACTOR_PLACED);
                schematicName = Schematic.Schematics.GOLD_EXTRACTOR.getName();

                if (useWESchematic)
                    schematic = new Schematic(Schematic.WorldEdit.getClipboard(Schematic.Schematics.GOLD_EXTRACTOR));
                else
                    schematic = new Schematic(Schematic.load(Schematic.Schematics.GOLD_EXTRACTOR));
                break;
            }


            case ELIXIR_EXTRACTOR: {
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
                if (user.getBuilding(BuildingType.GOLD_EXTRACTOR) != null || user.getBuilding(BuildingType.ELIXIR_EXTRACTOR) != null)
                    MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.COLLECTED_RESOURCES));

                errorMessage = Messages.getMessage(MessageKey.EXTRACTOR_NOT_PLACED);
                useWESchematic = config.useElixirExtractorSchematic();
                successMessage = Messages.getMessage(MessageKey.EXTRACTOR_PLACED);
                schematicName = Schematic.Schematics.ELIXIR_EXTRACTOR.getName();

                if (useWESchematic)
                    schematic = new Schematic(Schematic.WorldEdit.getClipboard(Schematic.Schematics.ELIXIR_EXTRACTOR));
                else
                    schematic = new Schematic(Schematic.load(Schematic.Schematics.ELIXIR_EXTRACTOR));
                break;
            }

            case TOWN_HALL:
                // When the town hall is upgraded there is no schematic to paste
                errorMessage = null;
                schematic = null;
                successMessage = null;
                schematicName = null;
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }


        // If the level is 0 it means that the player has not unlocked the building
        if (currentLevel <= 0) {
            // Check if player is into own island
            if (!user.getIsland().canBuildOnLocation(clicker.getLocation())) {
                MessageUtil.sendMessage(clicker, errorMessage);
                return;
            }

            // Paste schematic
            try {
                schematic.paste(islandWorld, origin);
            } catch (final PastingException ignored) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.LOAD_ERROR, schematicName));
                return;
            }

            // Save tower position
            if (type == BuildingType.ARCHER_TOWER)
                user.setArcherPos(new Vector(origin.getX(), (origin.getY() + schematic.getSize().getHeight()), origin.getZ()));

            MessageUtil.sendMessage(clicker, successMessage);
        }

        user.upgradeBuilding(type);
    }
}
