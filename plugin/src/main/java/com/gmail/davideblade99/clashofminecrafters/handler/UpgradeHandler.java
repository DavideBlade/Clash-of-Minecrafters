/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.ArcherTower;
import com.gmail.davideblade99.clashofminecrafters.building.Buildings;
import com.gmail.davideblade99.clashofminecrafters.building.ElixirExtractor;
import com.gmail.davideblade99.clashofminecrafters.building.GoldExtractor;
import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.setting.*;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileNotFoundException;

/**
 * Class that deals with the upgrading of the {@link BuildingLevel} objects
 *
 * @author DavideBlade
 * @since 3.2
 */
public final class UpgradeHandler {

    private final CoM plugin;

    public UpgradeHandler(@Nonnull final CoM plugin) {
        this.plugin = plugin;
    }

    /**
     * Upgrades the specified building for the specified player, after appropriate checks
     *
     * @param target       Player whose building to upgrade
     * @param buildingType Type of building to be upgraded
     *
     * @throws IllegalStateException If the player does not own a village (and therefore there is no place to paste the
     *                               building)
     * @see #upgradeArcherTower(Player, Location)
     * @see #upgradeElixirExtractor(Player, Location)
     * @see #upgradeGoldExtractor(Player, Location)
     * @see #upgradeTownHall(Player)
     */
    public void upgrade(@Nonnull final Player target, @Nonnull final Buildings buildingType) {
        switch (buildingType) {
            case TOWN_HALL:
                upgradeTownHall(target);
                break;
            case ARCHER_TOWER:
                upgradeArcherTower(target, target.getLocation());
                break;
            case GOLD_EXTRACTOR:
                upgradeGoldExtractor(target, target.getLocation());
                break;
            case ELIXIR_EXTRACTOR:
                upgradeElixirExtractor(target, target.getLocation());
                break;
            default:
                throw new IllegalStateException("Unexpected building type: " + buildingType);
        }
    }

