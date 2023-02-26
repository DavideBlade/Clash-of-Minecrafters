/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.command.label;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Permissions;
import com.gmail.davideblade99.clashofminecrafters.command.CommandFramework;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
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
        for (Currencies currency : Currencies.values()) {
            final int balance = user.getBalance(currency);
            final String currencyTranslation = user.getBalance().getCurrencyTranslation(currency);

            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.SHOW_BALANCE, String.valueOf(balance), currencyTranslation));
        }
    }
}