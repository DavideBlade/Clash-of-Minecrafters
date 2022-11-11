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
import com.gmail.davideblade99.clashofminecrafters.island.Island;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class IslandCommand extends CommandFramework {

    public IslandCommand(@Nonnull final CoM plugin) {
        super(plugin, "Island");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        CommandValidator.isTrue(sender instanceof Player, Messages.getMessage(MessageKey.COMMAND_FOR_PLAYER));


        final Player player = (Player) sender;
        if (args.length == 0) {
            CommandValidator.isTrue(player.hasPermission(Permissions.COMMAND_BASE + "island"), Messages.getMessage(MessageKey.NO_PERMISSION));

            final User user = plugin.getUser(player);
            final Island island = user.getIsland();
            if (island != null) {
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.TELEPORTATION));
                island.teleportToSpawn(player);
            } else {
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.ISLAND_CREATION));
                user.createIsland();
            }

            return;
        }


        if (args[0].equalsIgnoreCase("setspawn")) {
            CommandValidator.isTrue(player.hasPermission(Permissions.ISLAND_COMMAND_BASE + "setspawn"), Messages.getMessage(MessageKey.NO_PERMISSION));


            final User user = plugin.getUser(player);

            final Location loc = player.getLocation();
            final Island playerIsland = user.getIsland();
            CommandValidator.notNull(playerIsland, Messages.getMessage(MessageKey.ISLAND_REQUIRED));
            CommandValidator.isTrue(playerIsland.canBuildOnLocation(loc), Messages.getMessage(MessageKey.CANNOT_SET_SPAWN));
            CommandValidator.isTrue(BukkitLocationUtil.isSafeLocation(loc), Messages.getMessage(MessageKey.LOCATION_NOT_SAFE));

            user.setIslandSpawn(loc);

            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.ISLAND_SPAWN_SET));
            return;
        }

        MessageUtil.sendError(player, "Unknown sub-command \"" + args[0] + "\". Use /com help.");
    }
}