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
 * Represents an archers' tower
 *
 * @author DavideBlade
 * @since 3.2
 */
public final class ArcherTower {

    private int level;
    private Vector archerPos;
    private final Area buildingArea;

    /**
     * Initializes a new archer's tower, with the archer placed in the center and immediately above (1 block above) the
     * building delimited by the corners {@code corner1} and {@code corner2}
     *
     * @param level   Archer's tower level
     * @param corner1 Any corner of the building
     * @param corner2 Corner of building opposite to {@code corner1}
     *
     * @throws IllegalArgumentException If the level is less than or equal to 0
     */
    public ArcherTower(final int level, @Nonnull final Vector corner1, @Nonnull final Vector corner2) {
        this(level, new Area(corner1, corner2));
    }

    /**
     * Initializes a new archer's tower, with the archer placed in the center and immediately above (1 block above) the
     * building delimited by the specified area
     *
     * @param level        Archer's tower level
     * @param buildingArea Area occupied by the building
     *
     * @throws IllegalArgumentException If the level is less than or equal to 0
     */
    public ArcherTower(final int level, @Nonnull final Area buildingArea) {
        this(level, buildingArea.getHighestCenter(), buildingArea);
    }

    /**
     * Initializes a new archer's tower, with the archer at the specified position and the building delimited by the corners
     * {@code corner1} and {@code corner2}
     *
     * @param level          Archer's tower level
     * @param archerPosition Position of the tower archer, within or 1 block above the building ({@code buildingArea})
     * @param corner1        Any corner of the building
     * @param corner2        Corner of building opposite to {@code corner1}
     *
     * @throws IllegalArgumentException If the level is less than or equal to 0 or if the archer's position is outside the
     *                                  building
     */
    public ArcherTower(final int level, @Nonnull final Vector archerPosition, @Nonnull final Vector corner1, @Nonnull final Vector corner2) {
        this(level, archerPosition, new Area(corner1, corner2));
    }

    /**
     * Initializes a new archer's tower, with the archer at the specified position and the building delimited by the
     * specified area
     *
     * @param level          Archer's tower level
     * @param archerPosition Position of the tower archer, within or 1 block above the building ({@code buildingArea})
     * @param buildingArea   Area occupied by the building
     *
     * @throws IllegalArgumentException If the level is less than or equal to 0 or if the archer's position is outside the
     *                                  building
     */
    public ArcherTower(final int level, @Nonnull final Vector archerPosition, @Nonnull final Area buildingArea) {
        if (level <= 0)
            throw new IllegalArgumentException("Building level '" + level + "' is not positive");

        // The archer can also be placed above the building: +1 block allowed along the Y
        if (!buildingArea.contains(archerPosition) && !buildingArea.isRightBelow(archerPosition))
            throw new IllegalArgumentException("The position of the archer (" + archerPosition + ") must be inside the building (" + buildingArea.getMinCorner() + " - " + buildingArea.getMaxCorner() + ")");

        this.level = level;
        this.archerPos = archerPosition;
        this.buildingArea = buildingArea;
    }

    /**
     * @return The level (> 0) of the building unlocked by the player
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the specified level for the archer's tower
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
     * @return The archer's position
     */
    @Nonnull
    public Vector getArcherPos() {
        return archerPos;
    }

    /**
     * Sets a new position for the archer
     *
     * @param newPos New archer's position, within or 1 block above the building ({@code buildingArea})
     *
     * @throws IllegalArgumentException If the specified position is outside the building
     */
    public void setArcherPos(@Nonnull final Vector newPos) {
        // The archer can also be placed above the building: +1 block allowed along the Y
        if (!buildingArea.contains(newPos) && !buildingArea.isRightBelow(newPos))
            throw new IllegalArgumentException("The position of the archer (" + newPos + ") must be inside the building (" + buildingArea.getMinCorner() + " - " + buildingArea.getMaxCorner() + ")");

        this.archerPos = newPos;
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
        return "ArcherTower{" +
                "level=" + level +
                ", archerPos=" + archerPos +
                ", buildingArea=" + buildingArea +
                '}';
    }
}