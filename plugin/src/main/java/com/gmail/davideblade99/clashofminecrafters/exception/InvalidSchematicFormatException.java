/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.exception;

import javax.annotation.Nullable;

/**
 * Exception indicating that the format of a schematic file is incorrect
 *
 * @since v3.1.2
 */
public final class InvalidSchematicFormatException extends Exception {
    public InvalidSchematicFormatException() { }

    public InvalidSchematicFormatException(@Nullable final String message) {
        super(message);
    }
}