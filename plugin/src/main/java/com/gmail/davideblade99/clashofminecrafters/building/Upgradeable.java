/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.building;

import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.schematic.Pasteable;

import javax.annotation.Nonnull;

/**
 * Interface representing an upgradeable building (via the /upgrade command)
 *
 * @author DavideBlade
 * @see Pasteable
 * @since v3.1.4
 */
public interface Upgradeable {

    /**
     * <p>Checks whether the building level is the base level.</p>
     * <p>The base level is the level that players get by default when they first join the server and have not yet
     * purchased anything.</p>
     *
     * @return True if the current level of the building matches its base level, otherwise false
     *
     * @see Buildings#firstLevel
     */
    boolean isFirstLevel();

    /**
     * Checks whether the user can afford to purchase the building
     *
     * @return True if he can buy it, otherwise false
     */
    boolean canBePurchased(@Nonnull final User user);
}
