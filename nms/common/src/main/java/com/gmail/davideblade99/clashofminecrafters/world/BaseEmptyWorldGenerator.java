/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BaseEmptyWorldGenerator extends ChunkGenerator {

    public BaseEmptyWorldGenerator() {
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