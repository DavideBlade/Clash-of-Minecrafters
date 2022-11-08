/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.message;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ChatUtil;
import com.gmail.davideblade99.clashofminecrafters.util.EnumUtil;
import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

/**
 * Class for retrieving translatable messages in message_xx.yml files
 */
public final class Messages {

    private final static FileConfiguration MESSAGES = new YamlConfiguration();

    private final CoM plugin;

    public Messages(@Nonnull final CoM plugin) {
        this.plugin = plugin;

        setupMessages();
    }

    @Nonnull
    public static String getMessage(@Nonnull final MessageKey message) {
        final String text = MESSAGES.getString(message.getPath());
        if (text == null || text.isEmpty())
            return "&cMissing message: \"" + message + "\"";

        return text;
    }

    @Nonnull
    public static String getMessage(@Nonnull final MessageKey message, @Nonnull final String... replacements) {
        String text = getMessage(message);
        final String[] tags = message.getTags();
        if (tags == null || replacements.length != tags.length)
            throw new IllegalArgumentException("Invalid number of replacements for the message \"" + message + "\"");

        for (int i = 0; i < tags.length; i++)
            text = text.replace(tags[i], replacements[i]);

        return text;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void setupMessages() {
        final String extension = plugin.getConfig().getLang().toString();
        final File messagesFile = new File(plugin.getDataFolder() + "/Messages", "messages_" + extension + ".yml");
        final File messagesFolder = new File(plugin.getDataFolder(), "Messages");

        if (!messagesFolder.exists())
            messagesFolder.mkdirs();
        if (!messagesFile.exists())
            FileUtil.copyFile("messages_" + extension + ".yml", messagesFile);

        load(messagesFile);

        // Check all messages
        for (MessageKey message : MessageKey.values()) {
            if (!MESSAGES.isSet(message.getPath())) {
                final File broken = new File(plugin.getDataFolder() + "/Messages", "messages_" + extension + ".broken." + System.currentTimeMillis());
                messagesFile.renameTo(broken);

                ChatUtil.sendMessage("&cNot found all messages in messages_" + extension + ". It has been renamed to " + broken.getName());
                ChatUtil.sendMessage("&cIt was created a new messages_" + extension + ".yml file.");

                FileUtil.copyFile("messages_" + extension + ".yml", messagesFile);

                load(messagesFile);
                break;
            }
        }
    }

    private void load(@Nonnull final File messagesFile) {
        try {
            MESSAGES.load(messagesFile);
        }
        catch (final Exception e) {
            e.printStackTrace();

            ChatUtil.sendMessage("&cFailed to load " + messagesFile.getName() + ".");
            ChatUtil.sendMessage("&cClash of minecrafters " + plugin.getDescription().getVersion() + " was disabled.");

            plugin.disablePlugin();
        }
    }

    public enum Language {
        EN, IT;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        @Nullable
        public static Language matchLanguage(@Nullable final String lang) {
            return EnumUtil.getEnumIgnoreCase(lang, Language.class, null);
        }
    }
}