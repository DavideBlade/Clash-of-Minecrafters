package com.gmail.davideblade99.clashofminecrafters.exception;

import javax.annotation.Nullable;

/**
 * Exception indicating a problem while loading a schematic
 *
 * @since v3.1.4
 */
public final class SchematicLoadException extends SchematicException {

    public SchematicLoadException() {}

    public SchematicLoadException(@Nullable final String message) {
        super(message);
    }
}