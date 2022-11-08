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
import com.gmail.davideblade99.clashofminecrafters.island.building.BuildingType;
import com.gmail.davideblade99.clashofminecrafters.island.building.Extractor;
import com.gmail.davideblade99.clashofminecrafters.command.CommandFramework;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class ExtractorsCommand extends CommandFramework {

    public ExtractorsCommand(@Nonnull final CoM plugin) {
        super(plugin, "Extractors");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        CommandValidator.isTrue(sender instanceof Player, Messages.getMessage(MessageKey.COMMAND_FOR_PLAYER));
        CommandValidator.isTrue(sender.hasPermission(Permissions.COMMAND_BASE + "extractors"), Messages.getMessage(MessageKey.NO_PERMISSION));

        final User user = plugin.getUser((Player) sender);
        final Extractor goldExtractor = (Extractor) user.getBuilding(BuildingType.GOLD_EXTRACTOR);
        final Extractor elixirExtractor = (Extractor) user.getBuilding(BuildingType.ELIXIR_EXTRACTOR);

        // If the player owns at least one extractor
        CommandValidator.isTrue(goldExtractor != null || elixirExtractor != null, Messages.getMessage(MessageKey.NO_EXTRACTOR));


        if (args.length >= 1 && args[0].equalsIgnoreCase("collect")) {
            user.collectExtractors();

            ChatUtil.sendMessage(sender, Messages.getMessage(MessageKey.COLLECTED_RESOURCES));
        } else {
            // If the player bought the gold extractor
            if (goldExtractor != null)
                ChatUtil.sendMessage(sender, Messages.getMessage(MessageKey.EXTRACTORS_INFO, Messages.getMessage(MessageKey.GOLD_EXTRACTOR) + "\n", Integer.toString(goldExtractor.level), Integer.toString(goldExtractor.production), Integer.toString(user.getResourcesProduced(goldExtractor, user.getCollectionTime())), Integer.toString(goldExtractor.capacity)));

            // If the player bought the elixir extractor
            if (elixirExtractor != null)
                ChatUtil.sendMessage(sender, Messages.getMessage(MessageKey.EXTRACTORS_INFO, Messages.getMessage(MessageKey.ELIXIR_EXTRACTOR) + "\n", Integer.toString(elixirExtractor.level), Integer.toString(elixirExtractor.production), Integer.toString(user.getResourcesProduced(elixirExtractor, user.getCollectionTime())), Integer.toString(elixirExtractor.capacity)));
        }
    }
}
