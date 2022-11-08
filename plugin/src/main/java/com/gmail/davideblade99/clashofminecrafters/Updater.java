/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters;

import com.gmail.davideblade99.clashofminecrafters.util.bukkit.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Updater {

    private final CoM plugin;

    public Updater(@Nonnull final CoM plugin) {
        this.plugin = plugin;
    }

    public interface ResponseHandler {

        /**
         * Called when the updater finds a new version.
         *
         * @param newVersion - the new version
         */
        void onUpdateFound(@Nonnull final String newVersion);
    }

    public void checkForUpdate(@Nonnull final ResponseHandler responseHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=31180").openConnection();
                    final String newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();

                    if (isNewerVersion(newVersion)) {
                        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                            @Override
                            public void run() {
                                if (newVersion.contains(" "))
                                    responseHandler.onUpdateFound(newVersion.split(" ")[0]);
                                else
                                    responseHandler.onUpdateFound(newVersion);
                            }
                        });
                    }
                }
                catch (final IOException e) {
                    ChatUtil.sendMessage("&cCould not contact Spigot to check for updates.");
                }
                catch (final IllegalPluginAccessException ignored) {
                    // Plugin not enabled
                }
                catch (final Exception e) {
                    e.printStackTrace();
                    ChatUtil.sendMessage("&cUnable to check for updates: unhandled exception.");
                }
            }
        }).start();
    }

    /**
     * Compare the version found with the plugin's version
     */
    private boolean isNewerVersion(@Nullable final String versionOfSpigot) {
        return !plugin.getDescription().getVersion().equals(versionOfSpigot);
    }
}