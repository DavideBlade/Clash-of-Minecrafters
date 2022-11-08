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

public final class ChatUtil {

    private final static String PREFIX = "§8[§cCoM§8] " + ChatColor.RESET;

    private ChatUtil() {
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
     * @since v3.1
     */
    public static void sendWarning(@Nullable final String message) {
        sendMessage(Bukkit.getConsoleSender(), ChatColor.GOLD + message);
    }
}