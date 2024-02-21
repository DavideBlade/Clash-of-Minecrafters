/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.player;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.event.raid.RaidLostEvent;
import com.gmail.davideblade99.clashofminecrafters.handler.VillageHandler;
import com.gmail.davideblade99.clashofminecrafters.listener.VillageListener;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import javax.annotation.Nonnull;

public final class PlayerDeath extends VillageListener {

    public PlayerDeath(@Nonnull final CoM plugin) {
        super(plugin);
    }

    //TODO: quando un giocatore muore va portato al default spawn?
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        if (!VillageHandler.isVillageWorld(player.getWorld()))
            return;

        if (player.hasMetadata("NPC"))
            return; // The player is a NPC

        final User defender = plugin.getUser(player);
        final Village village = defender.getVillage();
        if (village != null && !village.isInsideVillage(player.getLocation())) // If player dies out of own village
        {
            // Prevent the loss of inventory and exp
            event.setDroppedExp(0);
            event.getDrops().clear();
        }

        // If player dies during a raid
        final Village attackedIsland = plugin.getWarHandler().getAttackedVillage(player);
        if (attackedIsland != null)
            Bukkit.getPluginManager().callEvent(new RaidLostEvent(player, defender));
    }
}