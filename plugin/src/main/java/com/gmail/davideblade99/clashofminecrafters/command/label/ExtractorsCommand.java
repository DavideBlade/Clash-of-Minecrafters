/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.command.label;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Permissions;
import com.gmail.davideblade99.clashofminecrafters.building.ElixirExtractor;
import com.gmail.davideblade99.clashofminecrafters.building.GoldExtractor;
import com.gmail.davideblade99.clashofminecrafters.command.CommandFramework;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.setting.ElixirExtractorLevel;
import com.gmail.davideblade99.clashofminecrafters.setting.GoldExtractorLevel;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
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
        CommandValidator.isTrue(plugin.getConfig().isGoldExtractorEnabled() || plugin.getConfig().isElixirExtractorEnabled(), Messages.getMessage(MessageKey.DISABLED_EXTRACTORS));

        final User user = plugin.getUser((Player) sender);

        CommandValidator.isTrue(user.hasExtractor(), Messages.getMessage(MessageKey.NO_EXTRACTOR)); // If the player owns at least one extractor

        if (args.length >= 1 && args[0].equalsIgnoreCase("collect")) {
            user.collectExtractors();

            MessageUtil.sendMessage(sender, Messages.getMessage(MessageKey.COLLECTED_RESOURCES));
        } else {
            final GoldExtractor goldExtractor = user.getGoldExtractor();
            final ElixirExtractor elixirExtractor = user.getElixirExtractor();

            // If the player unlocked the gold extractor
            if (goldExtractor != null) {
                final GoldExtractorLevel goldExtractorStats = plugin.getConfig().getExistingGoldExtractor(goldExtractor.getLevel());

                MessageUtil.sendMessage(sender, Messages.getMessage(MessageKey.EXTRACTORS_INFO, Messages.getMessage(MessageKey.GOLD_EXTRACTOR) + "\n", Integer.toString(goldExtractorStats.level), Integer.toString(goldExtractorStats.production), Integer.toString(user.getResourcesProduced(goldExtractorStats, user.getCollectionTime())), Integer.toString(goldExtractorStats.capacity)));
            }

            // If the player unlocked the elixir extractor
            if (elixirExtractor != null) {
                final ElixirExtractorLevel elixirExtractorStats = plugin.getConfig().getExistingElixirExtractor(elixirExtractor.getLevel());

                MessageUtil.sendMessage(sender, Messages.getMessage(MessageKey.EXTRACTORS_INFO, Messages.getMessage(MessageKey.ELIXIR_EXTRACTOR) + "\n", Integer.toString(elixirExtractorStats.level), Integer.toString(elixirExtractorStats.production), Integer.toString(user.getResourcesProduced(elixirExtractorStats, user.getCollectionTime())), Integer.toString(elixirExtractorStats.capacity)));
            }
        }
    }
}
