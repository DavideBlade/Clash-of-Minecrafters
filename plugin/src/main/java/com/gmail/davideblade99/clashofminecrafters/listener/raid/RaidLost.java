/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.raid;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.event.raid.RaidLostEvent;
import com.gmail.davideblade99.clashofminecrafters.listener.CoMListener;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.User;
import com.gmail.davideblade99.clashofminecrafters.util.Pair;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;

public final class RaidLost extends CoMListener {

    public RaidLost(@Nonnull final CoM plugin) {
        super(plugin);
    }

    /**
     * This method handles raid failure. Specifically:
     * <ul>
     *     <li>Kills the guardian and the archer (if any)</li>
     *     <li>Removes the island from the list of those under attack</li>
     *     <li>Removes trophies and currency from the player</li>
     *     <li>Teleports the player to the spawn</li>
     *     <li>Sends a message to the player</li>
     * </ul>
     */
    //TODO: al vincitore non dò trofei? e risorse?
    @EventHandler(priority = EventPriority.HIGH)
    public void onRaidFail(final RaidLostEvent event) {
        final String islandOwner = event.getDefender();
        final Player attacker = event.getAttacker();

        attacker.teleport(plugin.getConfig().getSpawn());
        MessageUtil.sendMessage(attacker, Messages.getMessage(MessageKey.RAID_FAILUIRE, islandOwner));

        plugin.getGuardianHandler().kill(islandOwner);
        plugin.getArcherHandler().kill(islandOwner);

        plugin.getWarHandler().removeUnderAttack(attacker);

        final User user = plugin.getUser(attacker);
        final Pair<Pair<Integer, Currency>, Integer> raidPenalty = plugin.getConfig().getRaidPenalty();

        user.removeBalance(raidPenalty.getKey().getKey(), raidPenalty.getKey().getValue());
        user.removeTrophies(raidPenalty.getValue());
    }
}