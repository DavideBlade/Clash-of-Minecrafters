/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.village;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.event.raid.RaidLostEvent;
import com.gmail.davideblade99.clashofminecrafters.listener.CoMListener;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.util.Pair;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;

/**
 * Manages the event related to the loss of a raid
 *
 * @author DavideBlade
 * @since 3.2.2
 */
public final class RaidLost extends CoMListener {

    public RaidLost(@Nonnull final CoM plugin) {
        super(plugin);
    }

    /**
     * This method handles raid failure. Specifically:
     * <ul>
     *     <li>Kills the guardian and the archer (if any)</li>
     *     <li>Removes the island from the list of those under attack</li>
     *     <li>Removes trophies and currency from the attacker</li>
     *     <li>Adds trophies and currency to the defender</li>
     *     <li>Teleports the player to the spawn</li>
     *     <li>Sends a message to the player</li>
     * </ul>
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onRaidFail(final RaidLostEvent event) {
        final User defender = event.getDefender();
        final Player attacker = event.getAttacker();
        final Pair<Pair<Integer, Currencies>, Integer> raidPenalty = plugin.getConfig().getRaidPenalty();
        final User userAttacker = plugin.getUser(attacker);

        attacker.teleport(plugin.getConfig().getSpawn());

        plugin.getBuildingTroopRegistry().removeTroops(defender);

        plugin.getWarHandler().removeUnderAttack(attacker);

        userAttacker.removeBalance(raidPenalty.getKey().getKey(), raidPenalty.getKey().getValue());
        userAttacker.removeTrophies(raidPenalty.getValue());

        defender.addBalance(raidPenalty.getKey().getKey(), raidPenalty.getKey().getValue());
        defender.addTrophies(raidPenalty.getValue());

        MessageUtil.sendMessage(attacker, Messages.getMessage(MessageKey.RAID_FAILUIRE, defender.getBase().getName()));
    }
}