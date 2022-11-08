/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.command.label;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.Permissions;
import com.gmail.davideblade99.clashofminecrafters.command.CommandFramework;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class BalanceCommand extends CommandFramework {

    public BalanceCommand(@Nonnull final CoM plugin) {
        super(plugin, "Balance");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        CommandValidator.isTrue(sender instanceof Player, Messages.getMessage(MessageKey.COMMAND_FOR_PLAYER));
        CommandValidator.isTrue(sender.hasPermission(Permissions.COMMAND_BASE + "balance"), Messages.getMessage(MessageKey.NO_PERMISSION));

        final Player player = (Player) sender;
        final User user = plugin.getUser(player);
        for (Currency currency : Currency.values()) {
            final int balance = user.getBalance(currency);
            final String currencyTranslation;
            switch (currency) {
                case GEMS:
                    currencyTranslation = Messages.getMessage(balance == 1 ? MessageKey.GEM : MessageKey.GEMS);
                    break;
                case GOLD:
                    currencyTranslation = Messages.getMessage(MessageKey.GOLD);
                    break;
                case ELIXIR:
                    currencyTranslation = Messages.getMessage(MessageKey.ELIXIR);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + currency);
            }

            ChatUtil.sendMessage(player, Messages.getMessage(MessageKey.SHOW_BALANCE, String.valueOf(balance), currencyTranslation));
        }
    }
}