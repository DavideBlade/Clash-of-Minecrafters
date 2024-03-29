/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.schematic.SchematicPaster;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematics;
import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class responsible for managing schematics through the use of external plugins (such as WorldEdit)
 *
 * @see SchematicPaster
 * @see Schematic
 * @see Schematics
 * @since 3.1.2
 */
public final class SchematicHandler {

    private final static String SCHEMATIC_EXTENSION = ".schematic";


    private final File schematicFolder;
    private SchematicPaster paster;

    /**
     * Create a new instance of this class
     *
     * @param plugin Plugin in whose {@link Plugin#getDataFolder()} look for the "Schematics" folder containing the
     *               schematics
     */
    public SchematicHandler(@Nonnull final Plugin plugin) {
        this.schematicFolder = new File(plugin.getDataFolder(), "Schematics");
    }

    /**
     * Set the plugin that handles the schematic
     *
     * @param paster Paster plugin
     */
    public void setSchematicPaster(@Nullable final SchematicPaster paster) {
        this.paster = paster;
    }

    /**
     * Load the specified schematic with one of the supported plugins
     *
     * @param schematic Name of schematic file to load
     *
     * @return A new {@link Schematic}
     *
     * @throws IllegalStateException           If the plugin to be used to paste schematics has not yet been
     *                                         defined with the {@link #setSchematicPaster(SchematicPaster)}
     *                                         method
     * @throws FileNotFoundException           If the schematic file does not exist and cannot be found in the .jar
     * @throws InvalidSchematicFormatException If the file format is not recognized by {@link #paster}
     * @throws IOException                     If WorldEdit throws an I/O exception
     */
    @Nonnull
    public Schematic getSchematic(@Nonnull final String schematic) throws FileNotFoundException, InvalidSchematicFormatException, IOException {
        if (paster == null)
            throw new IllegalStateException("Schematic paster not yet defined");

        final File schematicFile = getSchematicFile(schematic);
        if (!schematicFile.exists())
            FileUtil.copyFile(schematic + SCHEMATIC_EXTENSION, schematicFile);

        return paster.getSchematic(schematicFile);
    }

    /**
     * <p>Paste the schematic at the specified location.</p>
     * <p>The operation is delegated to the {@link #paster}: if it operates asynchronously (e.g., AsyncWorldEdit),
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
     *
     * @since 3.1.4
     */
    public void paste(@Nonnull final Schematic schematic, @Nonnull final Location location, @Nonnull final NullableCallback<PastingException> completionHandler) {
        schematic.paste(location, completionHandler);
    }

    /**
     * @return The list of file names ending with {@link #SCHEMATIC_EXTENSION} in the schematic folder. {@code
     * Null} if there are no files or the folder does not exist.
     */
    @Nullable
    private String[] getExistingSchematics() {
        final File[] listOfFiles = schematicFolder.listFiles((dir, name) -> name.endsWith(SCHEMATIC_EXTENSION));

        if (listOfFiles == null || listOfFiles.length == 0)
            return null;

        final String[] schematics = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++)
            schematics[i] = listOfFiles[i].getName();

        return schematics;
    }

    /**
     * Create a new {@link File} instance within the schematic folder and with the name related to the specified
     * schematic. The existence of the file will not be checked nor will it be created if it is absent.
     *
     * @param schematicName Schematic file name (without the extension!)
     *
     * @return a new {@link File} corresponding to the schematic
     */
    @Nonnull
    private File getSchematicFile(@Nonnull final String schematicName) {
        return new File(schematicFolder, schematicName + SCHEMATIC_EXTENSION);
    }

    /**
     * Search for a safe location in which to set the village spawn
     *
     * @param schematic Schematic within which to look for a safe position
     *
     * @return The first location found or {@code null} if none is available
     */
    @Nullable
    public static Location getSpawnLocation(@Nonnull final Schematic schematic) {
        final Point center = new Point(schematic.getOrigin().getBlockX() + schematic.getSize().getWidth() / 2, schematic.getOrigin().getBlockZ() - schematic.getSize().getLength() / 2);
        final Point position = new Point(center.x, center.y);

        boolean first = true;
        int counter = 0;
        int steps = 1;
        byte direction = 0;

        // Stop when outside
        while (position.x > schematic.getOrigin().getX() && position.x < schematic.getOrigin().getX() + schematic.getSize().getWidth() && position.y < schematic.getOrigin().getZ() && position.y > schematic.getOrigin().getY() - schematic.getSize().getLength()) {
            while (counter < steps) {
                // Move towards the line
                final Location location = checkSpawnLocation(schematic, position, direction, schematic.getOrigin().getWorld());

                if (location != null)
                    return location;

                counter++;
            }

            // Reset the counter and change direction
            counter = 0;
            direction = (byte) ((direction + 1) % 4);
            if (steps != 1 || direction == 1 || direction == 3)
                first = !first;

            // If finished, increment the line length
            if (first)
                steps++;
        }

        // Location not found
        return null;
    }

    @Nullable
    private static Location checkSpawnLocation(@Nonnull final Schematic schematic, @Nonnull final Point position, final byte direction, @Nonnull final World world) {
        final Integer spawnLocation = isYValid(schematic, new Location(world, position.x, schematic.getOrigin().getY(), position.y, -180, 0));

        if (spawnLocation == null) {
            switch (direction) {
                case 0:
                    position.x++;
                    break;
                case 1:
                    position.y--;
                    break;
                case 2:
                    position.x--;
                    break;
                case 3:
                    position.y++;
                    break;

                default:
                    throw new IllegalStateException("Unexpected direction: " + direction);
            }
        } else
            return new Location(world, position.x, spawnLocation, position.y);

        return null;
    }

    @Nullable
    private static Integer isYValid(@Nonnull final Schematic schematic, @Nonnull final Location loc) {
        for (int y = schematic.getOrigin().getBlockY(); y <= schematic.getOrigin().getY() + schematic.getSize().getHeight(); y++) {
            loc.setY(y);
            if (BukkitLocationUtil.isSafeLocation(loc))
                return y;
        }

        return null;
    }
}