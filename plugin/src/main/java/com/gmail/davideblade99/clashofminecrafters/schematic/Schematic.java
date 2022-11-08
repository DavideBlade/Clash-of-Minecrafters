/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Size3D;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import com.sk89q.worldedit.EditSession;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

//TODO: testare
public final class Schematic {

    private final Object schematic;
    private final Size3D size;
    private Vector origin;

    public Schematic(@Nonnull final Clipboard clipboard) {
        this.schematic = clipboard;

        final BlockVector3 dimensions = clipboard.getDimensions();
        this.size = new Size3D(dimensions.getBlockX(), dimensions.getBlockY(), dimensions.getBlockZ());
    }

    public Schematic(@Nonnull final SchemStructure structure) {
        this.schematic = structure;
        this.size = structure.getSize();
    }

    public void paste(@Nonnull final World world, @Nonnull final Vector origin) throws PastingException {
        this.origin = origin;

        if (schematic instanceof SchemStructure)
            paste((SchemStructure) schematic, world, origin);
        else
            WorldEdit.paste((Clipboard) schematic, world, origin);
    }

    @Nonnull
    public Size3D getSize() {
        return this.size;
    }

    @Nonnull
    public Vector getOrigin() {
        return this.origin;
    }

    @Override
    public String toString() {
        return "Type: [" + (schematic instanceof SchemStructure ? "Internal" : "WorldEdit") + "], origin: [" + origin.toString() + "], size: [" + size.toString() + "]";
    }

    /**
     * Paste specified block(s) starting from specified location
     *
     * @param structure - is the structure of block(s) that must be pasted
     * @param world     - is the world in which to paste the schematic
     * @param loc       - is the location where specified block(s) will start to be pasted
     * @since v2.0
     */
    private void paste(@Nonnull final SchemStructure structure, @Nonnull final World world, @Nonnull final Vector loc) {
        //TODO: ho ridotto le dimensioni dei file .schem? Se sì, inserire nelle note dell'update
        for (Map.Entry<Vector, Block> entry : structure.getBlocks().entrySet()) {
            /*
             * Add to block position (into the cuboid of schematic)
             * the location where it will be pasted.
             * From Z remove the length of schematic
             * to put selected block in front of the origin instead of behind it.
             */
            final Vector position = entry.getKey();
            final int x = position.getX() + loc.getX();
            final int y = position.getY() + loc.getY();
            final int z = position.getZ() + loc.getZ() - (this.size.getLength() - 1);


            final Block block = entry.getValue();
            final BlockData blockData = Bukkit.createBlockData(block.getData());
            if (blockData.getMaterial() == Material.AIR) // Skip air block positioning
                continue;

            final org.bukkit.block.Block oldBlock = world.getBlockAt(x, y, z);

            oldBlock.setBlockData(blockData, true);
        }
    }

