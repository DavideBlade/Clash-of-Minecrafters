/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.building;

import com.gmail.davideblade99.clashofminecrafters.util.EnumUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Enumerates all types of buildings
 *
 * @author DavideBlade
 * @since v3.1.4
 */
public enum Buildings {
    TOWN_HALL((byte) 1), ARCHER_TOWER((byte) 0), GOLD_EXTRACTOR((byte) 0), ELIXIR_EXTRACTOR((byte) 0);

    public final byte firstLevel;

    /**
     * Creates a new building type with the specified base level
     *
     * @param firstLevel Starting level of the building. Basically, it is the level that a player gets when he
     *                   joins the server for the first time and has not yet unlocked any building.
     */
    Buildings(final byte firstLevel) {
        this.firstLevel = firstLevel;
    }

    /**
     * @return an array containing only those buildings that are extractors
     */
    @Nonnull
    public static Buildings[] getExtractors() {
        return new Buildings[]{GOLD_EXTRACTOR, ELIXIR_EXTRACTOR};
    }

    /**
     * Attempts to match {@link Buildings} from the passed string, ignoring uppercase and lowercase letters.
     *
     * @param building Building name
     *
     * @return The {@link Buildings} if one is found, otherwise {@code null}
     */
    @Nullable
    public static Buildings matchBuilding(@Nullable final String building) {
        return matchOrDefault(building, null);
    }

    /**
     * Attempts to match {@link Buildings} from the passed string, ignoring uppercase and lowercase letters.
     *
     * @param building Building name
     * @param def      Default value that will be returned if no match is found
     *
     * @return The {@link Buildings}, if one was found, otherwise the default value {@code def}
     */
    @Nullable
    private static Buildings matchOrDefault(@Nullable final String building, @Nullable final Buildings def) {
        return EnumUtil.getEnumIgnoreCase(building, Buildings.class, def);
    }
}
