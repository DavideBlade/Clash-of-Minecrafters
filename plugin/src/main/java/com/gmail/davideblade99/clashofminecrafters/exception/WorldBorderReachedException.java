/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.exception;

import javax.annotation.Nullable;

/**
 * Exception indicating the positioning of an island that is not entirely within the borders of the island world.
 * In other words, there is at least one block of the island that "goes outside" the boundaries.
 */
public final class WorldBorderReachedException extends Exception {

    public WorldBorderReachedException() {}

    public WorldBorderReachedException(@Nullable final String message) {
        super(message);
    }
}
