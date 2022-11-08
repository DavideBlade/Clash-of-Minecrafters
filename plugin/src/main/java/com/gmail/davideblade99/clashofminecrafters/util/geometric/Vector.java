/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.geometric;

import org.bukkit.Location;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

public final class Vector implements Serializable {

    private static final long serialVersionUID = -8473361306775261624L;

    private int x;
    private int y;
    private int z;

    public Vector(@Nonnull final Location loc) {
        this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public Vector(@Nonnull final Vector v) {
        this(v.x, v.y, v.z);
    }

    public Vector(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(final int z) {
        this.z = z;
    }

    @Nonnull
    public Vector subtract(final int x, final int y, final int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    @Nonnull
    public org.bukkit.util.Vector toBukkitVector() {
        return new org.bukkit.util.Vector(x, y, z);
    }

    /**
     * Formats a string containing data of {@code this} vector
     *
     * @return a string containing data of the vector
     */
    @Nonnull
    @Override
    public String toString() {
        return x + ", " + y + ", " + z;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof Vector))
            return false;
        else {
            final Vector vector = (Vector) obj;
            return x == vector.x && y == vector.y && z == vector.z;
        }
    }

    @Contract("null -> null")
    @Nullable
    public static Vector fromString(@Nullable final String str) {
        if (str == null)
            return null;

        final String[] split = str.split(",");
        if (split.length != 3)
            return null;

        try {
            return new Vector(Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim()), Integer.parseInt(split[2].trim()));
        } catch (final Exception ignored) {
            return null;
        }
    }
}