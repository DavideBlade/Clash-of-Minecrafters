/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.world;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class responsible for generating empty chunks (air only)
 *
 * @see Server#createWorld(WorldCreator)
 * @see WorldCreator#generator(ChunkGenerator)
 * @since 3.2.2
 */
public class EmptyWorldGenerator extends ChunkGenerator {

    public EmptyWorldGenerator() {
        super();
    }

    @Override
    public final boolean canSpawn(final World world, final int x, final int z) {
        return true;
    }

    @Override
    public final List<BlockPopulator> getDefaultPopulators(final World world) {
        return Collections.emptyList();
    }

    @Override
    public final Location getFixedSpawnLocation(final World world, final Random random) {
        return null;
    }
}
