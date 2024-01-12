/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.building;

import com.gmail.davideblade99.clashofminecrafters.util.geometric.Area;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;

import javax.annotation.Nonnull;

/**
 * Represents a gold extractor
 *
 * @author DavideBlade
 * @since 3.2
 */
public final class GoldExtractor {

    private int level;
    private final Area buildingArea;

    /**
     * Initializes a new gold extractor, with the building delimited by the corners {@code corner1} and {@code corner2}
     *
     * @param level   Extractor level
     * @param corner1 Any corner of the building
     * @param corner2 Corner of building opposite to {@code corner1}
     *
     * @throws IllegalArgumentException If the level is less than or equal to 0
     */
    public GoldExtractor(final int level, @Nonnull final Vector corner1, @Nonnull final Vector corner2) {
        this(level, new Area(corner1, corner2));
    }

    /**
     * Initializes a new gold extractor, with the building delimited by the specified area
     *
     * @param level        Extractor level
     * @param buildingArea Area occupied by the building
     *
     * @throws IllegalArgumentException If the level is less than or equal to 0
     */
    public GoldExtractor(final int level, @Nonnull final Area buildingArea) {
        if (level <= 0)
            throw new IllegalArgumentException("Building level '" + level + "' is not positive");

        this.level = level;
        this.buildingArea = buildingArea;
    }

    /**
     * @return The level (> 0) of the building unlocked by the player
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the specified level for the gold extractor
     *
     * @param level New level to set, greater than or equal to 0
     *
     * @throws IllegalArgumentException If the level is less than or equal to 0
     */
    public void setLevel(final int level) {
        if (level <= 0)
            throw new IllegalArgumentException("Building level '" + level + "' is not positive");

        this.level = level;
    }

    /**
     * @return The area where the building is located
     */
    @Nonnull
    public Area getBuildingArea() {
        return buildingArea;
    }

    @Override
    public String toString() {
        return "GoldExtractor{" +
                "level=" + level +
                ", buildingArea=" + buildingArea +
                '}';
    }
}
