/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.world.v1_16_R1;

import com.gmail.davideblade99.clashofminecrafters.world.BaseEmptyWorldGenerator;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Random;

public final class EmptyWorldGenerator extends BaseEmptyWorldGenerator {

    @Override
    public ChunkData generateChunkData(final World world, final Random r, final int chunkX, final int chunkZ, final BiomeGrid biomeGrid) {
        final ChunkData emptyChunk = super.createChunkData(world);

        for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++)
                for (int y = 0; y < emptyChunk.getMaxHeight(); y++)
                    biomeGrid.setBiome(x, y, z, Biome.PLAINS);

        return emptyChunk;
    }
}
