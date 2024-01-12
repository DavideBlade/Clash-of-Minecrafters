/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.geometric;

import javax.annotation.Nonnull;

/**
 * Class representing an area (1D, 2D or 3D) enclosed between {@link #minCorner} and {@link #maxCorner}, the opposite corners
 * with, respectively, smaller and larger coordinates.
 *
 * @author DavideBlade
 * @since 3.2
 */
public class Area {

    private final Vector minCorner;
    private final Vector maxCorner;

    /**
     * Initialize a new area (segment) enclosed between the two coordinates x
     *
     * @param x1 Initial x coordinate
     * @param x2 Final x coordinate
     */
    public Area(final int x1, final int x2) {
        this(new Vector(x1, 0, 0), new Vector(x2, 0, 0));
    }

    /**
     * Initialize a new area (rectangle) enclosed between the points (x1, y1) and (x2, y2)
     *
     * @param x1 x-coordinate of the point (x1, y1)
     * @param y1 y-coordinate of the point (x1, y1)
     * @param x2 x-coordinate of the point (x2, y2), opposite to the point (x1, y1)
     * @param y2 y-coordinate of the point (x2, y2), opposite to the point (x1, y1)
     */
    public Area(final int x1, final int y1, final int x2, final int y2) {
        this(new Vector(x1, y1, 0), new Vector(x2, y2, 0));
    }

    /**
     * Initialize a new area (cube) enclosed between the points (x1, y1, z1) and (x2, y2, z2)
     *
     * @param x1 x-coordinate of the point (x1, y1, z1)
     * @param y1 y-coordinate of the point (x1, y1, z1)
     * @param z1 z-coordinate of the point (x1, y1, z1)
     * @param x2 x-coordinate of the point (x2, y2, z2), opposite to the point (x1, y1, z1)
     * @param y2 y-coordinate of the point (x2, y2, z2), opposite to the point (x1, y1, z1)
     * @param z2 z-coordinate of the point (x2, y2, z2), opposite to the point (x1, y1, z1)
     */
    public Area(final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        this(new Vector(x1, y1, z1), new Vector(x2, y2, z2));
    }

    /**
     * Initialize a new area enclosed between any two points in the space
     *
     * @param point1 Initial point
     * @param point2 Opposite point to {@code point1}}
     *
     * @see #getMinAreaCorner(Vector, Vector)
     * @see #getMaxAreaCorner(Vector, Vector)
     */
    public Area(@Nonnull final Vector point1, @Nonnull final Vector point2) {
        this.minCorner = getMinAreaCorner(point1, point2);
        this.maxCorner = getMaxAreaCorner(point1, point2);
    }

    /**
     * @return The corner of the area with smaller coordinates
     */
    @Nonnull
    public Vector getMinCorner() {
        return minCorner;
    }

    /**
     * @return The corner of the area with larger coordinates
     */
    @Nonnull
    public Vector getMaxCorner() {
        return maxCorner;
    }

    /**
     * @return A new {@link Vector} placed 1 block above and in the center of the area
     */
    @Nonnull
    public Vector getHighestCenter() {
        final int xSize = maxCorner.getX() - minCorner.getX();
        final int zSize = maxCorner.getZ() - minCorner.getZ();

        return new Vector(minCorner.getX() + xSize / 2, maxCorner.getY() + 1, maxCorner.getZ() + zSize / 2);
    }

    /**
     * Checks whether the point defined by the x,y and z coordinates is within {@code this} area
     *
     * @param x x-coordinate of the point (x, y, z)
     * @param y y-coordinate of the point (x, y, z)
     * @param z z-coordinate of the point (x, y, z)
     *
     * @return True if the point (x, y, z) is within {@code this} area
     */
    public boolean contains(final int x, final int y, final int z) {
        return contains(new Vector(x, y, z));
    }

    /**
     * Checks whether the specified vector is within {@code this} area
     *
     * @param vector Position to check
     *
     * @return True if the specified vector is within {@code this} area, otherwise false
     */
    public boolean contains(@Nonnull final Vector vector) {
        final int x = vector.getX(), y = vector.getY(), z = vector.getZ();
        final int minX = minCorner.getX(), minY = minCorner.getY(), minZ = minCorner.getZ();
        final int maxX = maxCorner.getX(), maxY = maxCorner.getY(), maxZ = maxCorner.getZ();

        return (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ);
    }

    /**
     * Checks whether the specified vector is immediately above (1 block above) {@code this} area
     *
     * @param vector Position to check
     *
     * @return True if the specified vector is 1 block above {@code this} area, otherwise false
     */
    public boolean isRightBelow(@Nonnull final Vector vector) {
        final int x = vector.getX(), y = vector.getY(), z = vector.getZ();
        final int minX = minCorner.getX(), minZ = minCorner.getZ();
        final int maxX = maxCorner.getX(), maxZ = maxCorner.getZ();

        return (x >= minX && x <= maxX && y == (maxCorner.getY() + 1) && z >= minZ && z <= maxZ);
    }

    @Override
    public String toString() {
        return "Area{" +
                "minCorner=" + minCorner +
                ", maxCorner=" + maxCorner +
                '}';
    }

    /**
     * Gets the corner of the area defined by the two corners {@code corner1} and {@code corner2} with the smallest
     * coordinates
     *
     * @param corner1 Any corner of the area
     * @param corner2 Opposite corner to {@code corner1}
     *
     * @return A new {@link Vector} with the smallest coordinates of the area enclosed between {@code corner1} and
     * {@code corner2}
     */
    @Nonnull
    public static Vector getMinAreaCorner(@Nonnull final Vector corner1, @Nonnull final Vector corner2) {
        final int minX = Math.min(corner1.getX(), corner2.getX());
        final int minY = Math.min(corner1.getY(), corner2.getY());
        final int minZ = Math.min(corner1.getZ(), corner2.getZ());

        return new Vector(minX, minY, minZ);
    }

    /**
     * Gets the corner of the area defined by the two corners {@code corner1} and {@code corner2} with the largest
     * coordinates
     *
     * @param corner1 Any corner of the area
     * @param corner2 Opposite corner to {@code corner1}
     *
     * @return A new {@link Vector} with the largest coordinates of the area enclosed between {@code corner1} and
     * {@code corner2}
     */
    @Nonnull
    public static Vector getMaxAreaCorner(@Nonnull final Vector corner1, @Nonnull final Vector corner2) {
        final int maxX = Math.max(corner1.getX(), corner2.getX());
        final int maxY = Math.max(corner1.getY(), corner2.getY());
        final int maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return new Vector(maxX, maxY, maxZ);
    }
}
