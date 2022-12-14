/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting.section;

import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;
import com.gmail.davideblade99.clashofminecrafters.setting.ElixirExtractorLevel;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.yaml.CoMYamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Class representing the elixir extractor section in the config.yml
 *
 * @since v3.1.1
 */
public final class ElixirExtractorConfiguration extends SectionConfiguration {

    /** Name of the section in the YAML file */
    private final static String SECTION = "Elixir extractors";


    /** List of {@link ElixirExtractorLevel}s obtained from the configuration */
    private final List<ElixirExtractorLevel> elixirExtractors;

    /**
     * Create a new instance of {@link ElixirExtractorConfiguration} and load all the elixir extractors with {@link
     * #loadElixirExtractors()}
     *
     * @param configuration Configuration containing the elixir extractors section
     */
    public ElixirExtractorConfiguration(@Nonnull final CoMYamlConfiguration configuration) {
        super(configuration, SECTION);

        this.elixirExtractors = super.section == null ? Collections.emptyList() : new ArrayList<>();

        loadElixirExtractors();
    }

    /**
     * @return The list containing the settings of the elixir extractors loaded
     */
    public List<ElixirExtractorLevel> getElixirExtractors() {
        return this.elixirExtractors;
    }

    /**
     * Reads the elixir extractors section in the {@link SectionConfiguration#section} and builds {@link
     * ElixirExtractorLevel}s. If a misconfigured level (with invalid or missing settings) is encountered, loading will
     * be stopped. Elixir extractors loaded to that point, however, will remain valid.
     */
    private void loadElixirExtractors() {
        final ConfigurationSection elixirExtractorSection = super.section;
        final Set<String> keys;

        // Check if extractor list is empty
        if (elixirExtractorSection == null || (keys = elixirExtractorSection.getKeys(false)).isEmpty()) {
            MessageUtil.sendWarning("Warning! The elixir extractor configuration is missing in the config.");
            return;
        }

        // Load elixir extractors
        short level = 0;
        for (String extractor : keys) {
            level++;

            final int production = elixirExtractorSection.getInt(extractor + ".Production", -1);
            final int capacity = elixirExtractorSection.getInt(extractor + ".Capacity", 0);
            final int price = elixirExtractorSection.getInt(extractor + ".Price", -1);
            final Currencies currency = Currencies.matchCurrency(elixirExtractorSection.getString(extractor + ".Currency", null));

            if (production < 0) {
                MessageUtil.sendError("The production of level \"" + extractor + "\" elixir extractor (in the config) cannot be negative!");
                MessageUtil.sendError("Loading of elixir extractors will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }
            if (capacity < 1) {
                MessageUtil.sendError("The capacity of level \"" + extractor + "\" elixir extractor (in the config) must be positive!");
                MessageUtil.sendError("Loading of elixir extractors will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }
            if (price < 0) {
                MessageUtil.sendError("The price of level \"" + extractor + "\" elixir extractor (in the config) cannot be negative!");
                MessageUtil.sendError("Loading of elixir extractors will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }
            if (currency == null) {
                MessageUtil.sendError("The currency set for elixir extractor \"" + extractor + "\" (in the config) does not exist.");
                MessageUtil.sendError("Loading of elixir extractors will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }

            this.elixirExtractors.add(new ElixirExtractorLevel(level, production, capacity, price, currency));
        }
    }
}
