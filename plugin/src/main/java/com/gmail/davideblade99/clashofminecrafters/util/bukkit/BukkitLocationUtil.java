/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class BukkitLocationUtil {

    private BukkitLocationUtil() {
        throw new IllegalAccessError();
    }

    public static boolean isSafeLocation(final Location loc) {
        final World world = Bukkit.getWorld("Islands");
        final Block b0 = world.getBlockAt(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
        final Block b1 = world.getBlockAt(loc);
        final Block b2 = world.getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());

        // If location is safe, return true
        return b0.getType() == Material.AIR && b1.getType() == Material.AIR && b2.getType().isSolid() && b2.getType() != Material.CACTUS;
    }

    /**
     * Formats a string containing data of the specified location, including yaw and pitch
     *
     * @param loc {@code Location} to be formatted
     *
     * @return a string containing data of the specified location
     */
    @Nonnull
    public static String toString(@Nonnull final Location loc) {
        return toString(loc, true);
    }

    /**
     * Formats a string containing data of the specified location
     *
     * @param loc      {@code Location} to be formatted
     * @param yawPitch Whether yaw and pitch should also be included in the string
     *
     * @return a string containing data of the specified location
     */
    @Nonnull
    public static String toString(@Nonnull final Location loc, final boolean yawPitch) {
        String small = loc.getWorld().getName() + ", " + BukkitVectorUtil.toString(loc.toVector());
        if (yawPitch)
            small += ", " + loc.getYaw() + ", " + loc.getPitch();

        return small;
    }

    @Contract("null, _ -> null; _, null -> null")
    @Nullable
    public static Location fromString(final World world, final String str) {
        if (world == null || str == null)
            return null;

        final String[] split = str.split(",");
        if (split.length == 3) {
            try {
                return new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
            } catch (final Exception ignored) {
                return null;
            }
        } else if (split.length == 5) {
            try {
                return new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Float.parseFloat(split[3]), Float.parseFloat(split[4]));
            } catch (final Exception ignored) {
                return null;
            }
        } else
            return null;
    }

    @Nullable
    public static Location fromString(@Nullable final String str) {
        if (str == null)
            return null;

        final String[] split = str.split(",");
        if (split.length == 4) {
            try {
                return new Location(Bukkit.getWorld(split[0].trim()), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
            } catch (final Exception ignored) {
                return null;
            }
        } else if (split.length == 6) {
            try {
                return new Location(Bukkit.getWorld(split[0].trim()), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
            } catch (final Exception ignored) {
                return null;
            }
        } else
            return null;
    }
}