/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.bukkit;

import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public final class BukkitVectorUtil {

    private BukkitVectorUtil() { }

    /**
     * Formats a string containing data of the specified vector
     *
     * @param vector {@code Vector} to be formatted
     *
     * @return a string containing data of the specified vector
     */
    @Nonnull
    public static String toString(@Nonnull final Vector vector) {
        return vector.getX() + ", " + vector.getY() + ", " + vector.getZ();
    }
}
