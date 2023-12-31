/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.geometric;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * A simple class for representing the dimensions of an object in 3D.
 *
 * @since v3.1.8
 */
public final class Size3D implements Serializable {

    private static final long serialVersionUID = -6546543157744332590L;

    private final int width; // x
    private final int height; // y
    private final int length; // z

    public Size3D(final int width, final int height, final int length) {
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "Width: " + width + ", height: " + height + ", length: " + length;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (!(obj instanceof Size3D))
            return false;
        else {
            final Size3D size = (Size3D) obj;
            return this.width == size.width && this.height == size.height && this.length == size.length;
        }
    }
}
