/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class responsible for sending messages (to console or players), translating color codes, and prefixing the
 * plugin prefix
 */
public final class MessageUtil {

    private final static String PREFIX = "§8[§cCoM§8] " + ChatColor.RESET;

    private MessageUtil() {
        throw new IllegalAccessError();
    }

    public static void broadcast(@Nullable final String message) {
        if (message == null || message.isEmpty())
            return;

        Bukkit.broadcastMessage(PREFIX + ColorUtil.colour(message));
    }

    public static void sendMessage(@Nonnull final CommandSender receiver, @Nullable final String message) {
        if (message == null || message.isEmpty())
            return;

        receiver.sendMessage(PREFIX + ColorUtil.colour(message));
    }

    public static void sendMessage(@Nullable final String message) {
        sendMessage(Bukkit.getConsoleSender(), message);
    }

    /**
     * Send a console message with the color of warnings.
     *
     * @param message Message to send colored
     *
     * @see #sendWarning(CommandSender, String)
     * @since v3.1
     */
    public static void sendWarning(@Nullable final String message) {
        sendWarning(Bukkit.getConsoleSender(), message);
    }

    /**
     * Send a message with the color of warnings.
     *
     * @param receiver Message receiver
     * @param message  Message to send colored
     *
     * @since v3.1
     */
    public static void sendWarning(@Nonnull final CommandSender receiver, @Nullable final String message) {
        sendMessage(receiver, ChatColor.GOLD + message);
    }

    /**
     * Send a console message with the color of critical errors.
     *
     * @param message Message to send colored
     *
     * @see #sendError(CommandSender, String)
     * @since v3.1
     */
    public static void sendError(@Nullable final String message) {
        sendError(Bukkit.getConsoleSender(), message);
    }

    /**
     * Send a message with the color of critical errors.
     *
     * @param receiver Message receiver
     * @param message  Message to send colored
     *
     * @since v3.1
     */
    public static void sendError(@Nonnull final CommandSender receiver, @Nullable final String message) {
        sendMessage(receiver, ChatColor.RED + message);
    }

    /**
     * Send a console message with the color of info messages.
     *
     * @param message Message to send colored
     *
     * @see #sendInfo(CommandSender, String)
     * @since v3.1
     */
    public static void sendInfo(@Nullable final String message) {
        sendInfo(Bukkit.getConsoleSender(), message);
    }

    /**
     * Send a message with the color of info messages.
     *
     * @param receiver Message receiver
     * @param message  Message to send colored
     *
     * @since v3.1
     */
    public static void sendInfo(@Nonnull final CommandSender receiver, @Nullable final String message) {
        sendMessage(receiver, ChatColor.DARK_GRAY + message);
    }
}