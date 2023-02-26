/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.command.label;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Permissions;
import com.gmail.davideblade99.clashofminecrafters.command.CommandFramework;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class RaidCommand extends CommandFramework {

    public RaidCommand(@Nonnull final CoM plugin) {
        super(plugin, "Raid");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        CommandValidator.isTrue(sender instanceof Player, Messages.getMessage(MessageKey.COMMAND_FOR_PLAYER));
        CommandValidator.isTrue(sender.hasPermission(Permissions.COMMAND_BASE + "raid"), Messages.getMessage(MessageKey.NO_PERMISSION));


        final Player player = (Player) sender;
        final User user = plugin.getUser(player);

        CommandValidator.notNull(user.getClanName(), Messages.getMessage(MessageKey.CLAN_REQUIRED));
        CommandValidator.isTrue(plugin.getWarHandler().isStarted(), Messages.getMessage(MessageKey.WAR_REQUIRED));
        CommandValidator.notNull(user.getVillage(), Messages.getMessage(MessageKey.ISLAND_REQUIRED));
        CommandValidator.isFalse(plugin.getWarHandler().isAttacking(player), Messages.getMessage(MessageKey.ALREADY_ATTACKING));


        MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.SEARCHING));
        plugin.getWarHandler().startRaid(player);
    }
}