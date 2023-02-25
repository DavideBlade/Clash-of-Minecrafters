/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.setting.BuildingLevel;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileNotFoundException;

public abstract class UpgradeMenuItem extends BaseItem {

    final CoM plugin;
    final BuildingLevel nextBuilding;

    /**
     * Creates a new instance of the class
     *
     * @param plugin       Plugin instance
     * @param nextBuilding Next level building (the one to be purchased)
     * @param slot         Slot in which to place the item
     *
     * @since v3.1.2
     */
    public UpgradeMenuItem(@Nonnull final CoM plugin, @Nonnull final BuildingLevel nextBuilding, final byte slot) {
        super(nextBuilding.getItem(plugin), slot);

        this.plugin = plugin;
        this.nextBuilding = nextBuilding;
    }

    /**
     * Shared utility method for loading a schematic. In case of error during loading it sends the error messages
     * to the {@code clicker}.
     *
     * @param clicker   Player who clicked the item and should receive any error messages
     * @param schematic Name of schematic file to laod
     *
     * @return The {@link Schematic} loaded or {@code null} in case of error
     */
    @Nullable
    Schematic loadSchematic(@Nonnull final Player clicker, @Nonnull final String schematic) {
        try {
            return plugin.getSchematicHandler().getSchematic(schematic);
        } catch (final FileNotFoundException e) {
            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.LOAD_ERROR, schematic));
            MessageUtil.sendError("It seems that the schematic within the .jar is missing. Download the plugin again.");
            //TODO: file di log
            return null;
        } catch (final InvalidSchematicFormatException e) {
            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.LOAD_ERROR, schematic));
            MessageUtil.sendError("It seems that the schematic format is invalid. They may not be up to date: create them again by checking for version matches.");
            //TODO: file di log
            return null;
        } catch (final Exception e) {
            MessageUtil.sendMessage(clicker, Messages.getMessage(MessageKey.LOAD_ERROR, schematic));
            MessageUtil.sendError("A generic error occurred in reading the schematic file.");
            //TODO: file di log
            return null;
        }
    }
}
