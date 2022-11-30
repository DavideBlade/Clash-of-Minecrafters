/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting.bean;

import com.gmail.davideblade99.clashofminecrafters.player.currency.Currencies;

import javax.annotation.Nonnull;

/**
 * JavaBean that contains the extractor shared settings retrieved from config.yml.
 *
 * @author DavideBlade
 * @since v3.1.2
 */
public abstract class ExtractorSettings extends BuildingSettings {

    public final int production;
    public final int capacity;

    public ExtractorSettings(final int level, final int production, final int capacity, final int price, @Nonnull final Currencies currency) {
        super(level, price, currency);

        this.production = production;
        this.capacity = capacity;
    }
}