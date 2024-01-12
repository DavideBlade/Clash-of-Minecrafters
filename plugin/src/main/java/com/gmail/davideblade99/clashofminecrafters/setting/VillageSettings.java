/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.setting;

import javax.annotation.Nonnull;

/**
 * JavaBean that contains village settings retrieved from config.yml.
 *
 * @author DavideBlade
 * @since 3.1.4
 */
public final class VillageSettings implements Pasteable {

    private final static String SCHEMATIC_NAME = "Village";

    public final byte maxExpansions;

    public VillageSettings(final byte maxExpansions) {
        this.maxExpansions = maxExpansions;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getRelatedSchematic() {
        return SCHEMATIC_NAME;
    }
}
