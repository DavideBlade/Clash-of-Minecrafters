/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.player;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Island;
import com.gmail.davideblade99.clashofminecrafters.listener.CoMListener;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ColorUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import javax.annotation.Nonnull;

public final class AsyncPlayerPreLogin extends CoMListener {

    public AsyncPlayerPreLogin(@Nonnull final CoM plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
        /*
         * Note 1: the player has not yet joined the server so the list of online players
         * does not contain him (Bukkit.getPlayer(event.getUniqueId()) = null).
         *
         * Note 2: since the player has not yet entered the server, the cached User will have a
         * reference to OfflinePlayer (only if the player is not the first time on the server).
         * If the player has never entered the server this problem does not arise because
         * the OfflinePlayer will not exist and therefore the User (with reference to the OfflinePlayer)
         * will not be created until the player actually enters the server (and at that point
         * will be a Player instead of an OfflinePlayer).
         * (If the player is online but the reference is still to OfflinePlayer some problems occur
         * as messages are not sent, teleports not executed, etc.)
         *
         * Note 3: the player will not be allowed to enter (i.e. see the Minecraft loading screen) until this method returns
         *
         * Note 4: the fact that the player has to wait on the Minecraft screen (note 3) is not necessarily
         * a bad thing: if the database is slow, it is better for the player to wait longer before entering
         * the server than every time when performing operations (e.g., executing plugin commands).
         * In fact, when #getUser() is performed, it will be fetched the player data from the database and they
         * will be cached. By doing so, the player will no longer have to wait (unless the cache fills up).
         */
        final User user = plugin.getUser(event.getUniqueId());
        if (user == null)
            return;

        final Island island = user.getIsland();
        if (island == null)
            return;

        if (plugin.getWarHandler().isUnderAttack(island))
            event.disallow(Result.KICK_OTHER, ColorUtil.colour(Messages.getMessage(MessageKey.ISLAND_UNDER_ATTACK)));
    }
}