/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic.awe;

import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.schematic.SchematicPaster;
import org.bukkit.Location;
import org.primesoft.asyncworldedit.api.IAsyncWorldEdit;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class that deals with handling schematics via AsyncWorldEdit
 *
 * @since v3.1.3
 */
public final class AsyncWEPaster implements SchematicPaster {

    private final IAsyncWorldEdit awe;

    public AsyncWEPaster(@Nonnull final IAsyncWorldEdit awe) {
        this.awe = awe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paste(@Nonnull final File schematic, @Nonnull final Location location) throws PastingException, FileNotFoundException, InvalidSchematicFormatException, IOException {
        getSchematic(schematic).paste(location);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Schematic getSchematic(@Nonnull final File schematic) throws FileNotFoundException, InvalidSchematicFormatException, IOException {
        return new AsyncWESchematic(schematic, awe);
    }
}
