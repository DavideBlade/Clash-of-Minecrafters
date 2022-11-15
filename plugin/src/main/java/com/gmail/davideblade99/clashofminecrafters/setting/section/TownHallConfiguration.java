/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting.section;

import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.island.building.TownHall;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.yaml.CoMYamlConfiguration;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Class representing the town hall section in the config.yml
 *
 * @author DavideBlade
 * @since v3.1.1
 */
public final class TownHallConfiguration extends SectionConfiguration {

    /** Name of the section in the YAML file */
    private final static String SECTION = "Town halls";


    /** List of {@link TownHall}s extracted from the configuration */
    private final List<TownHall> townHalls;

    /**
     * Create a new instance of {@link TownHallConfiguration} and load all the town halls with {@link
     * #loadTownHalls()}
     *
     * @param configuration Configuration containing the town halls section
     */
    public TownHallConfiguration(@Nonnull final CoMYamlConfiguration configuration) {
        super(configuration, SECTION);

        this.townHalls = super.section == null ? Collections.emptyList() : new ArrayList<>();

        loadTownHalls();
    }

    /**
     * @return The list containing the settings of the town halls loaded
     */
    public List<TownHall> getTownHalls() {
        return this.townHalls;
    }

    /**
     * Reads the town halls section in the {@link SectionConfiguration#section} and builds {@link TownHall}s. If a
     * misconfigured level (with invalid or missing settings) is encountered, loading will be stopped. Town halls
     * loaded to that point, however, will remain valid.
     */
    private void loadTownHalls() {
        final ConfigurationSection townHallSection = super.section;
        final Set<String> keys;

        // Check if town hall list is empty
        if (townHallSection == null || (keys = townHallSection.getKeys(false)).isEmpty()) {
            MessageUtil.sendWarning("Warning! The town hall configuration is missing in the config.");
            return;
        }

        // Load town hall levels
        short level = 1;
        for (String townHall : keys) {
            level++;

            final int price = townHallSection.getInt(townHall + ".Price", -1);
            final Currency currency = Currency.matchCurrency(townHallSection.getString(townHall + ".Currency", null));
            final String command = townHallSection.getString(townHall + ".Command", null);
            final byte hearts = (byte) townHallSection.getInt(townHall + ".Guardian.Health", -1); //TODO: differenziare il caso in cui è -1 perché non lo ha configurato (ammesso) o perché ha settato lui -1
            final Material helmet = Material.matchMaterial(townHallSection.getString(townHall + ".Guardian.Equipment.Helmet", ""));
            final Material chestplate = Material.matchMaterial(townHallSection.getString(townHall + ".Guardian.Equipment.Chestplate", ""));
            final Material leggings = Material.matchMaterial(townHallSection.getString(townHall + ".Guardian.Equipment.Leggings", ""));
            final Material boots = Material.matchMaterial(townHallSection.getString(townHall + ".Guardian.Equipment.Boots", ""));

            final List<?> effectString = townHallSection.getList(townHall + ".Guardian.Equipment.Potions", null);
            final List<PotionEffectType> potions;
            if (effectString == null)
                potions = null;
            else {
                potions = new ArrayList<>(effectString.size());
                for (Object obj : effectString) {
                    if (!(obj instanceof String))
                        continue;

                    final PotionEffectType potion = PotionEffectType.getByName((String) obj);
                    if (potion != null)
                        potions.add(potion);
                    else
                        MessageUtil.sendWarning("Warning! The potion effect \"" + obj + "\" of level \"" + townHall + "\" town hall (in the config) will be ignored, because it does not exist.");
                }
            }


            if (price < 0) {
                MessageUtil.sendError("The price of level \"" + townHall + "\" town hall (in the config) cannot be negative!");
                MessageUtil.sendError("Loading of town halls will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }
            if (currency == null) {
                MessageUtil.sendError("The currency set for town hall \"" + townHall + "\" (in the config) does not exist.");
                MessageUtil.sendError("Loading of town halls will be stopped. Only the first " + (level - 1) + " levels will remain valid.");
                return;
            }

            this.townHalls.add(new TownHall(level, price, currency, command, hearts, helmet, chestplate, leggings, boots, potions));
        }
    }
}