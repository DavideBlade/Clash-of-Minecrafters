/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.command.label;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.Permissions;
import com.gmail.davideblade99.clashofminecrafters.command.CommandFramework;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class ClanCommand extends CommandFramework {

    public ClanCommand(@Nonnull final CoM plugin) {
        super(plugin, "Clan");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        CommandValidator.isTrue(sender instanceof Player, Messages.getMessage(MessageKey.COMMAND_FOR_PLAYER));
        CommandValidator.minLength(args, 1, Messages.getMessage(MessageKey.POSSIBLE_CLAN_COMMANDS));


        final Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("join")) {
            CommandValidator.isTrue(player.hasPermission(Permissions.CLAN_COMMAND_BASE + ".join"), Messages.getMessage(MessageKey.NO_PERMISSION));
            CommandValidator.minLength(args, 2, Messages.getMessage(MessageKey.CLAN_JOIN_USAGE));


            plugin.getClanHandler().joinClan(player, args[1]);
            return;
        }


        if (args[0].equalsIgnoreCase("leave")) {
            CommandValidator.isTrue(player.hasPermission(Permissions.CLAN_COMMAND_BASE + "leave"), Messages.getMessage(MessageKey.NO_PERMISSION));


            plugin.getClanHandler().leaveClan(player);
            return;
        }


        if (args[0].equalsIgnoreCase("create")) {
            CommandValidator.isTrue(player.hasPermission(Permissions.CLAN_COMMAND_BASE + "create"), Messages.getMessage(MessageKey.NO_PERMISSION));
            CommandValidator.minLength(args, 2, Messages.getMessage(MessageKey.CLAN_CREATE_USAGE));


            plugin.getClanHandler().createClan(args[1], player);
            return;
        }

        ChatUtil.sendMessage(player, Messages.getMessage(MessageKey.POSSIBLE_CLAN_COMMANDS));
    }
}