    /**
     * Get a {@code SchemStructure} of blocks between the specified points
     *
     * @param start - is the first point from which it will begin to take the blocks
     * @param end   - is the end point up to which the blocks will be taken
     * @return {@code SchemStructure} of blocks between the specified points
     * {@code Null} if given start or given end are {@code null} or if world of given blocks isn't the same
     * @since v2.0
     */
    //TODO: non ha senso che i parametri siano Nullable
    @Nullable
    public static SchemStructure getBlocks(@Nullable final Location start, @Nullable final Location end) {
        if (start == null || end == null)
            return null;
        if (!start.getWorld().equals(end.getWorld()))
            return null;


        final int minX = Math.min(start.getBlockX(), end.getBlockX());
        final int minY = Math.min(start.getBlockY(), end.getBlockY());
        final int minZ = Math.min(start.getBlockZ(), end.getBlockZ());

        final int maxX = Math.max(start.getBlockX(), end.getBlockX());
        final int maxY = Math.max(start.getBlockY(), end.getBlockY());
        final int maxZ = Math.max(start.getBlockZ(), end.getBlockZ());


        final LinkedHashMap<Vector, Block> blocks = new LinkedHashMap<>();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.put(new Vector(x - minX, y - minY, z - minZ), new Block(start.getWorld().getBlockAt(x, y, z)));
                }
            }
        }

        final int sizeX = maxX - minX + 1;
        final int sizeY = maxY - minY + 1;
        final int sizeZ = maxZ - minZ + 1;
        return new SchemStructure(new Size3D(sizeX, sizeY, sizeZ), blocks);
    }

    /**
     * Load {@code SchemStructure} of block(s) from specified file
     *
     * @param schematic - is the schem to be loaded
     * @return {@code SchemStructure} of block(s) loaded. {@code Null} if an exception has occurred
     * @since v2.0
     */
    @Nullable
    public static SchemStructure load(@Nonnull final Schematics schematic) {
        final File schematicFile =  CoM.getInstance().getSchematicHandler().getSchematicFile(schematic, ".schem");
        if (!schematicFile.exists())
            FileUtil.copyFile(schematic.getName() + ".schem", schematicFile);

        try (final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(schematicFile))) {
            return (SchemStructure) ois.readObject();
        } catch (final Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * Save specified {@code SchemStructure} of block(s) into schem file with a specified name
     *
     * @param name      - is the schem name
     * @param structure - is the {@code SchemStructure} of block(s) that must be saved
     * @return true if the save has been successfully done, otherwise false
     * @since v2.0
     */
    //TODO: non ha senso che i parametri siano Nullable
    //TODO: utilizzre AsyncFileWriter?
    public static boolean save(@Nullable final String name, @Nullable final SchemStructure structure) {
        if (name == null || structure == null)
            return false;

        final File schemFile = new File(CoM.getInstance().getDataFolder() + "/Schematics", name + ".schem");
        if (!schemFile.exists())
            FileUtil.createFile(schemFile);

        try {
            final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(schemFile));
            oos.writeObject(structure);
            oos.close();

            return true;
        } catch (final Exception e) {
            e.printStackTrace();

            return false;
        }
    }


    public enum Schematics {
        ISLAND("Island"), GOLD_EXTRACTOR("GoldExtractor"), ELIXIR_EXTRACTOR("ElixirExtractor"), ARCHER("ArcherTower");


        private final String name;

        Schematics(@Nonnull final String name) {
            this.name = name;
        }

        @Nonnull
        public String getName() {
            return this.name;
        }
    }

    public static class WorldEdit {

        private WorldEdit() {
            throw new IllegalAccessError();
        }

        /**
         * Obtain {@code CuboidClipboard} from specified file
         *
         * @param schematic - is the schematic from which will be got {@code CuboidClipboard}
         * @return {@code CuboidClipboard} obtained from specified file
         * {@code Null} if schematic is {@code null} or if an exception has occurred
         * @since v2.0
         */
        //TODO: non ha senso che i parametri siano Nullable?
        @Nullable
        public static Clipboard getClipboard(@Nullable final Schematics schematic) {
            if (schematic == null)
                return null;

            final File schematicFile =  CoM.getInstance().getSchematicHandler().getSchematicFile(schematic, ".schematic");
            if (!schematicFile.exists()) //TODO: gestire il caso null
                FileUtil.copyFile(schematic.getName() + ".schematic", schematicFile);

            final ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            if (format == null) {
                //TODO: se il formato non è corretto? Invio sicuramente un messaggio di errore in console ma poi? Anche all'utente? devo poi chiaramente anche annullar l'operazione
                return null;
            }

            try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
                return reader.read();
            } catch (final IOException ignored) {
                //TODO: non faccio nulla? Non invio alert né annullo operazioni?
                return null;
            }
        }

        /**
         * Paste the schematic from the specified position, facing north-east
         *
         * @param clipboard - is the {@code Clipboard} of schematic.
         * @param world     - is the world in which to paste the schematic.
         * @param origin    - is the {@code Location} where schematic should be past.
         * @since v2.0
         */
        //TODO: prendere spunto dai builder di WE (EditSessionBuilder & PasteBuilder)
        //TODO: prendere spunto da Vector3 e Vector2 di WE
        //TODO: prendere spunto da BukkitAdapter di WE (per passare dalle mie implementazioni a quelle di WE o a quelle di Bukkit)
        static void paste(@Nonnull final Clipboard clipboard, @Nonnull final World world, @Nonnull final Vector origin) throws PastingException {
            final Vector adjustedOrigin = getPasteLocation(clipboard, origin);

            try (EditSession editSession = com.sk89q.worldedit.WorldEdit.getInstance().newEditSession(new BukkitWorld(world))) {
                final Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(adjustedOrigin.getX(), adjustedOrigin.getY(), adjustedOrigin.getZ())).copyEntities(true).ignoreAirBlocks(false).build();
                Operations.complete(operation);
            } catch (final Exception e) {
                e.printStackTrace();

                throw new PastingException("Exception generated by WorldEdit");
            }


            /*TODO
             * https://github.com/Boomclaw/MineResetLite/blob/master/src/main/java/com/koletar/jj/mineresetlite/commands/MineCommands.java#L112
             * https://github.com/EngineHub/WorldEdit/blob/master/worldedit-core/src/main/java/com/sk89q/worldedit/command/ClipboardCommands.java#L145
             */
        }

        /**
         * Returns the position where the schematic is to be pasted so that it faces north-east
         * and upwards from the origin.
         * In other words, this method ensures that the schematic is always placed in the same direction.
         * If the {@code Location} origin were used directly to paste the schematic, it would be
         * positioned from the origin, but in an unpredictable direction that would depend only on
         * the position the player had when he copied/saved the schematic.
         * <p>
         * Example:
         * when the schematic was copied/saved, the player was north-east of the structure
         * (the construction was therefore south-west of the player).
         * If you paste in the origin without using this adjusted position, the schematic will be pasted
         * in the south-west direction, instead of always in the usual direction.
         * This may cause problems with overlapping islands.
         *
         * @param clipboard is the {@code Clipboard} of schematic.
         * @param origin    is the {@code Vector} where schematic should be past.
         * @return the new position where the schematic is to be pasted.
         * @since v3.0
         */
        @Nonnull
        private static Vector getPasteLocation(@Nonnull final Clipboard clipboard, @Nonnull final Vector origin) {
            final Vector adjustedLocation = new Vector(origin);

            final Region region = clipboard.getRegion();
            final BlockVector3 minimumPoint = region.getMinimumPoint();

            // Offset indicates the position the player had when he copied the schematic
            final BlockVector3 offset = minimumPoint.subtract(clipboard.getOrigin());

            adjustedLocation.subtract(offset.getBlockX(), offset.getBlockY(), offset.getBlockZ()); // North-west corner
            adjustedLocation.subtract(0, 0, region.getMaximumPoint().subtract(minimumPoint).getBlockZ()); // South-west corner

            /*TODO: rimuovere se inutili
            final com.sk89q.worldedit.Vector min = BukkitUtil.toVector(origin.toBukkitLoc()).add(offset);
            final com.sk89q.worldedit.Vector max = min.add(clipboard.getRegion().getMaximumPoint().subtract(clipboard.getRegion().getMinimumPoint()));
            final com.sk89q.worldedit.Vector northEast = min.add(0, 0, clipboard.getRegion().getMaximumPoint().subtract(clipboard.getRegion().getMinimumPoint()).getZ());*/

            return adjustedLocation;
        }
    }
}