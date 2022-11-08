/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.world.v1_19_R1;

import com.gmail.davideblade99.clashofminecrafters.world.BaseEmptyWorldGenerator;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.Collections;
import java.util.List;

public final class EmptyWorldGenerator extends BaseEmptyWorldGenerator {

    @Override
    public BiomeProvider getDefaultBiomeProvider(final WorldInfo worldInfo) {
        return new PlainBiomeProvider();
    }

    private class PlainBiomeProvider extends BiomeProvider {

        @Override
        public Biome getBiome(final WorldInfo worldInfo, final int x, final int y, final int z) {
            return Biome.PLAINS;
        }

        @Override
        public List<Biome> getBiomes(final WorldInfo worldInfo) {
            return Collections.singletonList(Biome.PLAINS);
        }
    }
}
