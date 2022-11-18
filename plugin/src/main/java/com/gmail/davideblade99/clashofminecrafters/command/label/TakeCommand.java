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
import com.gmail.davideblade99.clashofminecrafters.User;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.util.number.IntegerUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public final class TakeCommand extends CommandFramework {

    public TakeCommand(@Nonnull final CoM plugin) {
        super(plugin, "Take");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        CommandValidator.isTrue(sender.hasPermission(Permissions.COMMAND_BASE + "take"), Messages.getMessage(MessageKey.NO_PERMISSION));
        CommandValidator.minLength(args, 2, Messages.getMessage(MessageKey.TAKE_COMMAND_USAGE));

        int amount;
        try {
            amount = IntegerUtil.parseIntWithOverflow(args[0]);
        }
        catch (final IntegerOutOfBoundary ignored) {
            if (args[0].charAt(0) == '-') // If the input number is negative
                amount = Integer.MIN_VALUE;
            else
                amount = Integer.MAX_VALUE;

            MessageUtil.sendMessage(sender, Messages.getMessage(MessageKey.TOO_BIG_NUMBER, Integer.toString(amount)));
        }
        catch (final NumberFormatException ignored) {
            MessageUtil.sendMessage(sender, Messages.getMessage(MessageKey.INTEGER_REQUIRED));
            return;
        }

        final Currency currency = Currency.matchCurrency(args[1]);
        CommandValidator.notNull(currency, Messages.getMessage(MessageKey.TAKE_COMMAND_USAGE));


        if (args.length == 2) {
            CommandValidator.isTrue(sender instanceof Player, Messages.getMessage(MessageKey.COMMAND_FOR_PLAYER));

            plugin.getUser((Player) sender).removeBalance(amount, currency);
        } else {
            CommandValidator.isTrue(sender.hasPermission(Permissions.COMMAND_BASE + "take.other"), Messages.getMessage(MessageKey.NO_PERMISSION));

            final User target = plugin.getUser(args[2]);
            CommandValidator.notNull(target, Messages.getMessage(MessageKey.UNKNOWN_PLAYER, args[2]));

            final int amountSubtracted = target.removeBalance(amount, currency);
            final String currencyTranslation;
            switch (currency) {
                case GEMS:
                    currencyTranslation = Messages.getMessage(amountSubtracted == 1 ? MessageKey.GEM : MessageKey.GEMS);
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
            MessageUtil.sendMessage(sender, Messages.getMessage(MessageKey.REMVOED_FROM_OTHERS_BALANCE, Integer.toString(amountSubtracted), currencyTranslation, target.getBase().getName()));
        }
    }
}