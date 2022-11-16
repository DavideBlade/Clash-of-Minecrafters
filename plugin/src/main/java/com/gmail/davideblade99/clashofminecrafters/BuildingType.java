/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters;

import javax.annotation.Nonnull;

/**
 * Enumerates all types of buildings.
 *
 * @author DavideBlade
 * @since v3.1.2
 */
public enum BuildingType {
    TOWN_HALL, ARCHER_TOWER, GOLD_EXTRACTOR, ELIXIR_EXTRACTOR;

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * @return an array containing only those buildings that are extractors
     */
    @Nonnull
    public static BuildingType[] getExtractors() {
        return new BuildingType[]{GOLD_EXTRACTOR, ELIXIR_EXTRACTOR};
    }
}
