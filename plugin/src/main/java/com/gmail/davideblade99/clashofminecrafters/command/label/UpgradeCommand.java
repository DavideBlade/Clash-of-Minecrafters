/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.command.label;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Permissions;
import com.gmail.davideblade99.clashofminecrafters.building.Buildings;
import com.gmail.davideblade99.clashofminecrafters.command.CommandFramework;
import com.gmail.davideblade99.clashofminecrafters.menu.UpgradeShop;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class UpgradeCommand extends CommandFramework {

    public UpgradeCommand(@Nonnull final CoM plugin) {
        super(plugin, "Upgrade");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        CommandValidator.isTrue(sender instanceof Player, Messages.getMessage(MessageKey.COMMAND_FOR_PLAYER));
        CommandValidator.isTrue(sender.hasPermission(Permissions.COMMAND_BASE + "upgrade"), Messages.getMessage(MessageKey.NO_PERMISSION));
        CommandValidator.isTrue(plugin.getConfig().anyBuildingEnabled(), Messages.getMessage(MessageKey.NO_BUILDINGS));

        final Player player = (Player) sender;
        final User user = plugin.getUser(player);
        CommandValidator.notNull(user.getVillage(), Messages.getMessage(MessageKey.ISLAND_REQUIRED));

        if (args.length == 0) {
            player.openInventory(new UpgradeShop(plugin, user).getInventory());
            return;
        }

        final Buildings building = Buildings.matchBuilding(args[0]);
        CommandValidator.notNull(building, Messages.getMessage(MessageKey.WRONG_BUILDING));

        plugin.getUpgradeManager().upgrade(player, building);
    }
}