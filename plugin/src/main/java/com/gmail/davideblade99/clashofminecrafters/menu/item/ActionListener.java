/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.menu.item;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Interface that must be implemented by classes interested in performing on-click operations.
 * When the click event occurs, that object's {@link #onClick(CoM, Player)} method is invoked.
 */
public interface ActionListener {

    /**
     * Invoked when a click occurs. Implement this method with the operations to be performed on click.
     *
     * @param plugin  Plugin instance
     * @param clicker Player who clicked the item
     */
    void onClick(@Nonnull final CoM plugin, @Nonnull final Player clicker);
}