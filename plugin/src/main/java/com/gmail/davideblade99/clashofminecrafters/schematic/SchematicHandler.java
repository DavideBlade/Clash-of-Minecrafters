/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic;

import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Class responsible for the management of schematic
 *
 * @since v3.1.2
 */
//TODO: rimuovere dal config.yml la possibilità di scegliere se usare WorldEdit o il formato interno
//TODO: adesso WorldEdit è richiesto, non è più una softdepend (aggiornar eplugin.yml + metodo in CoM.class che controlla le dipendenze)
//TODO: aggiornare wiki con nuovo sistema
public final class SchematicHandler {

    private final File schematicFolder;

    public SchematicHandler(@Nonnull final Plugin plugin) {
        this.schematicFolder = new File(plugin.getDataFolder(), "Schematics");
    }

    public void paste(@Nonnull final Schematics schematic, @Nonnull final World world, @Nonnull final Vector origin) throws PastingException {
        final File schematicFile = getSchematicFile(schematic, ".schematic");
        if (!schematicFile.exists()) //TODO: gestire il caso null
            FileUtil.copyFile(schematic.getName() + ".schematic", schematicFile);

        //TODO: switch per vari plugin alternativi a WE
        new WESchematic(schematicFile).paste(world, origin);
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
    public File getSchematicFile(@Nullable final Schematics schematic, @Nullable final String schematicExtension) {
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