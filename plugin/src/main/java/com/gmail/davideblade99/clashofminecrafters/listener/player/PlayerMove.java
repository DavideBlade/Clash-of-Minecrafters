/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.player;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.handler.VillageHandler;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.listener.VillageListener;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;

public final class PlayerMove extends VillageListener {

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
        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ())
            return;

        final Village island = plugin.getUser(player).getVillage();
        if (island == null)
            return;

        if (island.isInsideVillage(from) && !island.isInsideVillage(to))
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.LEFT_ISLAND));
        else if (island.isInsideVillage(to) && !island.isInsideVillage(from))
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.ENTERED_IN_ISLAND));
    }
}