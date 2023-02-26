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
}
