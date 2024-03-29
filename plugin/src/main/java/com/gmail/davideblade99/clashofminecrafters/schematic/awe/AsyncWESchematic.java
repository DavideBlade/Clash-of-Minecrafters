/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic.awe;

import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.schematic.worldedit.WESchematic;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.primesoft.asyncworldedit.api.IAsyncWorldEdit;
import org.primesoft.asyncworldedit.api.utils.IFuncParamEx;
import org.primesoft.asyncworldedit.api.worldedit.IAsyncEditSessionFactory;
import org.primesoft.asyncworldedit.api.worldedit.ICancelabeEditSession;
import org.primesoft.asyncworldedit.api.worldedit.IThreadSafeEditSession;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class representing the schematic of AsyncWorldEdit
 *
 * @since 3.1.4
 */
public final class AsyncWESchematic extends WESchematic {

    private final Plugin plugin;
    private final IAsyncWorldEdit awe;

    /**
     * Creates a new instance of the class and loads the schematic
     *
     * @param schematicFile File containing the schematic
     * @param plugin        Clash of Minecrafters instance
     * @param awe           AsyncWorldEdit instance
     *
     * @throws FileNotFoundException           If the schematic file does not exist
     * @throws InvalidSchematicFormatException If the file format is not recognized by WorldEdit
     * @throws IOException                     If WorldEdit throws an I/O exception
     */
    public AsyncWESchematic(@Nonnull final File schematicFile, @Nonnull final IAsyncWorldEdit awe, @Nonnull final Plugin plugin) throws FileNotFoundException, InvalidSchematicFormatException, IOException {
        super(schematicFile);

        this.plugin = plugin;
        this.awe = awe;
    }

    @Override
    public String toString() {
        return "Type: [AsyncWorldEdit], origin: [" + (origin == null ? "not set" : origin.toString()) + "], size: [" + size + "]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paste(@Nonnull final Location origin, @Nonnull final NullableCallback<PastingException> completionHandler) {
        super.origin = origin;

        final Vector adjustedOrigin = super.getPasteLocation(origin);

        /*
         * Unfortunately, the API provided by AsyncWorldEdit with Maven is out of date,
         * so you need to use this method. If they are ever updated, you can use
         * WorldEdit.getInstance().newEditSessionBuilder() and cast to IAsyncEditSessionBuilder.
         */
        final IAsyncEditSessionFactory aweEditSessionFactory = (IAsyncEditSessionFactory) WorldEdit.getInstance().getEditSessionFactory();
        final IThreadSafeEditSession editSession = aweEditSessionFactory.getThreadSafeEditSession(new BukkitWorld(origin.getWorld()), -1);
        final IFuncParamEx<Integer, ICancelabeEditSession, MaxChangedBlocksException> action = new PasteAction(BlockVector3.at(adjustedOrigin.getX(), adjustedOrigin.getY(), adjustedOrigin.getZ()), completionHandler);
        awe.getBlockPlacer().performAsAsyncJob(editSession, awe.getPlayerManager().getUnknownPlayer(), "CoM pasting schematic", action);
    }

    private final class PasteAction implements IFuncParamEx<Integer, ICancelabeEditSession, MaxChangedBlocksException> {

        private final BlockVector3 pasteLoc;
        private final NullableCallback<PastingException> completionHandler;

        public PasteAction(@Nonnull final BlockVector3 pasteLoc, @Nonnull final NullableCallback<PastingException> completionHandler) {
            this.pasteLoc = pasteLoc;
            this.completionHandler = completionHandler;
        }

        @Override
        public Integer execute(@Nonnull final ICancelabeEditSession editSession) throws MaxChangedBlocksException {
            editSession.enableQueue();
            final ClipboardHolder holder = new ClipboardHolder(clipboard);

            try {
                final Operation operation = holder.createPaste(editSession)
                        .to(pasteLoc)
                        .copyEntities(true)
                        .ignoreAirBlocks(false)
                        .build();

                Operations.complete(operation);

                // Go back on main thread
                Bukkit.getScheduler().runTask(plugin, () -> completionHandler.call(null));
            } catch (final Exception e) {
                e.printStackTrace();

                // Go back on main thread
                Bukkit.getScheduler().runTask(plugin, () -> completionHandler.call(new PastingException("Exception generated by AsyncWorldEdit")));
            } finally {
                editSession.flushSession();
            }
            return editSession.size();
        }
    }
}
