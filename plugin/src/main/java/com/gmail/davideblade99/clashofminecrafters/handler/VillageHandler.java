/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Village;
import com.gmail.davideblade99.clashofminecrafters.exception.InvalidSchematicFormatException;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.exception.WorldBorderReachedException;
import com.gmail.davideblade99.clashofminecrafters.geometric.Size2D;
import com.gmail.davideblade99.clashofminecrafters.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematics;
import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import com.gmail.davideblade99.clashofminecrafters.yaml.IslandConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class VillageHandler {

    public final static String VILLAGE_WORLD_NAME = "Villages";
    private final static short DISTANCE_BETWEEN_ISLANDS = 256; // For both x and z

    // World borders
    public final static int MIN_X = 29999983;
    public final static int MIN_Z = 29999983;
    private final static int MAX_X = -MIN_X;
    private final static int MAX_Z = -MIN_Z;


    private final CoM plugin;
    private final int expansions;
    public final File islandDataFile;

    public VillageHandler(@Nonnull final CoM plugin) {
        this.plugin = plugin;
        this.expansions = plugin.getConfig().getMaxExpansions() * 16;
        this.islandDataFile = new File(plugin.getDataFolder(), "island data.yml"); //TODO: cambiare in "village data.yml"

        // Setup storage
        if (!islandDataFile.exists()) {
            FileUtil.createFile(islandDataFile);

            final IslandConfiguration islandConfiguration = new IslandConfiguration(islandDataFile);
            islandConfiguration.setX(VillageHandler.MIN_X);
            islandConfiguration.setZ(VillageHandler.MIN_Z);
            islandConfiguration.save();
        }
    }

    @Nonnull
    public Village generateIsland(@Nonnull final OfflinePlayer player) throws PastingException, WorldBorderReachedException {
        final Schematic schematic;
        //TODO
        try {
            schematic = plugin.getSchematicHandler().getSchematic(Schematics.VILLAGE);
        } catch (FileNotFoundException e) {
            return null;
        } catch (InvalidSchematicFormatException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        final IslandConfiguration islandStorage = new IslandConfiguration(islandDataFile);
        int x = islandStorage.getX();
        int z = islandStorage.getZ();

        // I move left for the width of the schematic and forward for expansions along z
        final Vector origin = new Vector(x, 65, z).subtract((schematic.getSize().getWidth() - 1) + expansions, 0, expansions);
        final World world = getVillageWorld();

        // If the island, with its left-hand expansions, exceeds the maximum x
        if (origin.getX() - expansions <= MAX_X) {
            x = MIN_X - (schematic.getSize().getWidth() - 1) - expansions;
            z = origin.getZ() - 2 * expansions - DISTANCE_BETWEEN_ISLANDS;
            origin.setX(x);
            origin.setZ(z);

            // If it exceeds the maximum z
            if (origin.getZ() - (schematic.getSize().getLength() - 1) <= MAX_Z)
                throw new WorldBorderReachedException();
        }

        schematic.paste(origin.toBukkitLocation(world));
        x = origin.getX() - expansions - DISTANCE_BETWEEN_ISLANDS;

        final Location spawn = SchematicHandler.getSpawnLocation(schematic);
        if (spawn == null)
            throw new PastingException();

        // Create village
        final Village village = new Village(player.getName(), spawn, origin, new Size2D(schematic.getSize()), new Size2D(this.expansions));

        // Save on file
        islandStorage.setX(x);
        islandStorage.setZ(z);
        islandStorage.save();

        return village;
    }

    /**
     * @return The world containing players' villages
     *
     * @since v3.1.2
     */
    public World getVillageWorld() {
        return Bukkit.getWorld(VILLAGE_WORLD_NAME);
    }

    /**
     * @param world World to check
     *
     * @return True if the world passed as the parameter is the village world, otherwise false
     *
     * @since v3.1.2
     */
    public boolean isVillageWorld(@Nonnull final World world) {
        return world.getName().equals(VILLAGE_WORLD_NAME);
    }
}
