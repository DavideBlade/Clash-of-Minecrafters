/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.player;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.setting.Settings;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.util.collection.Sets;
import com.gmail.davideblade99.clashofminecrafters.yaml.ClanConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents an existing clan
 *
 * @author DavideBlade
 * @since 3.1.4
 */
public final class Clan {

    private final CoM plugin;
    private final String clanName;
    private final UUID owner;
    private final HashSet<UUID> members; // This set contains even the owner
    private int level;
    private int exp;

    public Clan(@Nonnull final CoM plugin, @Nonnull final String clanName, @Nonnull final ClanConfiguration config) {
        this(plugin, clanName, config.getOwner(), config.getMembers(), config.getLevel(), config.getExp());
    }

    public Clan(@Nonnull final CoM plugin, @Nonnull final String clanName, @Nonnull final UUID owner) {
        this(plugin, clanName, owner, 1, 0);
    }

    private Clan(@Nonnull final CoM plugin, @Nonnull final String clanName, @Nonnull final UUID owner, final int level, final int exp) {
        this(plugin, clanName, owner, Sets.newHashSet(owner), level, exp);
    }

    // The list of members must also contain the owner
    private Clan(@Nonnull final CoM plugin, @Nonnull final String clanName, @Nonnull final UUID owner, @Nonnull final HashSet<UUID> members, final int level, final int exp) {
        this.plugin = plugin;
        this.clanName = clanName;
        this.owner = owner;
        this.members = members;
        this.level = level;
        this.exp = exp;
    }

    public void giveRaidExp() {
        final Settings config = plugin.getConfig();
        if (level >= config.getClanLevels()) // If the clan already has the highest level
            return;

        final ClanConfiguration clanConfig = new ClanConfiguration(plugin.getClanHandler().getClanFile(clanName));

        // Set exp
        exp += config.getClanRaidRewards();
        clanConfig.setExp(exp);

        // If clan levels exist and the clan has enough exp to level up
        if (exp >= config.getRequiredClanExp(level + 1)) {
            // Increase the level
            clanConfig.setLevel(++level);

            // Execute the command specified in config.yml
            final String command = config.getClanCommand(level);
            if (command != null)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%clan", clanName));

            // Broadcast to clan member
            for (UUID uuid : members) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player != null) // Player online
                    MessageUtil.sendMessage(player, Messages.getMessage(MessageKey.INCREASED_LEVEL, String.valueOf(level)));
            }
        }
        clanConfig.save();
    }

    @Nonnull
    public String getName() {
        return clanName;
    }

    @Nonnull
    public Set<UUID> getMembers() {
        return members;
    }

    public void addMember(@Nonnull final UUID newMember) {
        if (!members.add(newMember)) // Updates the file only if the set has been modified (if it did not contain the player)
            return;

        new ClanConfiguration(plugin.getClanHandler().getClanFile(clanName), true).setMembers(members);
    }

    public void removeMember(@Nonnull final UUID member) {
        if (!members.remove(member)) // Updates the file only if the set has been modified (if it contained the player)
            return;

        new ClanConfiguration(plugin.getClanHandler().getClanFile(clanName), true).setMembers(members);
    }

    public boolean isOwner(@Nonnull final UUID uuid) {
        return owner.equals(uuid);
    }

    // Set data on file
    public void saveOnFile() {
        final ClanConfiguration clanConfiguration = new ClanConfiguration(plugin.getClanHandler().getClanFile(clanName));
        clanConfiguration.setOwner(owner);
        clanConfiguration.setMembers(members);
        clanConfiguration.setLevel(level);
        clanConfiguration.setExp(exp);
        clanConfiguration.save();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof Clan))
            return false;
        else {
            final Clan clan = (Clan) obj;
            return clanName.equalsIgnoreCase(clan.clanName);
        }
    }
}