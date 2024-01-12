/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.handler;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.exception.WorldBorderReachedException;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Size2D;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.schematic.Schematic;
import com.gmail.davideblade99.clashofminecrafters.util.FileUtil;
import com.gmail.davideblade99.clashofminecrafters.util.Pair;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NonnullCallback;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
import com.gmail.davideblade99.clashofminecrafters.yaml.IslandConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

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
    public final File villageDataFile;

    public VillageHandler(@Nonnull final CoM plugin) {
        this.plugin = plugin;
        this.expansions = plugin.getConfig().getVillageSettings().maxExpansions * 16;
        this.villageDataFile = new File(plugin.getDataFolder(), "village data.yml");

        // Setup storage
        if (!villageDataFile.exists()) {
            FileUtil.createFile(villageDataFile);

            final IslandConfiguration islandConfiguration = new IslandConfiguration(villageDataFile);
            islandConfiguration.setX(VillageHandler.MIN_X);
            islandConfiguration.setZ(VillageHandler.MIN_Z);
            islandConfiguration.save();
        }
    }

    /**
     * <p>Generates the village for the specified player. The completion of the operation will correspond to the
     * callback invocation.</p>
     * <p>If any exception is thrown, it is passed to the callback received as a parameter.</p>
     *
     * @param player            Player to create island to
     * @param completionHandler Callback that will be invoked when the operation is completed. It will have the
     *                          created village as a parameter if the operation is completed successfully,
     *                          otherwise it will receive the thrown exception. List of possible exceptions:
     *                          <ul>
     *                              <li>{@link WorldBorderReachedException} is thrown if there is no space left for other villages</li>
     *                              <li>{@link PastingException} is thrown in case of error during schematic pasting</li>
     *                          </ol>
     *
     * @since 3.1.4
     */
    public void generateVillage(@Nonnull final OfflinePlayer player, @Nonnull final NonnullCallback<Pair<Village, Exception>> completionHandler) {
        final Schematic schematic;
        try {
            //TODO: metodo nella classe Settings per ottenere VillageSettings (come per i settaggi degli edifici)
            schematic = plugin.getSchematicHandler().getSchematic(plugin.getConfig().getVillageSettings().getRelatedSchematic());
        } catch (final Exception e) { // Pass exceptions to the callback
            completionHandler.call(new Pair<>(null, e));
            return;
        }

        final IslandConfiguration islandStorage = new IslandConfiguration(villageDataFile);
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
            if (origin.getZ() - (schematic.getSize().getLength() - 1) <= MAX_Z) {
                completionHandler.call(new Pair<>(null, new WorldBorderReachedException()));
                return;
            }
        }

        schematic.paste(origin.toBukkitLocation(world), new NullableCallback<PastingException>() {
            @Override
            public void call(@Nullable final PastingException result) {
                final Location spawn = SchematicHandler.getSpawnLocation(schematic);
                if (spawn == null) {
                    completionHandler.call(new Pair<>(null, new PastingException()));
                    return;
                }

                // Create village
                final Village village = new Village(player.getName(), spawn, origin, new Size2D(schematic.getSize()), new Size2D(VillageHandler.this.expansions));
                completionHandler.call(new Pair<>(village, null));
            }
        });

        /*
         * Variables are updated regardless, even in case of an error.
         * This is to avoid placing an island near a schematic stump in the event of an error
         */

        // Save on file x and z
        x = origin.getX() - expansions - DISTANCE_BETWEEN_ISLANDS;
        islandStorage.setX(x);
        islandStorage.setZ(z);
        islandStorage.save();
    }

    /**
     * @return The world containing players' villages
     *
     * @since 3.1.2
     */
    public World getVillageWorld() {
        return Bukkit.getWorld(VILLAGE_WORLD_NAME);
    }

    /**
     * @param world World to check
     *
     * @return True if the world passed as the parameter is the village world, otherwise false
     *
     * @since 3.1.2
     */
    public boolean isVillageWorld(@Nonnull final World world) {
        return world.getName().equals(VILLAGE_WORLD_NAME);
    }
}
