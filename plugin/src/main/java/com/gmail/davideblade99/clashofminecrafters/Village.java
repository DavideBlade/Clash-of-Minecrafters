/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters;

import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.geometric.Size2D;
import com.gmail.davideblade99.clashofminecrafters.geometric.Vector;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class representing a player's village
 *
 * @since v3.1.2
 */
public final class Village {

    public final String owner;
    public Location spawn;
    public final Vector origin;
    public final Size2D size;
    public final Size2D expansions;

    public Village(@Nonnull final String owner, @Nonnull final Location spawn, @Nonnull final Vector origin, @Nonnull final Size2D islandSize, @Nonnull final Size2D expansions) {
        this.owner = owner;
        this.spawn = spawn;
        this.origin = origin;
        this.size = islandSize;
        this.expansions = expansions;
    }

    public void teleportToSpawn(@Nonnull final Player player) {
        if (BukkitLocationUtil.isSafeLocation(spawn))
            player.teleport(spawn);
        else {
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.ISLAND_SPAWN_NOT_SAFE));
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.TELEPORTATION_CANCELLED));
        }
    }

    // Check if loc is into island
    public boolean canBuildOnLocation(@Nonnull final Location loc) {
        int minX = origin.getX() - expansions.getWidth(), minZ = origin.getZ() + expansions.getLength();
        int maxX = origin.getX() + (size.getWidth() - 1) + expansions.getWidth(), maxZ = origin.getZ() - (size.getLength() - 1) - expansions.getLength();

        if (minX > maxX) {
            int tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        if (minZ > maxZ) {
            int tmp = minZ;
            minZ = maxZ;
            maxZ = tmp;
        }

        final int x = (int) loc.getX(), z = (int) loc.getZ();
        return (x >= minX && x <= maxX && z >= minZ && z <= maxZ);
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof Village))
            return false;
        else {
            final Village island = (Village) obj;
            return this.owner.equals(island.owner);
        }
    }

    @Override
    public String toString() {
        return "Island{" +
                "owner='" + owner + "'" +
                ", spawn=" + BukkitLocationUtil.toString(spawn) +
                ", origin=" + origin +
                ", size=" + size +
                ", expansions=" + expansions +
                '}';
    }
}