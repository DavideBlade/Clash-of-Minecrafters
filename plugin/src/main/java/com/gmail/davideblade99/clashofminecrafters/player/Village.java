/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.player;

import com.gmail.davideblade99.clashofminecrafters.util.geometric.Area;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Size2D;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class representing a player's village
 *
 * @author DavideBlade
 * @since 3.1.4
 */
public final class Village {

    public final String owner;
    private Location spawn;
    public final Vector origin;
    public final Size2D size;
    public final Size2D expansions;

    /**
     * Creates a new village
     *
     * @param owner      Village owner
     * @param spawn      Village spawn point
     * @param origin     Point where the schematic is pasted
     * @param size       Village size
     * @param expansions Expansions owned by the village
     *
     * @throws IllegalArgumentException If the {@code spawn} does not have a world (i.e., {@link Location#getWorld()}
     *                                  {@code = null})
     */
    public Village(@Nonnull final String owner, @Nonnull final Location spawn, @Nonnull final Vector origin, @Nonnull final Size2D size, @Nonnull final Size2D expansions) {
        if (spawn.getWorld() == null)
            throw new IllegalArgumentException("Invalid spawn: does not have a specified world");

        this.owner = owner;
        this.spawn = spawn;
        this.origin = origin;
        this.size = size;
        this.expansions = expansions;
    }

    /**
     * @return A new copy of the spawn point
     */
    public Location getSpawn() {
        return this.spawn.clone();
    }

    /**
     * Sets the spawn to the specified location, if it is safe
     *
     * @param spawn New spawn point to be set
     *
     * @return True if the location was safe and the new spawn could be set, otherwise false
     * @see BukkitLocationUtil#isSafeLocation(Location)
     */
    public boolean setSpawn(@Nonnull final Location spawn) {
        if (!BukkitLocationUtil.isSafeLocation(spawn))
            return false;

        this.spawn = spawn;
        return true;
    }

    public void teleportToSpawn(@Nonnull final Player player) {
        if (BukkitLocationUtil.isSafeLocation(spawn))
            player.teleport(spawn);
        else {
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.ISLAND_SPAWN_NOT_SAFE));
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.TELEPORTATION_CANCELLED));
        }
    }

    /**
     * Checks whether {@code loc} is within {@code this} village
     *
     * @param loc Location to be checked
     *
     * @return True if the specified location is within {@code this} village, otherwise false
     */
    public boolean isInsideVillage(@Nonnull final Location loc) {
        if (!spawn.getWorld().equals(loc.getWorld())) // Not village world
            return false;

        final int x1 = origin.getX() - expansions.getWidth(), z1 = origin.getZ() + expansions.getLength();
        final int x2 = origin.getX() + (size.getWidth() - 1) + expansions.getWidth(), z2 = origin.getZ() - (size.getLength() - 1) - expansions.getLength();

        return new Area(x1, Integer.MIN_VALUE, z1, x2, Integer.MAX_VALUE, z2).contains(new Vector(loc));
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof Village))
            return false;
        else {
            final Village island = (Village) obj;
            return owner.equals(island.owner);
        }
    }

    @Override
    public String toString() {
        return "Village{" +
                "owner='" + owner + "'" +
                ", spawn=" + BukkitLocationUtil.toString(spawn) +
                ", origin='" + origin + "'" +
                ", size=" + size +
                ", expansions=" + expansions +
                '}';
    }
}