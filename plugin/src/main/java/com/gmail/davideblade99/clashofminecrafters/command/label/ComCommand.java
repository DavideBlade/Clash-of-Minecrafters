/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.command.label;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.command.CommandFramework;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public final class ComCommand extends CommandFramework {

    public ComCommand(@Nonnull final CoM plugin) {
        super(plugin, "Com");
    }

    @Override
    protected void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args) {
        if (args.length == 0) {
            final String[] message = {
                    "§6----------[§cClash of Minecrafters§6]----------",
                    "§6Developer: §cDavideBlade",
                    "§6Version: §c" + plugin.getDescription().getVersion(),
                    "§6Help: §c/com help"
            };

            sender.sendMessage(message);
        } else {
            if (args[0].equalsIgnoreCase("help")) {
                if (args.length >= 2 && args[1].equalsIgnoreCase("2")) {
                    final String[] helpMessage2 = {
                            "§6-----------[§cCoM commands§6]-----------",
                            "§6/add <amount> <currency> [player] - §cAdd gems, gold or elixir.",
                            "§6/take <amount> <currency> [player] - §cRemove gems, gold or elixir.",
                            "§6/raid - §cSearch an island of enemies to attack.",
                            "§6/balance - §cGet your balance.",
                            "§6/open <menu> [player] - §cOpen specified menu.",
                            "§6/upgrade - §cOpen the menu with buildings' upgrades.",
                            "§cUse §6/com help 3 §cfor read the next page."
                    };

                    sender.sendMessage(helpMessage2);
                } else if (args.length >= 2 && args[1].equalsIgnoreCase("3")) {
                    final String[] helpMessage3 = {
                            "§6-----------[§cCoM commands§6]-----------",
                            "§6/war state - §cCheck the state of clan war.",
                            "§6/trophies - §cCheck how many trophies you have.",
                            "§6/extractors - §cShow your own extractors info.",
                            "§6/extractors collect - §cCollects the extractors' production."
                    };

                    sender.sendMessage(helpMessage3);
                } else {
                    final String[] helpMessage1 = {
                            "§6-----------[§cCoM commands§6]-----------",
                            "§6/com - §cPlugin information.",
                            "§6/clan join <clan name> - §cJoin in specified clan.",
                            "§6/clan create <clan name> - §cCreate the specified clan.",
                            "§6/clan leave - §cLeave your current clan.",
                            "§6/island - §cCreate and go to your island.",
                            "§6/island setspawn - §cSet your island spawn.",
                            "§cUse §6/com help 2 §cfor read the next page."
                    };

                    sender.sendMessage(helpMessage1);
                }

                return;
            }

            MessageUtil.sendError(sender, "Unknown sub-command \"" + args[0] + "\". Use /com help.");
        }
    }
}