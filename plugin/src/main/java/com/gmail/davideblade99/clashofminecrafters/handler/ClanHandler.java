/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.Clan;
import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.User;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.yaml.ClanConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClanHandler {

    private final CoM plugin;
    private final File clanFolder;
    private final Map<String, Clan> clans = new HashMap<>(); // <clan name (lowercase), Clan>

    public ClanHandler(@Nonnull final CoM plugin) {
        this.plugin = plugin;
        this.clanFolder = new File(plugin.getDataFolder(), "Clan data");

        loadClans();
    }

    public void createClan(@Nonnull final String clanName, @Nonnull final Player owner) {
        if (clanName.contains(".")) {
            MessageUtil.sendMessage(owner, Messages.getMessage(MessageKey.CANNOT_CONTAINS_DOT));
            return;
        }
        if (clanName.length() > 30) {
            MessageUtil.sendMessage(owner, Messages.getMessage(MessageKey.MAX_LENGTH));
            return;
        }
        if (getClanByName(clanName) != null) {
            MessageUtil.sendMessage(owner, Messages.getMessage(MessageKey.CLAN_ALREADY_EXISTS));
            return;
        }

        final User user = plugin.getUser(owner);
        if (user.getClanName() != null) {
            MessageUtil.sendMessage(owner, Messages.getMessage(MessageKey.ALREADY_HAVE_CLAN));
            return;
        }

        final Clan clan = new Clan(plugin, clanName, owner.getUniqueId());
        clan.saveOnFile(); // Save clan on file
        registerClan(clan); // Store on memory

        user.setClanName(clanName);

        MessageUtil.sendMessage(owner, Messages.getMessage(MessageKey.CREATED_CLAN, clanName));
    }

    public void joinClan(@Nonnull final Player player, @Nonnull final String clanName) {
        final Clan clan = getClanByName(clanName);
        if (clan == null) {
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.CLAN_NOT_FOUND));
            return;
        }
        if (clan.getMembers().size() >= plugin.getConfig().getMaxPlayerPerClan()) {
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.FULL_CLAN));
            return;
        }

        final User user = plugin.getUser(player);
        if (user.getClanName() != null) {
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.ALREADY_HAVE_CLAN));
            return;
        }

        clan.addMember(player.getUniqueId());

        user.setClanName(clanName);

        MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.JOINED_CLAN, clanName));
    }

    public void leaveClan(@Nonnull final Player player) {
        final User user = plugin.getUser(player);

        final Clan playerClan = getClanByName(user.getClanName());
        if (playerClan == null) {
            MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.CLAN_REQUIRED));
            return;
        }

        if (playerClan.isOwner(player.getUniqueId()))
            disbandClan(playerClan);
        else {
            playerClan.removeMember(player.getUniqueId());

            user.removeClan();
        }

        MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.LEFT_CLAN, playerClan.getName()));
    }

    // Get clan from its name
    @Nullable
    public Clan getClanByName(@Nullable final String clanName) {
        if (clanName == null)
            return null;

        return clans.get(clanName.toLowerCase());
    }

    // clanName is case-sensitive! If the clan name is "HI" and clanName is "Hi", the file will not exists
    @Nonnull
    public File getClanFile(@Nonnull final String clanName) {
        return new File(clanFolder, clanName + ".yml");
    }

    @Nullable
    private File[] getClanFiles() {
        return clanFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".yml");
            }
        });
    }

    //TODO: non devo caricare tutti i clan ma solo quelli con giocatori attivi (altrimenti riempo la ram in 30 secondi)
    // Oppure magari i clan dei giocatori che sono online
    private void loadClans() {
        final File[] listOfFiles = getClanFiles();
        if (listOfFiles == null)
            return;

        for (File file : listOfFiles)
            registerClan(new Clan(plugin, file.getName().replace(".yml", ""), new ClanConfiguration(file)));
    }

    private void registerClan(@Nonnull final Clan clan) {
        clans.put(clan.getName().toLowerCase(), clan);
    }

    private void disbandClan(@Nonnull final Clan clan) {
        clans.remove(clan.getName().toLowerCase());

        // Remove clan from all members
        for (UUID member : clan.getMembers()) {
            final User user = plugin.getUser(member);
            if (user == null)
                continue; // UUID not registered -> probably the clan file has been incorrectly edited manually

            user.removeClan();
        }

        getClanFile(clan.getName()).delete();
    }
}
