/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic.worldedit;

import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.schematic.SchematicPaster;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class that deals with handling schematics via WorldEdit
 *
 * @since 3.1.4
 */
public final class WEPaster implements SchematicPaster {

    /**
     * {@inheritDoc}
     */
    @Override
    public void paste(@Nonnull final Schematic schematic, @Nonnull final Location location, @Nonnull final NullableCallback<PastingException> completionHandler) {
        schematic.paste(location, completionHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Schematic getSchematic(@Nonnull final File schematic) throws FileNotFoundException, InvalidSchematicFormatException, IOException {
        return new WESchematic(schematic);
    }
}
