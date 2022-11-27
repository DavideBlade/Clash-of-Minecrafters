/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic;

import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
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
     * <p>Paste the schematic from the specified position, facing north-east.</p>
     * <p>The operation is delegated to the plugin paster: if it operates asynchronously (e.g., AsyncWorldEdit),
     * the schematic will be pasted asynchronously, otherwise the main thread will be used. In any case, the
     * completion of the operation will correspond to the callback invocation.</p>
     * <p>If exceptions are thrown, they are passed to the callback received as parameter.</p>
     *
     * @param schematic         Schematic to be pasted
     * @param location          Origin point where the schematic should be pasted
     * @param completionHandler Callback that will be invoked when the operation is completed. It will have {@code
     *                          null} as parameter if the operation was completed successfully, otherwise it will
     *                          receive the thrown exception. {@link PastingException} is thrown in case of error
     *                          during schematic pasting.
     */
    void paste(@Nonnull final Schematic schematic, @Nonnull final Location location, @Nonnull final NullableCallback<PastingException> completionHandler);

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
