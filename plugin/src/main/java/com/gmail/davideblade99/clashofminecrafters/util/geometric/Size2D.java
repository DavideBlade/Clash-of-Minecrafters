/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.geometric;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A simple class for representing the dimensions of an object in 2D.
 *
 * @since v3.1.8
 */
public final class Size2D {

    private final int width; // x
    private final int length; // z

    public Size2D(final int width, final int length) {
        this.width = width;
        this.length = length;
    }

    public Size2D(final int lengthAndWidth) {
        this(lengthAndWidth, lengthAndWidth);
    }

    public Size2D(@Nonnull final Size3D size) {
        this(size.getWidth(), size.getLength());
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return width + ", " + length;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof Size2D))
            return false;
        else {
            final Size2D size = (Size2D) obj;
            return this.width == size.width && this.length == size.length;
        }
    }

    @Nullable
    public static Size2D fromString(@Nullable final String str) {
        if (str == null)
            return null;

        final String[] split = str.split(",");
        if (split.length != 2)
            return null;

        try {
            return new Size2D(Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim()));
        } catch (final Exception ignored) {
            return null;
        }
    }
}