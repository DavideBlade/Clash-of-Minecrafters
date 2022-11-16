/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting.section;

import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.setting.bean.ArcherTowerSettings;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.yaml.CoMYamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Class representing the archer tower section in the config.yml
 *
 * @since v3.1.1
 */
public final class ArcherTowerConfiguration extends SectionConfiguration {

    /** Name of the section in the YAML file */
    private final static String SECTION = "Archer towers";


    /** List of {@link ArcherTowerSettings}s extracted from the configuration */
    private final List<ArcherTowerSettings> archerTowers;

    /**
     * Create a new instance of {@link ArcherTowerConfiguration} and load all the archer towers with {@link
     * #loadArcherTowers()}
     *
     * @param configuration Configuration containing the archer towers section
     */
    public ArcherTowerConfiguration(@Nonnull final CoMYamlConfiguration configuration) {
        super(configuration, SECTION);

        this.archerTowers = super.section == null ? Collections.emptyList() : new ArrayList<>();

        loadArcherTowers();
    }

    /**
     * @return The list containing the settings of the archer towers loaded
     */
    public List<ArcherTowerSettings> getArcherTowers() {
        return this.archerTowers;
    }

    /**
     * Reads the archer towers section in the {@link SectionConfiguration#section} and builds {@link ArcherTowerSettings}s.
     * If a misconfigured level (with invalid or missing settings) is encountered, loading will be stopped. Archer
     * towers loaded to that point, however, will remain valid.
     */
    private void loadArcherTowers() {
        final ConfigurationSection archerSection = super.section;
        final Set<String> keys;

        // Check if archer tower list is empty
        if (archerSection == null || (keys = archerSection.getKeys(false)).isEmpty()) {
            MessageUtil.sendWarning("Warning! The configuration of the archer towers is missing in the config.");
            return;
        }

        // Load archer towers
        int level = 0;
        for (String archerTower : keys) {
            level++;

            final double damage = archerSection.getDouble(archerTower + ".Damage", -1);
            final int price = archerSection.getInt(archerTower + ".Price", -1);
            final Currency currency = Currency.matchCurrency(archerSection.getString(archerTower + ".Currency", null));

            if (damage < 0) {
                MessageUtil.sendError("The damage of the archer tower \"" + archerTower + "\" (in the config) cannot be negative!");
                MessageUtil.sendError("Loading of archer towers will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }
            if (price < 0) {
                MessageUtil.sendError("The price for the archer tower \"" + archerTower + "\" (in the config) cannot be negative!");
                MessageUtil.sendError("Loading of archer towers will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }
            if (currency == null) {
                MessageUtil.sendError("The currency set for the archer tower \"" + archerTower + "\" (in the config) does not exist.");
                MessageUtil.sendError("Loading of archer towers will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }

            this.archerTowers.add(new ArcherTowerSettings(level, damage, price, currency));
        }
    }
}
