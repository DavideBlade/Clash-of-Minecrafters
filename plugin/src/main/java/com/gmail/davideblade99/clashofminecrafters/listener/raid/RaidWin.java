/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.raid;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.event.raid.RaidWinEvent;
import com.gmail.davideblade99.clashofminecrafters.listener.CoMListener;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.util.Pair;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;

public final class RaidWin extends CoMListener {

    public RaidWin(@Nonnull final CoM plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRaidSuccess(final RaidWinEvent event) {
        final String islandOwner = event.getDefender();
        final Player attacker = event.getAttacker();

        attacker.teleport(plugin.getConfig().getSpawn());
        MessageUtil.sendMessage(attacker, Messages.getMessage(MessageKey.RAID_SUCCESSFUL, islandOwner));

        plugin.getGuardianHandler().kill(islandOwner);
        plugin.getArcherHandler().kill(islandOwner);

        plugin.getWarHandler().removeUnderAttack(attacker);

        final User user_attacker = plugin.getUser(attacker);
        final User user_defender = plugin.getUser(islandOwner);
        final Pair<Pair<Integer, Currencies>, Integer> raidRewards = plugin.getConfig().getRaidRewards();

        user_attacker.addBalance(raidRewards.getKey().getKey(), raidRewards.getKey().getValue());
        user_attacker.addTrophies(raidRewards.getValue());
        plugin.getClanHandler().getClanByName(user_attacker.getClanName()).giveRaidExp();

        // Unexpected missing data
        if (user_defender == null)
            throw new IllegalStateException("Raid defender \"" + islandOwner + "\" missing from the database");
        user_defender.removeBalance(raidRewards.getKey().getKey(), raidRewards.getKey().getValue());
        user_defender.removeTrophies(raidRewards.getValue());
    }
}