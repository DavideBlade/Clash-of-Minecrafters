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
import com.gmail.davideblade99.clashofminecrafters.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public final class OpenCommand extends CommandFramework {

    public OpenCommand(@Nonnull final CoM plugin) {
        super(plugin, "Open");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        CommandValidator.isTrue(sender.hasPermission(Permissions.COMMAND_BASE + "open"), Messages.getMessage(MessageKey.NO_PERMISSION));
        CommandValidator.minLength(args, 1, Messages.getMessage(MessageKey.OPEN_COMMAND_USAGE));

        final Menu menu = plugin.getConfig().getMenu(args[0]); // args[0] = menu name
        CommandValidator.notNull(menu, Messages.getMessage(MessageKey.MENU_NOT_FOUND));


        final Player target;
        if (args.length == 1) {
            CommandValidator.isTrue(sender instanceof Player, Messages.getMessage(MessageKey.COMMAND_FOR_PLAYER));

            target = ((Player) sender);
        } else {
            CommandValidator.isTrue(sender.hasPermission(Permissions.COMMAND_BASE + "open.other"), Messages.getMessage(MessageKey.NO_PERMISSION));

            target = Bukkit.getPlayer(args[1]);
            CommandValidator.notNull(target, Messages.getMessage(MessageKey.PLAYER_NOT_ONLINE, args[1]));
        }

        /*
         * Create the menu (Menu#getInventory()) in async because if
         * there are heads (item), a http request is made to the Mojang server
         * to download the texture (skin), if not already in the server cache.
         *
         * Textures remain in the server cache for 60 minutes after the last use.
         */
        new BukkitRunnable() {
            @Override
            public void run() {
                final Inventory inventory = menu.getInventory();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        target.openInventory(inventory);
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }
}