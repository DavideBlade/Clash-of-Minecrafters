package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.building.Buildings;
import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematics;
import com.gmail.davideblade99.clashofminecrafters.setting.bean.BuildingSettings;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileNotFoundException;

/**
 * Represents the item used to upgrade elixir extractors
 *
 * @since v3.1.2
 */
public final class ElixirExtractorUpgradeItem extends UpgradeMenuItem {

    /**
     * {@inheritDoc}
     */
    public ElixirExtractorUpgradeItem(@Nonnull final CoM plugin, @Nonnull final BuildingSettings nextBuilding, final byte slot) {
        super(plugin, nextBuilding, slot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(@Nonnull final CoM plugin, @Nonnull final Player clicker) {
        final World villageWorld = plugin.getVillageHandler().getVillageWorld();
        final Vector origin = new Vector(clicker.getLocation());
        final User user = plugin.getUser(clicker);

        final int currentLevel = nextBuilding.level - 1;
        final int nextLevel = currentLevel + 1;

        // Check if player has money to upgrade
        if (!user.hasMoneyToUpgrade(nextLevel, Buildings.ELIXIR_EXTRACTOR)) {
            final String currencyTranslation = user.getBalance().getCurrencyTranslation(nextBuilding.currency);

            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.NOT_ENOUGH_MONEY, currencyTranslation));
            return;
        }


        // If the level is 0 it means that the player has not unlocked the building
        if (currentLevel <= 0) {
            // Check if player is into own island
            if (!user.getIsland().canBuildOnLocation(clicker.getLocation())) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.EXTRACTOR_NOT_PLACED));
                return;
            }

            // Paste schematic
            try {
                plugin.getSchematicHandler().getSchematic(Schematics.ELIXIR_EXTRACTOR).paste(origin.toBukkitLocation(villageWorld), new NullableCallback<PastingException>() {
                    @Override
                    public void call(@Nullable final PastingException result) {
                        MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.EXTRACTOR_PLACED));

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
                            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.COLLECTED_RESOURCES));

                        user.upgradeBuilding(Buildings.ELIXIR_EXTRACTOR);
                    }
                });
            } catch (final FileNotFoundException e) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.LOAD_ERROR, Schematics.ELIXIR_EXTRACTOR.getName()));
                MessageUtil.sendError("It seems that the schematic within the .jar is missing. Download the plugin again.");
                //TODO: file di log
            } catch (final InvalidSchematicFormatException e) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.LOAD_ERROR, Schematics.ELIXIR_EXTRACTOR.getName()));
                MessageUtil.sendError("It seems that the schematic format is invalid. They may not be up to date: create them again by checking for version matches.");
                //TODO: file di log
            } catch (final Exception e) {
                MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.LOAD_ERROR, Schematics.ELIXIR_EXTRACTOR.getName()));
                MessageUtil.sendError("A generic error occurred in reading the schematic file.");
                //TODO: file di log
            }
        }
    }
}
