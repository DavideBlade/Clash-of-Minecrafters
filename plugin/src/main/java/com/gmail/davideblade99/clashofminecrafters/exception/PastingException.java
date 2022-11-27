/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.exception;

import javax.annotation.Nullable;

/**
 * Exception that indicates a problem when pasting a schematic
 */
public final class PastingException extends SchematicException {

    public PastingException() {}

    public PastingException(@Nullable final String message) {
        super(message);
    }
}