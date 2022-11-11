/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.command;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class CommandFramework implements CommandExecutor {

    protected final CoM plugin;
    private final String label;

    protected CommandFramework(@Nonnull final CoM plugin, @Nonnull final String label) {
        this.plugin = plugin;
        this.label = label;
    }

    @Override
    public final boolean onCommand(@Nonnull final CommandSender sender, @Nonnull final Command cmd, @Nonnull final String label, @Nonnull final String[] args) {
        try {
            execute(sender, args);
        }
        catch (final CommandException e) {
            if (e.getMessage() != null && !e.getMessage().isEmpty())
                MessageUtil.sendMessage(sender, ChatColor.RED + e.getMessage());
        }

        return true;
    }

    protected abstract void execute(@Nonnull final CommandSender sender, @Nonnull final String[] args);

    public static void register(@Nonnull final JavaPlugin plugin, @Nonnull final CommandFramework command) {
        final PluginCommand pluginCommand = plugin.getCommand(command.label);
        if (pluginCommand == null)
            return;

        pluginCommand.setExecutor(command);
    }

    /***************************************************
     *
     * VALIDATE CLASS
     *
     ***************************************************/
    protected static final class CommandValidator {

        public static void notNull(@Nullable final Object o, @Nullable final String msg) {
            if (o == null)
                throw new CommandException(msg);
        }

        public static void isNull(@Nullable final Object o, @Nullable final String msg) {
            if (o != null)
                throw new CommandException(msg);
        }

        public static void isTrue(final boolean statement, @Nullable final String msg) {
            if (!statement)
                throw new CommandException(msg);
        }

        public static void isFalse(final boolean statement, @Nullable final String msg) {
            if (statement)
                throw new CommandException(msg);
        }

        public static void minLength(@Nonnull final Object[] array, final int minLength, @Nullable final String msg) {
            if (array.length < minLength)
                throw new CommandException(msg);
        }

        public static void maxLength(@Nonnull final String string, final int maxLength, @Nullable final String msg) {
            if (string.length() > maxLength)
                throw new CommandException(msg);
        }
    }


    /***************************************************
     *
     * COMMAND EXCEPTION
     *
     ***************************************************/
    private static class CommandException extends RuntimeException {

        private static final long serialVersionUID = 7690951194721461830L;

        CommandException(@Nullable final String msg) {
            super(msg);
        }
    }
}