    /**
     * <p>Upgrade and paste (if schematic exists) the archer's tower for the specified player, after verifying that the
     * maximum level has not been reached, that the player can afford the upgrade purchase, and that they own a village (and
     * are within it if a new building is to be pasted).</p>
     * <p>If the player already owns the maximum level, cannot afford to purchase the upgrade, is outside the village, or if
     * an error occurs while loading the schematic, a message is sent to the player.</p>
     * <p>If the upgrade is successful, the cost is removed from the player's balance and a message is sent to the
     * player.</p>
     *
     * @param player The player to whom to upgrade the archer's tower
     * @param origin Location where to paste the schematic, if any
     *
     * @throws IllegalStateException If the player does not own a village (and therefore there is no place to paste the
     *                               building)
     */
    public void upgradeArcherTower(@Nonnull final Player player, @Nonnull final Location origin) {
        final User user = plugin.getUser(player);
        final Village village = user.getVillage();
        final ArcherTower archerTower = user.getArcherTower();
        final int nextLevel = archerTower == null ? 1 : archerTower.getLevel() + 1;

        // Checks whether the player owns a village
        if (village == null)
            throw new IllegalStateException("User does not own a village, so he cannot purchase buildings");

        // Max level reached
        if ((nextLevel - 1) >= plugin.getConfig().getMaxArcherTowerLevel()) {
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.MAX_LEVEL_REACHED, Messages.getMessage(MessageKey.ARCHER_TOWER)));
            return;
        }

        // Check if player has money to upgrade
        final ArcherTowerLevel nextTower = plugin.getConfig().getExistingArcherTower(nextLevel);
        if (!nextTower.canBePurchased(user)) {
            final String currencyTranslation = user.getBalance().getCurrencyTranslation(nextTower.currency);

            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            return;
        }

        // If there is a schematic to paste
        final String schematicName = nextTower.getRelatedSchematic();
        if (schematicName != null) {
            final Schematic schematic = loadSchematic(schematicName);
            if (schematic == null) {
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.LOAD_ERROR, schematicName));
                return;
            }

            // If a new building has to be placed, the player must be within the village (otherwise where should it be pasted?).
            if (!user.hasUnlockedArcherTower() && !village.isInsideVillage(player.getLocation())) {
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.TOWER_NOT_PLACED));
                return;
            }

            // Paste schematic
            schematic.paste(origin, exception -> {
                if (exception != null) { // Paste failed
                    MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.PASTE_ERROR, schematicName));
                    return;
                }

                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.TOWER_PLACED));

                if (!user.hasUnlockedArcherTower()) { // First paste
                    final Vector corner1 = new Vector(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
                    final Vector corner2 = new Vector(origin.getBlockX() + (schematic.getSize().getWidth() - 1), origin.getBlockY() + (schematic.getSize().getHeight() - 1), origin.getBlockZ() - (schematic.getSize().getLength() - 1));
                    user.newArcherTower(corner1, corner2);

                    // Spawn the archer
                    plugin.getBuildingTroopRegistry().createArcher(user);
                }

                //TODO: ognuno di questi metodi chiama un updateDatabase()
                user.removeBalance(nextTower.price, nextTower.currency);
                user.getArcherTower().setLevel(nextLevel);
                user.updateDatabase();

                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.UPGRADE_COMPLETED, Messages.getMessage(MessageKey.ARCHER_TOWER), String.valueOf(nextLevel)));
            });
        } else {
            //TODO: ognuno di questi metodi chiama un updateDatabase()
            user.removeBalance(nextTower.price, nextTower.currency);
            archerTower.setLevel(nextLevel);
            user.updateDatabase();

            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.UPGRADE_COMPLETED, Messages.getMessage(MessageKey.ARCHER_TOWER), String.valueOf(nextLevel)));
        }
    }


    /**
     * <p>Upgrade and paste (if schematic exists) the elixir extractor for the specified player, after verifying that the
     * maximum level has not been reached, that the player can afford the upgrade purchase, and that they own a village (and
     * are within it if a new building is to be pasted).</p>
     * <p>If the player already owns the maximum level, cannot afford to purchase the upgrade, is outside the village, or if
     * an error occurs while loading the schematic, a message is sent to the player.</p>
     * <p>If the upgrade is successful, the cost is removed from the player's balance and a message is sent to the
     * player.</p>
     *
     * @param player The player to whom to upgrade the elixir extractor
     * @param origin Location where to paste the schematic, if any
     *
     * @throws IllegalStateException If the player does not own a village (and therefore there is no place to paste the
     *                               building)
     */
    public void upgradeElixirExtractor(@Nonnull final Player player, @Nonnull final Location origin) {
        final User user = plugin.getUser(player);
        final Village village = user.getVillage();
        final ElixirExtractor elixirExtractor = user.getElixirExtractor();
        final int nextLevel = elixirExtractor == null ? 1 : elixirExtractor.getLevel() + 1;

        // Checks whether the player owns a village
        if (village == null)
            throw new IllegalStateException("User does not own a village, so he cannot purchase buildings");

        // Max level reached
        if ((nextLevel - 1) >= plugin.getConfig().getMaxElixirExtractorLevel()) {
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.MAX_LEVEL_REACHED, Messages.getMessage(MessageKey.ELIXIR_EXTRACTOR)));
            return;
        }

        // Check if player has money to upgrade
        final ElixirExtractorLevel nextExtractor = plugin.getConfig().getExistingElixirExtractor(nextLevel);
        if (!nextExtractor.canBePurchased(user)) {
            final String currencyTranslation = user.getBalance().getCurrencyTranslation(nextExtractor.currency);

            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            return;
        }

        // If there is a schematic to paste
        final String schematicName = nextExtractor.getRelatedSchematic();
        if (schematicName != null) {
            final Schematic schematic = loadSchematic(schematicName);
            if (schematic == null) {
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.LOAD_ERROR, schematicName));
                return;
            }

            // If a new building has to be placed, the player must be within the village (otherwise where should it be pasted?).
            if (!user.hasUnlockedElixirExtractor() && !village.isInsideVillage(player.getLocation())) {
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.EXTRACTOR_NOT_PLACED));
                return;
            }

            // Paste schematic
            schematic.paste(origin, exception -> {
                if (exception != null) { // Paste failed
                    MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.PASTE_ERROR, schematicName));
                    return;
                }

                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.EXTRACTOR_PLACED));

                if (!user.hasUnlockedElixirExtractor()) { // First paste
                    final Vector corner1 = new Vector(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
                    final Vector corner2 = new Vector(origin.getBlockX() + (schematic.getSize().getWidth() - 1), origin.getBlockY() + (schematic.getSize().getHeight() - 1), origin.getBlockZ() - (schematic.getSize().getLength() - 1));
                    user.newElixirExtractor(corner1, corner2);
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
                if (user.hasExtractor())
                    MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.COLLECTED_RESOURCES));

                //TODO: ognuno di questi metodi chiama un updateDatabase()
                user.removeBalance(nextExtractor.price, nextExtractor.currency);
                user.getElixirExtractor().setLevel(nextLevel);
                user.updateDatabase();

                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.UPGRADE_COMPLETED, Messages.getMessage(MessageKey.ELIXIR_EXTRACTOR), String.valueOf(nextLevel)));
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
            if (user.hasExtractor())
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.COLLECTED_RESOURCES));

            //TODO: ognuno di questi metodi chiama un updateDatabase()
            user.removeBalance(nextExtractor.price, nextExtractor.currency);
            user.getElixirExtractor().setLevel(nextLevel);
            user.updateDatabase();

            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.UPGRADE_COMPLETED, Messages.getMessage(MessageKey.ELIXIR_EXTRACTOR), String.valueOf(nextLevel)));
        }
    }

    /**
     * <p>Upgrade and paste (if schematic exists) the gold extractor for the specified player, after verifying that the
     * maximum level has not been reached, that the player can afford the upgrade purchase, and that they own a village (and
     * are within it if a new building is to be pasted).</p>
     * <p>If the player already owns the maximum level, cannot afford to purchase the upgrade, is outside the village, or if
     * an error occurs while loading the schematic, a message is sent to the player.</p>
     * <p>If the upgrade is successful, the cost is removed from the player's balance and a message is sent to the
     * player.</p>
     *
     * @param player The player to whom to upgrade the gold extractor
     * @param origin Location where to paste the schematic, if any
     *
     * @throws IllegalStateException If the player does not own a village (and therefore there is no place to paste the
     *                               building)
     */
    public void upgradeGoldExtractor(@Nonnull final Player player, @Nonnull final Location origin) {
        final User user = plugin.getUser(player);
        final Village village = user.getVillage();
        final GoldExtractor goldExtractor = user.getGoldExtractor();
        final int nextLevel = goldExtractor == null ? 1 : goldExtractor.getLevel() + 1;

        // Checks whether the player owns a village
        if (village == null)
            throw new IllegalStateException("User does not own a village, so he cannot purchase buildings");

        // Max level reached
        if ((nextLevel - 1) >= plugin.getConfig().getMaxGoldExtractorLevel()) {
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.MAX_LEVEL_REACHED, Messages.getMessage(MessageKey.ELIXIR_EXTRACTOR)));
            return;
        }

        // Check if player has money to upgrade
        final GoldExtractorLevel nextExtractor = plugin.getConfig().getExistingGoldExtractor(nextLevel);
        if (!nextExtractor.canBePurchased(user)) {
            final String currencyTranslation = user.getBalance().getCurrencyTranslation(nextExtractor.currency);

            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            return;
        }

        // If there is a schematic to paste
        final String schematicName = nextExtractor.getRelatedSchematic();
        if (schematicName != null) {

            final Schematic schematic = loadSchematic(schematicName);
            if (schematic == null) {
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.LOAD_ERROR, schematicName));
                return;
            }

            // If a new building has to be placed, the player must be within the village (otherwise where should it be pasted?).
            if (!user.hasUnlockedGoldExtractor() && !village.isInsideVillage(player.getLocation())) {
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.EXTRACTOR_NOT_PLACED));
                return;
            }

            // Paste schematic
            schematic.paste(origin, exception -> {
                if (exception != null) { // Paste failed
                    MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.PASTE_ERROR, schematicName));
                    return;
                }

                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.EXTRACTOR_PLACED));

                if (!user.hasUnlockedGoldExtractor()) { // First paste
                    final Vector corner1 = new Vector(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
                    final Vector corner2 = new Vector(origin.getBlockX() + (schematic.getSize().getWidth() - 1), origin.getBlockY() + (schematic.getSize().getHeight() - 1), origin.getBlockZ() - (schematic.getSize().getLength() - 1));
                    user.newGoldExtractor(corner1, corner2);
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
                if (user.hasExtractor())
                    MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.COLLECTED_RESOURCES));

                //TODO: ognuno di questi metodi chiama un updateDatabase()
                user.removeBalance(nextExtractor.price, nextExtractor.currency);
                user.getGoldExtractor().setLevel(nextLevel);
                user.updateDatabase();

                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.UPGRADE_COMPLETED, Messages.getMessage(MessageKey.GOLD_EXTRACTOR), String.valueOf(nextLevel)));
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
            if (user.hasExtractor())
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.COLLECTED_RESOURCES));

            //TODO: ognuno di questi metodi chiama un updateDatabase()
            user.removeBalance(nextExtractor.price, nextExtractor.currency);
            user.getGoldExtractor().setLevel(nextLevel);
            user.updateDatabase();

            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.UPGRADE_COMPLETED, Messages.getMessage(MessageKey.GOLD_EXTRACTOR), String.valueOf(nextLevel)));
        }
    }

    /**
     * <p>Upgrade the town hall for the specified player, after verifying that the maximum level has not been reached,
     * that the player owns a village and can afford to purchase the upgrade.</p>
     * <p>If the player already has the maximum level or cannot afford the upgrade, a message is sent to the player.</p>
     * <p>If the upgrade is successful, the cost is removed from the player's balance and a message is sent to the
     * player.</p>
     *
     * @param player The player to whom to upgrade the town hall
     *
     * @throws IllegalStateException If the player does not own a village (and consequently does not own a town hall)
     */
    public void upgradeTownHall(@Nonnull final Player player) {
        final User user = plugin.getUser(player);
        final int nextLevel = user.getTownHallLevel() + 1;

        // Checks whether the player owns a village
        if (user.getVillage() == null)
            throw new IllegalStateException("User does not own a village, so he cannot purchase buildings");

        // Max level reached
        if ((nextLevel - 1) >= plugin.getConfig().getMaxTownHallLevel()) {
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.MAX_LEVEL_REACHED, Messages.getMessage(MessageKey.TOWN_HALL)));
            return;
        }

        // Check if player has money to upgrade
        final TownHallLevel nextTownHall = plugin.getConfig().getExistingTownHall(nextLevel);
        if (!nextTownHall.canBePurchased(user)) {
            final String currencyTranslation = user.getBalance().getCurrencyTranslation(nextTownHall.currency);

            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            return;
        }

        if (nextTownHall.command != null)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), nextTownHall.command.replace("%player", player.getName()));
        //TODO: parte per l'incollamento della schematic

        //TODO: ognuno di questi metodi chiama un updateDatabase()
        user.removeBalance(nextTownHall.price, nextTownHall.currency);
        user.setTownHallLevel(nextLevel);
        user.updateDatabase();

        MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.UPGRADE_COMPLETED, Messages.getMessage(MessageKey.TOWN_HALL), String.valueOf(nextLevel)));
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
}
