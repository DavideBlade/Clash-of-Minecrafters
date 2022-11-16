/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Island;
import com.gmail.davideblade99.clashofminecrafters.setting.Configuration;
import com.gmail.davideblade99.clashofminecrafters.yaml.IslandConfiguration;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.exception.WorldBorderReachedException;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import com.gmail.davideblade99.clashofminecrafters.util.SchematicUtil;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Size2D;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import javax.annotation.Nonnull;
import java.io.File;

public final class IslandHandler {

    private final static short DISTANCE_BETWEEN_ISLANDS = 256; // For both x and z

    // World borders
    public final static int MIN_X = 29999983;
    public final static int MIN_Z = 29999983;
    private final static int MAX_X = -MIN_X;
    private final static int MAX_Z = -MIN_Z;


    private final Configuration config;
    private final int expansions;

    public final File islandDataFile;

    public IslandHandler(@Nonnull final CoM plugin) {
        this.config = plugin.getConfig();
        this.expansions = config.getMaxExpansions() * 16;
        this.islandDataFile = new File(plugin.getDataFolder(), "island data.yml");

        // Setup storage
        if (!islandDataFile.exists()) {
            FileUtil.createFile(islandDataFile);

            final IslandConfiguration islandConfiguration = new IslandConfiguration(islandDataFile);
            islandConfiguration.setX(IslandHandler.MIN_X);
            islandConfiguration.setZ(IslandHandler.MIN_Z);
            islandConfiguration.save();
        }
    }

    @Nonnull
    public Island generateIsland(@Nonnull final OfflinePlayer player) throws PastingException, WorldBorderReachedException {
        final Schematic schematic;
        if (config.useIslandSchematic())
            schematic = new Schematic(Schematic.WorldEdit.getClipboard(Schematic.Schematics.ISLAND));
        else
            schematic = new Schematic(Schematic.load(Schematic.Schematics.ISLAND));

        final IslandConfiguration islandStorage = new IslandConfiguration(islandDataFile);
        int x = islandStorage.getX();
        int z = islandStorage.getZ();

        // I move left for the width of the schematic and forward for expansions along z
        final Vector origin = new Vector(x, 65, z).subtract((schematic.getSize().getWidth() - 1) + expansions, 0, expansions);
        final World world = Bukkit.getWorld("Islands");

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

        schematic.paste(world, origin);
        x = origin.getX() - expansions - DISTANCE_BETWEEN_ISLANDS;

        final Location spawn = SchematicUtil.getSpawnLocation(schematic, world);
        if (spawn == null)
            throw new PastingException();

        // Create island
        final Island island = new Island(player.getName(), spawn, origin, new Size2D(schematic.getSize()), new Size2D(this.expansions));

        // Save on file
        islandStorage.setX(x);
        islandStorage.setZ(z);
        islandStorage.save();

        return island;
    }
}
