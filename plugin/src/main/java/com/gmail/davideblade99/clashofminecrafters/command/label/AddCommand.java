/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.command.label;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.Permissions;
import com.gmail.davideblade99.clashofminecrafters.command.CommandFramework;
import com.gmail.davideblade99.clashofminecrafters.exception.IntegerOutOfBoundary;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ChatUtil;
import com.gmail.davideblade99.clashofminecrafters.util.number.IntegerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class AddCommand extends CommandFramework {

    public AddCommand(@Nonnull final CoM plugin) {
        super(plugin, "Add");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        CommandValidator.isTrue(sender.hasPermission(Permissions.COMMAND_BASE + "add"), Messages.getMessage(MessageKey.NO_PERMISSION));
        CommandValidator.minLength(args, 2, Messages.getMessage(MessageKey.ADD_COMMAND_USAGE));


        int amount;
        try {
            amount = IntegerUtil.parseIntWithOverflow(args[0]);
        }
        catch (final IntegerOutOfBoundary ignored) {
            if (args[0].charAt(0) == '-') // If the input number is negative
                amount = Integer.MIN_VALUE;
            else
                amount = Integer.MAX_VALUE;
            ChatUtil.sendMessage(sender, Messages.getMessage(MessageKey.TOO_BIG_NUMBER, Integer.toString(amount)));
        }
        catch (final NumberFormatException ignored) {
            ChatUtil.sendMessage(sender, Messages.getMessage(MessageKey.INTEGER_REQUIRED));
            return;
        }

        final Currency currency = Currency.matchCurrency(args[1]);
        CommandValidator.notNull(currency, Messages.getMessage(MessageKey.ADD_COMMAND_USAGE));

        if (args.length == 2) {
            CommandValidator.isTrue(sender instanceof Player, Messages.getMessage(MessageKey.COMMAND_FOR_PLAYER));

            plugin.getUser((Player) sender).addBalance(amount, currency);
        } else {
            CommandValidator.isTrue(sender.hasPermission(Permissions.COMMAND_BASE + "add.other"), Messages.getMessage(MessageKey.NO_PERMISSION));

            final User target = plugin.getUser(args[2]);
            CommandValidator.notNull(target, Messages.getMessage(MessageKey.UNKNOWN_PLAYER, args[2]));

            final int amountAdded = target.addBalance(amount, currency);
            final String currencyTranslation;
            switch (currency) {
                case GEMS:
                    currencyTranslation = Messages.getMessage(amountAdded == 1 ? MessageKey.GEM : MessageKey.GEMS);
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
            ChatUtil.sendMessage(sender, Messages.getMessage(MessageKey.ADDED_TO_OTHERS_BALANCE, Integer.toString(amountAdded), currencyTranslation, target.getBase().getName()));
        }
    }
}