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
import com.gmail.davideblade99.clashofminecrafters.clan.WarHandler;
import com.gmail.davideblade99.clashofminecrafters.command.CommandFramework;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public final class WarCommand extends CommandFramework {

    public WarCommand(@Nonnull final CoM plugin) {
        super(plugin, "War");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        CommandValidator.isTrue(sender.hasPermission(Permissions.WAR_COMMAND_BASE + "state"), Messages.getMessage(MessageKey.NO_PERMISSION));
        CommandValidator.minLength(args, 1, Messages.getMessage(MessageKey.WAR_COMMAND_USAGE));


        final WarHandler warHandler = plugin.getWarHandler();
        if (warHandler.isStarted())
            MessageUtil.sendMessage(sender, Messages.getMessage(MessageKey.WAR_STARTED));
        else {
            final int timeToStart = warHandler.getTimeToStart();

            MessageUtil.sendMessage(sender, Messages.getMessage(MessageKey.STARTING_WAR, String.valueOf(timeToStart), Messages.getMessage(timeToStart == 1 ? MessageKey.SECOND : MessageKey.SECONDS)));
        }
    }
}