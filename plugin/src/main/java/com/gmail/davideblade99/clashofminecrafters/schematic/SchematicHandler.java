/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic;

import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
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
 * Class responsible for the management of schematic
 *
 * @since v3.1.2
 */
//TODO: rimuovere dal config.yml la possibilità di scegliere se usare WorldEdit o il formato interno <- Aggiornare wiki
//TODO: adesso WorldEdit è richiesto, non è più una softdepend (aggiornar eplugin.yml + metodo in CoM.class che controlla le dipendenze)
//TODO: aggiornare wiki con nuovo sistema
//TODO: rimosso comando /schem e relativo permesso (scriverlo nelle note dell'aggiornamento e aggiornare wiki)
//TODO: rimossi tutti i messaggi relativi alle schematic interne <- scriverlo nelle note dell'aggiornamento
//TODO: rimossa dal config.yml la parte che permetteva di scegliere tra WE e le schematic interne <- scriverlo nelle note dell'aggiornamento e aggiornare la wiki
//TODO: ottimizzato la creazione dei villaggi (incollamento delle schematic) <- scriverlo nelle note dell'aggiornamento
//TODO: adesso la schematic dei villaggi si chiama "Village.schematic" invece di "Island.schematic" <- scriverlo nelle note di aggiornamento (e aggiornare wiki?)

//TODO: questo handler dovrebbe andare insieme agli altri nell'altro package (questa nuova struttura dei pacchetti si ritiene sempre la scelta giusta?)
public final class SchematicHandler {

    private final static String SCHEMATIC_EXTENSION = ".schematic";


    private final File schematicFolder;
    private SchematicPaster paster;

    public SchematicHandler(@Nonnull final Plugin plugin) {
        this.schematicFolder = new File(plugin.getDataFolder(), "Schematics");
    }

    //TODO: invocarlo
    public void setSchematicPaster(@Nullable final SchematicPaster paster) {
        this.paster = paster;
    }

    /**
     * Load the specified schematic with one of the supported plugins
     *
     * @param schematic Schematic to load
     *
     * @return A new {@link Schematic}
     *
     * @throws IllegalStateException           If the plugin to be used to paste schematics has not yet been
     *                                         defined with the {@link #setSchematicPaster(SchematicPaster)}
     *                                         method
     * @throws IllegalArgumentException        If the {@link #paster}, set with {@link #setSchematicPaster(SchematicPaster)},
     *                                         is not recognized
     * @throws FileNotFoundException           If the schematic file cannot be found in the .jar
     * @throws InvalidSchematicFormatException If the file format is not recognized by {@code paster}
     * @throws IOException                     If WorldEdit throws an I/O exception
     */
    @Nonnull
    public Schematic getSchematic(@Nonnull final Schematics schematic) throws FileNotFoundException, InvalidSchematicFormatException, IOException {
        if (paster == null)
            throw new IllegalStateException("Schematic paster not yet defined");

        final File schematicFile = getSchematicFile(schematic);
        if (!schematicFile.exists())
            FileUtil.copyFile(schematic.getName(), schematicFile);

        switch (paster) {
            case WORLEDIT:
                return new WESchematic(schematicFile);

            default:
                throw new IllegalArgumentException("Unexpected paster: " + paster);
        }
    }

    /**
     * Paste the schematic at the specified location
     *
     * @param schematic Schematic to be pasted
     * @param location  Origin point where the schematic should be pasted
     *
     * @throws PastingException                In case of error during schematic pasting
     * @throws FileNotFoundException           If the schematic file cannot be found in the .jar
     * @throws InvalidSchematicFormatException If the file format is not recognized by {@code paster}
     * @throws IOException                     If WorldEdit throws an I/O exception
     */
    public void paste(@Nonnull final Schematics schematic, @Nonnull final Location location) throws PastingException, FileNotFoundException, InvalidSchematicFormatException, IOException {
        getSchematic(schematic).paste(location);
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
     * Create a new {@link File} within the schematic folder and with the name related to the specified schematic.
     * The existence of the file will not be checked nor will it be created if it is absent.
     *
     * @param schematic Schematic to get the file of
     *
     * @return a new {@link File} corresponding to the schematic
     */
    @Nonnull
    private File getSchematicFile(@Nonnull final Schematics schematic) {
        return new File(schematicFolder, schematic.getName() + SCHEMATIC_EXTENSION);
    }

    /**
     * TODO
     * @param schematic
     * @return
     *
     * @since v3.1.2
     */
    //TODO: riguardare il metodo
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

    /**
     * TODO
     * @param schematic
     * @param loc
     * @return
     *
     * @since v3.1.2
     */
    //TODO: riguardare il metodo
    private static Integer isYValid(final Schematic schematic, final Location loc) {
        for (int y = schematic.getOrigin().getBlockY(); y <= schematic.getOrigin().getY() + schematic.getSize().getHeight(); y++) {
            loc.setY(y);
            if (BukkitLocationUtil.isSafeLocation(loc))
                return y;
        }

        return null;
    }

    /**
     * TODO
     *
     * @param schematic
     * @param position
     * @param direction
     * @param world
     * @return
     *
     * @since v3.1.2
     */
    //TODO: riguardare il metodo
    private static Location checkSpawnLocation(final Schematic schematic, final Point position, final byte direction,
                                               final World world) {
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
                    throw new IllegalStateException("Unexpected value: " + direction);
            }
        } else
            return new Location(world, position.x, spawnLocation, position.y);

        return null;
    }
}