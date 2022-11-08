/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.island.building;

import javax.annotation.Nonnull;

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
