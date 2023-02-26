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
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.primesoft.asyncworldedit.api.IAsyncWorldEdit;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class that deals with handling schematics via AsyncWorldEdit
 *
 * @since v3.1.4
 */
public final class AsyncWEPaster implements SchematicPaster {

    private final Plugin plugin;
    private final IAsyncWorldEdit awe;

    public AsyncWEPaster(@Nonnull final Plugin plugin, @Nonnull final IAsyncWorldEdit awe) {
        this.plugin = plugin;
        this.awe = awe;
    }

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
        return new AsyncWESchematic(schematic, awe, plugin);
    }
}
