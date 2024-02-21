/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener.village;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.event.raid.RaidWinEvent;
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
 * Manages the event related to winning a raid
 *
 * @author DavideBlade
 * @since 3.2.2
 */
public final class RaidWin extends CoMListener {

    public RaidWin(@Nonnull final CoM plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRaidSuccess(final RaidWinEvent event) {
        final User defender = event.getDefender();
        final Player attacker = event.getAttacker();
        final Pair<Pair<Integer, Currencies>, Integer> raidRewards = plugin.getConfig().getRaidRewards();
        final User userAttacker = plugin.getUser(attacker);

        attacker.teleport(plugin.getConfig().getSpawn());

        plugin.getBuildingTroopRegistry().removeTroops(defender);

        plugin.getWarHandler().removeUnderAttack(attacker);

        userAttacker.addBalance(raidRewards.getKey().getKey(), raidRewards.getKey().getValue());
        userAttacker.addTrophies(raidRewards.getValue());
        plugin.getClanHandler().getClanByName(userAttacker.getClanName()).giveRaidExp();

        defender.removeBalance(raidRewards.getKey().getKey(), raidRewards.getKey().getValue());
        defender.removeTrophies(raidRewards.getValue());

        MessageUtil.sendMessage(attacker, Messages.getMessage(MessageKey.RAID_SUCCESSFUL, plugin.getPlayerHandler().getPlayerName(defender.getBase().getUniqueId())));
    }
}