/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashBiMap;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public final class PlayerHandler extends CacheLoader<String, User> {

    private final CoM plugin;
    private final UUIDMap uuidMap;
    private final LoadingCache<String, User> players; // <uuid, User>

    public PlayerHandler(@Nonnull final CoM plugin) {
        this.plugin = plugin;
        this.uuidMap = new UUIDMap(plugin);
        this.players = CacheBuilder.newBuilder().maximumSize(plugin.getConfig().getMaxPlayerCacheCount()).build(this);
    }

    @Nullable
    public String getPlayerName(@Nonnull final UUID playerUUID) {
        return uuidMap.getPlayerName(playerUUID);
    }

    @Nullable
    public UUID getPlayerUUID(@Nonnull final String playerName) {
        return uuidMap.getUUID(playerName);
    }

    @Nullable
    public User getPlayer(@Nonnull final String playerName) {
        final UUID playerUUID = getPlayerUUID(playerName);

        return playerUUID == null ? null : getPlayer(playerUUID);
    }

    /**
     * Returns the cached User related to the UUID passed. If it is not in the cache,
     * a new User will be generated with the information in the current database by calling {@link #load(String)}.
     * If there is no information (the player never entered the server) null will be returned.
     *
     * @param uuid Player UUID
     *
     * @return The User relative to the UUID or null, in case there is no information
     *
     * @see #load(String)
     */
    @Nullable
    public User getPlayer(@Nonnull final UUID uuid) {
        try {
            return players.get(uuid.toString());
        } catch (final UncheckedExecutionException | ExecutionException ignored) {
            return null; // If the player has never played before
        }
    }

    /**
     * Called each time an attempt is made to obtain an element (key)
     * from the cache that is not contained therein.
     * This method generates the value that will be associated with the searched key.
     *
     * @param playerUUID UUID of the player to be searched
     *
     * @return Return the {@link User} found
     *
     * @throws Exception Thrown if the player does not exist (= has never logged
     *                   into the server, so does not have a file)
     */
    @Nonnull
    @Override
    public User load(@Nonnull final String playerUUID) throws Exception {
        /*
         * Checking whether the player has any data stored on the plugin database
         * is better than checking whether the OfflinePlayer (Bukkit.getOfflinePlayer(UUID))
         * exists since the plugin may have been installed at a later time or the player's data
         * may have been deleted manually by the server administrator.
         * If this were the case, some players would turn out to have already played
         * (the data is still present in the default world, so Bukkit.getOfflinePlayer(UUID) != null)
         * even though this is not actually the case as far as the plugin is concerned.
         * This would result in missing default data and values (e.g., starting balance)
         * since these are only set when the player first enters.
         */
        final UUID uuid = UUID.fromString(playerUUID);
        if (!plugin.getDatabase().hasPlayedBefore(uuid))
            throw new Exception("Player not found!"); // Causes ExecutionException to be thrown in the LoadingCache#get() method

        return new User(plugin, Bukkit.getOfflinePlayer(uuid));
    }

    public void updatePlayerMapping(@Nonnull final Player player) {
        uuidMap.registerPlayer(player);
    }

    //TODO: farlo in async (vedi Essentials)
    public void loadUUIDMap() {
        uuidMap.loadAllUsers();
    }

    private final class UUIDMap {

        private final File mapFile;
        private final Pattern splitPattern = Pattern.compile(",");
        private final HashBiMap<String, UUID> names = HashBiMap.create();

        private UUIDMap(@Nonnull final CoM plugin) {
            this.mapFile = new File(plugin.getDataFolder(), "usermap.csv"); //TODO: fare così ovunque: non mettere delle costanti statiche in FileUtil
        }

        @Nullable
        private String getPlayerName(@Nonnull final UUID playerUUID) {
            return names.inverse().get(playerUUID);
        }

        @Nullable
        private UUID getUUID(@Nonnull final String playerName) {
            return names.get(playerName);
        }

        private void registerPlayer(@Nonnull final Player player) {
            final String oldName = names.inverse().get(player.getUniqueId());

            /*
             * forcePut(...) allows to replace the name of a player who has changed it.
             * In other words, if the UUID is the same but the name is different,
             * the old name is removed and the new one (associated with the UUID) is added.
             *
             * If we only use put(...), in case the uuid is already associated with the old name,
             * an exception will be thrown by the method.
             */
            names.forcePut(player.getName(), player.getUniqueId());

            if (oldName == null) // If the player is not registered/mapped
                writeEntry(player.getName(), player.getUniqueId());
            else if (!oldName.equals(player.getName())) // If the player has changed his name since the last time
                writeUUIDMap(); //TODO: se possibile fare un replace anzichè sovrascrivere l'intero file (RandomAccessFile?)
        }

        private void writeEntry(@Nonnull final String name, @Nonnull final UUID uuid) {
            //TODO: funziona correttamente? Cioè, scrive "append"?
            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(mapFile, true))) {
                writer.write(name + "," + uuid);
                writer.newLine();
            } catch (final IOException ex) {
                ex.printStackTrace(); //TODO: che altro faccio?
            }
        }

        //TODO: fare come fa Essentials: scrive su un secondo file e poi sovrascrive il vecchio
        private void writeUUIDMap() {
            if (names.size() == 0)
                return;

            try (final BufferedWriter writer = new BufferedWriter(new FileWriter(mapFile))) {
                for (Map.Entry<String, UUID> entry : names.entrySet()) {
                    writer.write(entry.getKey() + "," + entry.getValue().toString());
                    writer.newLine();
                }
            } catch (final IOException ex) {
                ex.printStackTrace(); //TODO: che altro faccio?
            }
        }

        private void loadAllUsers() {
            try {
                if (!mapFile.exists())
                    FileUtil.createFile(mapFile);

                try (final BufferedReader reader = new BufferedReader(new FileReader(mapFile))) {
                    while (true) {
                        final String line = reader.readLine();
                        if (line == null)
                            break;

                        final String[] values = splitPattern.split(line);
                        if (values.length != 2)
                            continue;

                        final String name = values[0];
                        final UUID uuid = UUID.fromString(values[1]);
                        names.put(name, uuid);
                    }
                }
            } catch (final IOException ex) {
                ex.printStackTrace(); //TODO: che altro faccio?
            }
        }
    }
}
