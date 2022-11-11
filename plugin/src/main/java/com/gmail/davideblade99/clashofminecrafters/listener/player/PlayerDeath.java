/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.player;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.event.raid.RaidLostEvent;
import com.gmail.davideblade99.clashofminecrafters.island.Island;
import com.gmail.davideblade99.clashofminecrafters.listener.IslandListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nonnull;

public final class PlayerDeath extends IslandListener {

    public PlayerDeath(@Nonnull final CoM plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (!isIslandWorld(player.getWorld()))
            return;

        final Island island = plugin.getUser(player).getIsland();
        if (island != null && !island.canBuildOnLocation(player.getLocation())) // If player dies out of own island
        {
            // Prevent the loss of inventory and exp
            event.setDroppedExp(0);
            event.getDrops().clear();
        }

        // If player dies during a raid
        final Island attackedIsland = plugin.getWarHandler().getAttackedIsland(player);
        if (attackedIsland != null)
            Bukkit.getPluginManager().callEvent(new RaidLostEvent(player, attackedIsland.owner));
    }
}