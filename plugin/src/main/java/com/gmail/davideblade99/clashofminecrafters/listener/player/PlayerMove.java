/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.player;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.island.Island;
import com.gmail.davideblade99.clashofminecrafters.listener.IslandListener;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ChatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;

public final class PlayerMove extends IslandListener {

    public PlayerMove(@Nonnull final CoM plugin) {
        super(plugin);
    }

    /**
     * This method is responsible for monitoring whether the player enters or leaves the island
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        //TODO: fare tutto in async
        final Player player = event.getPlayer();

        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (!isIslandWorld(player.getWorld()))
            return;
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
            return;

        final Island island = plugin.getUser(player).getIsland();
        if (island == null)
            return;

        //TODO: funziona?
        if (island.canBuildOnLocation(from) && !island.canBuildOnLocation(to))
            ChatUtil.sendMessage(player, Messages.getMessage(MessageKey.LEFT_ISLAND));
        else if (island.canBuildOnLocation(to) && !island.canBuildOnLocation(from))
            ChatUtil.sendMessage(player, Messages.getMessage(MessageKey.ENTERED_IN_ISLAND));
    }
}