/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.player;

import com.gmail.davideblade99.clashofminecrafters.handler.VillageHandler;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Area;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Size2D;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Class representing a player's village
 *
 * @author DavideBlade
 * @since 3.2.2
 */
public final class Village {

    public final UUID owner;
    private Location spawn;
    public final Vector origin;
    public final Size2D size;
    public final Size2D expansions;

    /**
     * Creates a new village
     *
     * @param owner      UUID of the player owning the village
     * @param spawn      Village spawn point
     * @param origin     Point where the schematic is pasted
     * @param size       Village size
     * @param expansions Expansions owned by the village
     *
     * @throws IllegalArgumentException If the world in which to spawn the archer is not the village world
     * @see VillageHandler#isVillageWorld(World)
     */
    public Village(@Nonnull final UUID owner, @Nonnull final Location spawn, @Nonnull final Vector origin, @Nonnull final Size2D size, @Nonnull final Size2D expansions) {
        if (!VillageHandler.isVillageWorld(spawn.getWorld()))
            throw new IllegalArgumentException("Villages can only exist in the world of villages");

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
        // TODO: impedire di creare un edificio sopra il punto di spawn, in questo modo non e' necessario dover controllare ogni volta che lo spawn sia sicuro
        // TODO: Una volta fatto, rimuovere tutti i controlli ed i messaggi
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
            final Village village = (Village) obj;
            return owner.equals(village.owner);
        }
    }

    @Override
    public int hashCode() {
        return owner.hashCode();
    }

    @Override
    public String toString() {
        return "Village{" +
                "owner=" + owner +
                ", spawn=" + spawn +
                ", origin=" + origin +
                ", size=" + size +
                ", expansions=" + expansions +
                '}';
    }
}