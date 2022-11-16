/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FilenameFilter;

public final class SchematicHandler {

    private final File schematicFolder;

    public SchematicHandler(@Nonnull final CoM plugin) {
        this.schematicFolder = new File(plugin.getDataFolder(), "Schematics");
    }

    @Nullable
    public String[] getSchematicList() {
        //TODO: doppio loop: 1 con listFiles e il 2° con il for(...) mio

        final File[] listOfFiles = schematicFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".schem") || name.endsWith(".schematic");
            }
        });

        if (listOfFiles == null || listOfFiles.length == 0)
            return null;

        final String[] schematics = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++)
            schematics[i] = listOfFiles[i].getName();

        return schematics;
    }

    @Nullable
    public File getSchematicFile(@Nullable final Schematic.Schematics schematic, @Nullable final String schematicExtension) {
        //TODO: restituire direttamente new File(...)

        if (schematic == null || schematicExtension == null)
            return null;
        if (!schematicExtension.equals(".schem") && !schematicExtension.equals(".schematic"))
            return null;

        final File[] listOfFiles = schematicFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.equals(schematic.getName() + schematicExtension);
            }
        });

        return listOfFiles != null && listOfFiles.length != 0 ? listOfFiles[0] : new File(schematicFolder, schematic.getName() + schematicExtension);
    }
}
