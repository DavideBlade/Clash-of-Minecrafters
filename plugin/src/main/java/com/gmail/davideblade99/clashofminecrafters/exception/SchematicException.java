/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.exception;

import javax.annotation.Nullable;

/**
 * Exception indicating a generic error related to schematic operations
 *
 * @since 3.1.4
 */
public class SchematicException extends Exception {

    public SchematicException() { }

    public SchematicException(@Nullable final String message) {
        super(message);
    }
}