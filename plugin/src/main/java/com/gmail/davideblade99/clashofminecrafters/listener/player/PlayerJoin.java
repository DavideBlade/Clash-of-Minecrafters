/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.player;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.listener.CoMListener;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.setting.Config;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ScoreboardUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nonnull;

import static com.gmail.davideblade99.clashofminecrafters.Currency.*;

public final class PlayerJoin extends CoMListener {

    public PlayerJoin(@Nonnull final CoM plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final User user = plugin.getUser(player);

        // Update map between player name <-> UUID
        plugin.getPlayerHandler().updatePlayerMapping(player);

        final Config config = plugin.getConfig();
        if (config.teleportOnJoin())
            player.teleport(config.getSpawn());

        player.setScoreboard(ScoreboardUtil.createScoreboard(user.getBalance(GEMS), user.getBalance(GOLD), user.getBalance(ELIXIR), user.getTrophies()));
    }
}