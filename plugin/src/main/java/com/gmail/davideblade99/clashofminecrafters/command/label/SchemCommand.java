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
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class SchemCommand extends CommandFramework {

    public SchemCommand(@Nonnull final CoM plugin) {
        super(plugin, "Schem");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        CommandValidator.minLength(args, 1, Messages.getMessage(MessageKey.POSSIBLE_SCHEM_COMMANDS));


        if (args[0].equalsIgnoreCase("save")) {
            CommandValidator.isTrue(sender instanceof Player, Messages.getMessage(MessageKey.COMMAND_FOR_PLAYER));
            CommandValidator.isTrue(sender.hasPermission(Permissions.SCHEM_COMMAND_BASE + "save"), Messages.getMessage(MessageKey.NO_PERMISSION));
            CommandValidator.minLength(args, 8, Messages.getMessage(MessageKey.SCHEM_SAVE_USAGE));


            final Player player = (Player) sender;
            int x1, y1, z1, x2, y2, z2;

            try {
                x1 = Integer.parseInt(args[1]);
                y1 = Integer.parseInt(args[2]);
                z1 = Integer.parseInt(args[3]);
                x2 = Integer.parseInt(args[4]);
                y2 = Integer.parseInt(args[5]);
                z2 = Integer.parseInt(args[6]);
            } catch (final NumberFormatException ignored) {
                MessageUtil.sendMessage(sender, Messages.getMessage(MessageKey.INTEGER_REQUIRED));
                return;
            }

            if (Schematic.save(args[7], Schematic.getBlocks(new Location(player.getWorld(), x1, y1, z1), new Location(player.getWorld(), x2, y2, z2))))
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.SCHEMATIC_SAVED, args[7]));
            else
                MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.SAVE_ERROR, args[7]));
            return;
        }


        if (args[0].equalsIgnoreCase("list")) {
            final String[] schematics = plugin.getSchematicHandler().getSchematicList();
            CommandValidator.notNull(schematics, Messages.getMessage(MessageKey.NO_SCHEMATIC_FOUND));
            CommandValidator.minLength(schematics, 1, Messages.getMessage(MessageKey.NO_SCHEMATIC_FOUND));


            sender.sendMessage("§6----------[§cClash of Minecrafters schematics§6]----------");
            for (String schematicName : schematics)
                sender.sendMessage("§6" + schematicName);

            return;
        }

        MessageUtil.sendMessage(sender, Messages.getMessage(MessageKey.POSSIBLE_SCHEM_COMMANDS));
    }
}