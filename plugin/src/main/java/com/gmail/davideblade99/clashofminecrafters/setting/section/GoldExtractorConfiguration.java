/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting.section;

import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.setting.GoldExtractorLevel;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.yaml.CoMYamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Class representing the gold extractor section in the config.yml
 *
 * @since v3.1.1
 */
public final class GoldExtractorConfiguration extends SectionConfiguration {

    /** Name of the section in the YAML file */
    private final static String SECTION = "Gold extractors";


    /** List of {@link GoldExtractorLevel}s obtained from the configuration */
    private final List<GoldExtractorLevel> goldExtractors;

    /**
     * Create a new instance of {@link GoldExtractorConfiguration} and load all the gold extractors with {@link
     * #loadGoldExtractors()}
     *
     * @param configuration Configuration containing the gold extractors section
     */
    public GoldExtractorConfiguration(@Nonnull final CoMYamlConfiguration configuration) {
        super(configuration, SECTION);

        this.goldExtractors = super.section == null ? Collections.emptyList() : new ArrayList<>();

        loadGoldExtractors();
    }

    /**
     * @return The list containing the settings of the gold extractors loaded
     */
    public List<GoldExtractorLevel> getGoldExtractors() {
        return this.goldExtractors;
    }

    /**
     * Reads the gold extractors section in the {@link SectionConfiguration#section} and builds {@link
     * GoldExtractorLevel}s. If a misconfigured level (with invalid or missing settings) is encountered, loading will be
     * stopped. Gold extractors loaded to that point, however, will remain valid.
     */
    private void loadGoldExtractors() {
        final ConfigurationSection goldExtractorSection = super.section;
        final Set<String> keys;

        // Check if extractor list is empty
        if (goldExtractorSection == null || (keys = goldExtractorSection.getKeys(false)).isEmpty()) {
            MessageUtil.sendWarning("Warning! The gold extractor configuration is missing in the config.");
            return;
        }

        // Load gold extractors
        short level = 0;
        for (String extractor : keys) {
            level++;

            final int production = goldExtractorSection.getInt(extractor + ".Production", -1);
            final int capacity = goldExtractorSection.getInt(extractor + ".Capacity", 0);
            final int price = goldExtractorSection.getInt(extractor + ".Price", -1);
            final Currencies currency = Currencies.matchCurrency(goldExtractorSection.getString(extractor + ".Currency", null));

            if (production < 0) {
                MessageUtil.sendError("The production of level \"" + extractor + "\" gold extractor (in the config) cannot be negative!");
                MessageUtil.sendError("Loading of gold extractors will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }
            if (capacity < 1) {
                MessageUtil.sendError("The capacity of level \"" + extractor + "\" gold extractor (in the config) must be positive!");
                MessageUtil.sendError("Loading of gold extractors will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }
            if (price < 0) {
                MessageUtil.sendError("The price of level \"" + extractor + "\" gold extractor (in the config) cannot be negative!");
                MessageUtil.sendError("Loading of gold extractors will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }
            if (currency == null) {
                MessageUtil.sendError("The currency set for gold extractor \"" + extractor + "\" (in the config) does not exist.");
                MessageUtil.sendError("Loading of gold extractors will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }

            this.goldExtractors.add(new GoldExtractorLevel(level, production, capacity, price, currency));
        }
    }
}
