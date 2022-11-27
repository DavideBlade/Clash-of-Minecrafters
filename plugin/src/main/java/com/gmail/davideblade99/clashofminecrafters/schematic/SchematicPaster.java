/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic;

import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A class that implements this interface represents a plugin (e.g., WorldEdit) that is responsible for pasting a
 * schematic
 *
 * @see Schematic
 * @since v3.1.3
 */
public interface SchematicPaster {

    /**
     * Paste the schematic from the specified position, facing north-east
     *
     * @param schematic Schematic to be pasted
     * @param location  Origin point where the schematic should be pasted
     *
     * @throws PastingException                In case of error during schematic pasting
     * @throws FileNotFoundException           If the schematic file does not exist
     * @throws InvalidSchematicFormatException If the file format is not recognized
     * @throws IOException                     If an I/O exception has occurred
     */
    void paste(@Nonnull final File schematic, @Nonnull final Location location) throws PastingException, FileNotFoundException, InvalidSchematicFormatException, IOException;

    /**
     * Load the specified schematic
     *
     * @param schematic Schematic to load
     *
     * @return A new {@link Schematic}
     *
     * @throws FileNotFoundException           If the schematic file does not exist
     * @throws InvalidSchematicFormatException If the file format is not recognized
     * @throws IOException                     If an I/O exception has occurred
     */
    @Nonnull
    Schematic getSchematic(@Nonnull final File schematic) throws FileNotFoundException, InvalidSchematicFormatException, IOException;
}
