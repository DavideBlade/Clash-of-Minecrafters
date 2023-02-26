/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.Buildings;
import com.gmail.davideblade99.clashofminecrafters.building.Upgradeable;
import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.setting.ArcherTowerLevel;
import com.gmail.davideblade99.clashofminecrafters.setting.BuildingLevel;
import com.gmail.davideblade99.clashofminecrafters.setting.ElixirExtractorLevel;
import com.gmail.davideblade99.clashofminecrafters.setting.GoldExtractorLevel;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileNotFoundException;

/**
 * Class that deals with the upgrading of the {@link Upgradeable} objects
 *
 * @author DavideBlade
 * @since v3.1.4
 */
public final class UpgradeManager {

    private final CoM plugin;
    private final ArcherTowerUpgradeManager archerTowerUpgradeManager;
    private final ElixirExtractorUpgradeManager elixirExtractorUpgradeManager;
    private final GoldExtractorUpgradeManager goldExtractorUpgradeManager;
    private final TownHallUpgradeManager townHallUpgradeManager;

    public UpgradeManager(@Nonnull final CoM plugin) {
        this.plugin = plugin;
        this.archerTowerUpgradeManager = new ArcherTowerUpgradeManager();
        this.elixirExtractorUpgradeManager = new ElixirExtractorUpgradeManager();
        this.goldExtractorUpgradeManager = new GoldExtractorUpgradeManager();
        this.townHallUpgradeManager = new TownHallUpgradeManager();
    }

    /**
     * Upgrade the building for the specified player, after verifying that the maximum level has not been reached
     * and that the player owns an island
     *
     * @param target       User whose building to upgrade
     * @param buildingType Type of building to be upgraded
     *
     * @throws IllegalStateException If the player already owns the maximum level of the upgrade or if the player
     *                               does not own a village (and therefore there is no place for the building)
     */
    public void upgrade(@Nonnull final Player target, @Nonnull final Buildings buildingType) {
        final User user = plugin.getUser(target);

        /*
         * Check if the upgrade can be performed. It checks that the player does not already have the maximum
         * level of the upgrade and is inside his island, in case a schematic needs to be pasted.
         */
        final BuildingLevel nextBuilding = plugin.getConfig().getBuilding(buildingType, user.getBuildingLevel(buildingType) + 1);
        if (nextBuilding == null)
            throw new IllegalStateException("The building is already at the highest level. It cannot be upgraded further.");

        // If there is a schematic to paste
        final String schematicName = nextBuilding.getRelatedSchematic();
        if (schematicName != null) {
            final Village village = user.getVillage();
            if (village == null)
                throw new IllegalStateException("User does not own a village, so he cannot purchase buildings");

            // Check if the player is into own island
            if (!village.isInsideVillage(target.getLocation()))
                MessageUtil.sendMessage(target, Messages.getMessage(buildingType == Buildings.ELIXIR_EXTRACTOR || buildingType == Buildings.GOLD_EXTRACTOR ? MessageKey.EXTRACTOR_NOT_PLACED : MessageKey.TOWER_NOT_PLACED));
        }


        // Check if player has money to upgrade
        if (!nextBuilding.canBePurchased(user)) {
            final String currencyTranslation = user.getBalance().getCurrencyTranslation(nextBuilding.currency);

            MessageUtil.sendMessage(target, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            return;
        }

        switch (buildingType) {
            case TOWN_HALL:
                townHallUpgradeManager.upgrade(user);
                break;
            case ARCHER_TOWER:
                archerTowerUpgradeManager.upgrade(user, target.getLocation());
                break;
            case GOLD_EXTRACTOR:
                goldExtractorUpgradeManager.upgrade(user, target.getLocation());
                break;
            case ELIXIR_EXTRACTOR:
                elixirExtractorUpgradeManager.upgrade(user, target.getLocation());
                break;
        }
    }

    /**
     * Utility method for loading a schematic
     *
     * @param schematic Name of schematic file to laod
     *
     * @return The {@link Schematic} loaded or {@code null} in case of error
     */
    @Nullable
    private Schematic loadSchematic(@Nonnull final String schematic) {
        try {
            return plugin.getSchematicHandler().getSchematic(schematic);
        } catch (final FileNotFoundException e) {
            MessageUtil.sendError("It seems that the schematic within the .jar is missing. Download the plugin again.");
            //TODO: file di log
            return null;
        } catch (final InvalidSchematicFormatException e) {
            MessageUtil.sendError("It seems that the schematic format is invalid. They may not be up to date: create them again by checking for version matches.");
            //TODO: file di log
            return null;
        } catch (final Exception e) {
            MessageUtil.sendError("A generic error occurred in reading the schematic file.");
            //TODO: file di log
            return null;
        }
    }

    private final class ArcherTowerUpgradeManager {

        /**
         * Upgrade the archer's tower for the specified player by placing the schematic (if any)
         *
         * @param user   User to upgrade the archer's tower to
         * @param origin Location where to paste the schematic, if any
         */
        private void upgrade(@Nonnull final User user, @Nonnull final Location origin) {
            final ArcherTowerLevel nextArcherTower = plugin.getConfig().getArcherTower(user.getBuildingLevel(Buildings.ARCHER_TOWER) + 1);

            // If there is a schematic to paste
            final String schematicName = nextArcherTower.getRelatedSchematic();
            if (schematicName != null) {
                final Schematic schematic = loadSchematic(schematicName);
                if (schematic == null) {
                    MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.LOAD_ERROR, schematicName));
                    return;
                }

                // Paste schematic
                schematic.paste(origin, exception -> {
                    if (exception != null) { // Paste failed
                        MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.PASTE_ERROR, schematicName));
                        return;
                    }

                    MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.TOWER_PLACED));

