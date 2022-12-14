/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting.section;

import com.gmail.davideblade99.clashofminecrafters.setting.ClanLevel;
import com.gmail.davideblade99.clashofminecrafters.util.Pair;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.yaml.CoMYamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Class representing the clan section in the config.yml
 *
 * @since v3.1.1
 */
public final class ClanConfiguration extends SectionConfiguration {

    /** Name of the section in the YAML file */
    private final static String SECTION = "Clans";


    /**
     * List of {@link ClanLevel}s extracted from the configuration representing, for each level, the exp
     * required and the command to be executed when a clan reaches the level.
     */
    private final List<ClanLevel> clans;

    /**
     * Create a new instance of {@link ClanConfiguration} and load all the clans with {@link #loadClans()}
     *
     * @param configuration Configuration containing the clans section
     */
    public ClanConfiguration(@Nonnull final CoMYamlConfiguration configuration) {
        super(configuration, SECTION);

        this.clans = super.section == null ? Collections.emptyList() : new ArrayList<>();

        loadClans();
    }

    /**
     * @return The list containing the settings of the clans loaded
     *
     * @since v3.1.2
     */
    public List<ClanLevel> getClans() {
        return this.clans;
    }

    /**
     * Reads the clans section in the {@link SectionConfiguration#section} and builds {@link Pair}s of required exp
     * and commands to be executed when the level is reached. If a misconfigured level (with invalid or missing
     * settings) is encountered, loading will be stopped. Clans loaded to that point, however, will remain valid.
     */
    private void loadClans() {
        final ConfigurationSection clanSection = super.section;
        final Set<String> keys;

        // Check if clans list is empty
        if (clanSection == null || (keys = clanSection.getKeys(false)).isEmpty()) {
            MessageUtil.sendWarning("Warning! The configuration of the clans is missing in the config.");
            return;
        }

        short level = 1;
        for (String clan : keys) {
            level++;

            final int expRequired = clanSection.getInt(clan + ".Exp required", -1);
            final String command = clanSection.getString(clan + ".Command", null);

            if (expRequired < 0) {
                MessageUtil.sendError("\"Exp required\" of the clan \"" + clan + "\" (in the config) cannot be negative!");
                MessageUtil.sendError("Loading of clans will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }

            this.clans.add(new ClanLevel(expRequired, command));
        }
    }
}
