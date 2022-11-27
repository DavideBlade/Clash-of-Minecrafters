/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic.worldedit;

import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.geometric.Size3D;
import com.gmail.davideblade99.clashofminecrafters.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class representing the schematic of WorldEdit
 *
 * @since v3.1.3
 */
//TODO: https://gist.github.com/aikar/105311fd5d77e488bc6ec3e65872681f#file-worldedithelper-java-L82
public class WESchematic implements Schematic {

    protected final Clipboard clipboard; // Clipboard of the schematic
    protected final Size3D size;
    protected Location origin;

    /**
     * Creates a new instance of the class and loads the schematic
     *
     * @param schematicFile File containing the schematic
     *
     * @throws FileNotFoundException           If the schematic file does not exist
     * @throws InvalidSchematicFormatException If the file format is not recognized by WorldEdit
     * @throws IOException                     If WorldEdit throws an I/O exception
     */
    public WESchematic(@Nonnull final File schematicFile) throws FileNotFoundException, InvalidSchematicFormatException, IOException {
        this.clipboard = getClipboard(schematicFile);

        final BlockVector3 dimensions = clipboard.getDimensions();
        this.size = new Size3D(dimensions.getBlockX(), dimensions.getBlockY(), dimensions.getBlockZ());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final Size3D getSize() {
        return this.size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public final Location getOrigin() {
        return this.origin;
    }

    @Override
    public String toString() {
        return "Type: [WorldEdit], origin: [" + (origin == null ? "not set" : origin.toString()) + "], size: [" + size.toString() + "]";
    }

    /**
     * {@inheritDoc}
     */
    //TODO: prendere spunto dai builder di WE (EditSessionBuilder & PasteBuilder)
    //TODO: prendere spunto da Vector3 e Vector2 di WE
    //TODO: prendere spunto da BukkitAdapter di WE (per passare dalle mie implementazioni a quelle di WE o a quelle di Bukkit)
    @Override
    public void paste(@Nonnull final Location origin, @Nonnull final NullableCallback<PastingException> completionHandler) {
        this.origin = origin;

        final Vector adjustedOrigin = getPasteLocation(origin);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(origin.getWorld()))) {
            editSession.setReorderMode(EditSession.ReorderMode.MULTI_STAGE);
            final Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                    .to(BlockVector3.at(adjustedOrigin.getX(), adjustedOrigin.getY(), adjustedOrigin.getZ()))
                    .copyEntities(true)
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);

            completionHandler.call(null);
        } catch (final Exception e) {
            e.printStackTrace();

            completionHandler.call(new PastingException("Exception generated by WorldEdit"));
        }


        /*TODO
         * https://github.com/Boomclaw/MineResetLite/blob/master/src/main/java/com/koletar/jj/mineresetlite/commands/MineCommands.java#L112
         * https://github.com/EngineHub/WorldEdit/blob/master/worldedit-core/src/main/java/com/sk89q/worldedit/command/ClipboardCommands.java#L145
         */
    }

    /**
     * <p>
     * Returns the position where the schematic is to be pasted so that it faces north-east and upwards from the
     * origin. In other words, this method ensures that the schematic is always placed in the same direction. If
     * the {@code origin} were used directly to paste the schematic, it would be positioned from the origin, but in
     * an unpredictable direction that would depend only on the position the player had when he copied/saved the
     * schematic.
     * </p>
     * <p>
     * Example: when the schematic was copied/saved, the player was north-east of the structure (the construction
     * was therefore south-west of the player). If you paste in the origin without using this adjusted position,
     * the schematic will be pasted in the south-west direction, instead of always in the usual direction. This may
     * cause problems with overlapping islands.
     * </p>
     *
     * @param origin is the position where the schematic is to be pasted
     *
     * @return the new position where the schematic is to be pasted
     */
    @Nonnull
    protected final Vector getPasteLocation(@Nonnull final Location origin) {
        final Vector adjustedLocation = new Vector(origin);
        final Region region = clipboard.getRegion();
        final BlockVector3 minimumPoint = region.getMinimumPoint();

        // Offset indicates the position the player had when he copied the schematic
        final BlockVector3 offset = minimumPoint.subtract(clipboard.getOrigin());

        adjustedLocation.subtract(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ()); // North-west corner
        adjustedLocation.subtract(0, 0, region.getMaximumPoint().subtract(minimumPoint).getBlockZ()); // South-west corner

        return adjustedLocation;
    }

    /**
     * Load {@link Clipboard} from the schematic file
     *
     * @param schematicFile - is the file which contains the schematic
     *
     * @return The {@link Clipboard} obtained from specified file or {@code null} if the format of the schematic is
     * invalid or an error occurred when reading the file
     *
     * @throws FileNotFoundException           If the schematic file does not exist
     * @throws InvalidSchematicFormatException If the file format is not recognized by WorldEdit
     * @throws IOException                     If WorldEdit throws an I/O exception
     */
    @Nonnull
    private Clipboard getClipboard(@Nonnull final File schematicFile) throws FileNotFoundException, InvalidSchematicFormatException, IOException {
        if (!schematicFile.exists())
            throw new FileNotFoundException("Schematic file does not exist");

        final ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
        if (format == null) {
            throw new InvalidSchematicFormatException("Unrecognized format by WorldEdit");
            //TODO: Invio sicuramente un messaggio di errore in console ma poi? Anche all'utente? devo poi chiaramente anche annullar l'operazione
        }

        try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
            return reader.read();
        }
    }
}