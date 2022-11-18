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
import com.gmail.davideblade99.clashofminecrafters.User;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class TrophiesCommand extends CommandFramework {

    public TrophiesCommand(@Nonnull final CoM plugin) {
        super(plugin, "Trophies");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        CommandValidator.isTrue(sender instanceof Player, Messages.getMessage(MessageKey.COMMAND_FOR_PLAYER));
        CommandValidator.isTrue(sender.hasPermission(Permissions.COMMAND_BASE + "trophies"), Messages.getMessage(MessageKey.NO_PERMISSION));


        final User user = plugin.getUser(((Player) sender));
        MessageUtil.sendMessage(sender, Messages.getMessage(MessageKey.SHOW_TROPHIES, String.valueOf(user.getTrophies())));
    }
}