                    // Save tower position
                    user.setArcherPos(new Vector(origin.getBlockX(), (origin.getBlockY() + schematic.getSize().getHeight()), origin.getBlockZ()));

                    user.upgradeBuilding(Buildings.ARCHER_TOWER);
                });
            } else
                user.upgradeBuilding(Buildings.ARCHER_TOWER);
        }
    }

    private final class ElixirExtractorUpgradeManager {

        /**
         * Upgrade the elixir extractor for the specified player by placing the schematic (if any)
         *
         * @param user   User to upgrade the elixir extractor to
         * @param origin Location where to paste the schematic, if any
         */
        private void upgrade(@Nonnull final User user, @Nonnull final Location origin) {
            final ElixirExtractorLevel nextElixirExtractor = plugin.getConfig().getElixirExtractor(user.getBuildingLevel(Buildings.ELIXIR_EXTRACTOR) + 1);

            // If there is a schematic to paste
            final String schematicName = nextElixirExtractor.getRelatedSchematic();
            if (schematicName != null) {

                final Schematic schematic = loadSchematic(schematicName);
                if (schematic == null) {
                    MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.LOAD_ERROR, schematicName));
                    return;
                }

                // Paste schematic
                schematic.paste(origin, exception -> {
                    if (exception != null) { // Paste failed
                        MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.PASTE_ERROR, schematicName));
                        return;
                    }

                    MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.EXTRACTOR_PLACED));

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
                    if (user.hasBuilding(Buildings.ELIXIR_EXTRACTOR))
                        MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.COLLECTED_RESOURCES));

                    user.upgradeBuilding(Buildings.ELIXIR_EXTRACTOR);
                });
            } else {
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
                if (user.hasBuilding(Buildings.ELIXIR_EXTRACTOR))
                    MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.COLLECTED_RESOURCES));

                user.upgradeBuilding(Buildings.ELIXIR_EXTRACTOR);
            }
        }
    }

    private final class GoldExtractorUpgradeManager {

        /**
         * Upgrade the gold extractor for the specified player by placing the schematic (if any)
         *
         * @param user   User to upgrade the gold extractor to
         * @param origin Location where to paste the schematic, if any
         */
        private void upgrade(@Nonnull final User user, @Nonnull final Location origin) {
            final GoldExtractorLevel nextGoldExtractor = plugin.getConfig().getGoldExtractor(user.getBuildingLevel(Buildings.GOLD_EXTRACTOR) + 1);

            // If there is a schematic to paste
            final String schematicName = nextGoldExtractor.getRelatedSchematic();
            if (schematicName != null) {

                final Schematic schematic = loadSchematic(schematicName);
                if (schematic == null) {
                    MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.LOAD_ERROR, schematicName));
                    return;
                }

                // Paste schematic
                schematic.paste(origin, exception -> {
                    if (exception != null) { // Paste failed
                        MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.PASTE_ERROR, schematicName));
                        return;
                    }

                    MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.EXTRACTOR_PLACED));

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
                        MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.COLLECTED_RESOURCES));

                    user.upgradeBuilding(Buildings.GOLD_EXTRACTOR);
                });
            } else {
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
                    MessageUtil.sendMessage((Player) user.getBase(), Messages.getMessage(MessageKey.COLLECTED_RESOURCES));

                user.upgradeBuilding(Buildings.GOLD_EXTRACTOR);
            }
        }
    }

    private final class TownHallUpgradeManager {

        /**
         * Upgrade the town hall for the specified player
         *
         * @param user User to upgrade the gold extractor to
         */
        private void upgrade(@Nonnull final User user) {
            user.upgradeBuilding(Buildings.TOWN_HALL);
        }
    }
}
