/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.building;

import com.gmail.davideblade99.clashofminecrafters.player.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface representing an upgradeable building (via the /upgrade command)
 *
 * @author DavideBlade
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
     * @return The name of the schematic file to be pasted in when the player buys the building. {@code Null} if no
     * schematic is to be pasted.
     *
     * @implSpec The first level should always return {@code null}, as the player has not yet unlocked anything.
     * @implSpec The name returned should not contain the ".schematic" extension but only the file name.
     */
    @Nullable
    String getRelatedSchematic();

    /**
     * Checks whether the user can afford to purchase the building
     *
     * @return True if he can buy it, otherwise false
     */
    boolean canBePurchased(@Nonnull final User user);
}
